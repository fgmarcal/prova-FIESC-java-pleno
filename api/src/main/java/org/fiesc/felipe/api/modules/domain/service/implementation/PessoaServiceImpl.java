package org.fiesc.felipe.api.modules.domain.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.api.modules.shared.exceptions.NotFoundException;
import org.fiesc.felipe.api.modules.shared.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.shared.dto.EnderecoDto;
import org.fiesc.felipe.api.modules.shared.dto.PessoaResponseDto;
import org.fiesc.felipe.api.modules.shared.dto.ResponseDto;
import org.fiesc.felipe.api.modules.domain.entity.Endereco;
import org.fiesc.felipe.api.modules.domain.entity.Pessoa;
import org.fiesc.felipe.api.modules.infrastructure.repository.PessoaRepository;
import org.fiesc.felipe.api.modules.infrastructure.external.CorreiosIntegrationService;
import org.fiesc.felipe.api.modules.domain.service.interfaces.PessoaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;
    private final CorreiosIntegrationService correiosIntegrationService;
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @Override
    @Transactional
    public void criarPessoa(PessoaRequestDto dto) {
        if (dto == null || dto.cpf() == null) {
            throw new RuntimeException("CPF da pessoa é obrigatório ou objeto inválido");
        }
        if (existePorCpf(dto.cpf())) {
            throw new RuntimeException("Pessoa já existe");
        }
        validarCamposObrigatorios(dto);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setCpf(dto.cpf());
        pessoa.setEmail(dto.email());

        validarDataNascimento(dto, pessoa);

        EnderecoDto enderecoValido = getEnderecoValido(dto);

        Endereco endereco = new Endereco();
        endereco.setCep(enderecoValido.cep());
        endereco.setRua(enderecoValido.rua());
        endereco.setNumero(dto.endereco().numero());
        endereco.setCidade(enderecoValido.cidade());
        endereco.setEstado(enderecoValido.estado());

        endereco.setPessoa(pessoa);
        pessoa.setEndereco(endereco);
        pessoaRepository.save(pessoa);
    }

    @Override
    @Transactional
    public void atualizarPessoa(PessoaRequestDto dto) throws NotFoundException {
        Pessoa pessoa = pessoaRepository.findByCpf(dto.cpf())
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        validarCamposObrigatorios(dto);

        pessoa.setNome(dto.nome());
        pessoa.setCpf(dto.cpf());
        pessoa.setEmail(dto.email());
        validarDataNascimento(dto, pessoa);

        EnderecoDto enderecoValido = getEnderecoValido(dto);

        Endereco endereco = pessoa.getEndereco();
        if (endereco == null) {
            endereco = new Endereco();
            endereco.setPessoa(pessoa);
        }

        endereco.setCep(enderecoValido.cep());
        endereco.setRua(enderecoValido.rua());
        endereco.setNumero(dto.endereco().numero());
        endereco.setCidade(enderecoValido.cidade());
        endereco.setEstado(enderecoValido.estado());

        pessoa.setEndereco(endereco);

        pessoaRepository.save(pessoa);
    }

    @Override
    public List<PessoaResponseDto> listarTodos() {
        return pessoaRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public PessoaResponseDto consultarPorCpf(String cpf) throws NotFoundException {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));
        return mapToDto(pessoa);
    }

    @Override
    @Transactional
    public ResponseDto removerPorCpf(String cpf) throws NotFoundException {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        pessoaRepository.delete(pessoa);
        return new ResponseDto(pessoa.getIdPessoa(), "Pessoa removida com sucesso");
    }

    private boolean existePorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf).isPresent();
    }


    private PessoaResponseDto mapToDto(Pessoa pessoa) {
        Endereco endereco = pessoa.getEndereco();
        return new PessoaResponseDto(
                pessoa.getNome(),
                pessoa.getCpf(),
                pessoa.getNascimento() != null ? pessoa.getNascimento().format(FORMATADOR) : null,
                pessoa.getEmail(),
                new EnderecoDto(
                        endereco.getCep(),
                        endereco.getRua(),
                        endereco.getNumero(),
                        endereco.getCidade(),
                        endereco.getEstado()
                ),
                pessoa.getDataHoraInclusaoRegistro() != null ? pessoa.getDataHoraInclusaoRegistro().toString() : null,
                pessoa.getDataHoraUltimaAlteracaoRegistro() != null ? pessoa.getDataHoraUltimaAlteracaoRegistro().toString() : null
        );
    }

    private void validarDataNascimento(PessoaRequestDto dto, Pessoa pessoa) {
        DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate nascimento = LocalDate.parse(dto.nascimento(), FORMATADOR_DATA);
        if (nascimento.isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de nascimento não pode ser futura");
        }
        pessoa.setNascimento(nascimento);
    }

    private EnderecoDto getEnderecoValido(PessoaRequestDto dto) {
        EnderecoDto enderecoValido = correiosIntegrationService.buscarEnderecoPorCep(dto.endereco().cep());
        if (enderecoValido == null) {
            throw new RuntimeException("CEP inválido ou não encontrado");
        }
        return enderecoValido;
    }

    private void validarCamposObrigatorios(PessoaRequestDto dto) {
        if (dto.nome() == null || dto.nome().isBlank() ||
                dto.cpf() == null || dto.email() == null || dto.nascimento() == null ||
                dto.endereco() == null ||
                dto.endereco().cep() == null || dto.endereco().rua() == null ||
                dto.endereco().numero() == null || dto.endereco().cidade() == null || dto.endereco().estado() == null) {
            throw new RuntimeException("Todos os campos são obrigatórios");
        }
    }
}
