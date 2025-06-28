package org.fiesc.felipe.api.modules.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaResponseDto;
import org.fiesc.felipe.api.modules.model.entity.Endereco;
import org.fiesc.felipe.api.modules.model.entity.Pessoa;
import org.fiesc.felipe.api.modules.repository.PessoaRepository;
import org.fiesc.felipe.api.modules.service.external.CorreiosIntegrationService;
import org.fiesc.felipe.api.modules.service.interfaces.PessoaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;
    private final CorreiosIntegrationService correiosIntegrationService;

    @Override
    @Transactional
    public void salvarPessoa(PessoaRequestDto dto) {
        if (dto == null || dto.cpf() == null) {
            throw new RuntimeException("CPF da pessoa é obrigatório ou objeto inválido");
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
    public void atualizarPessoa(PessoaRequestDto dto) {
        Pessoa pessoa = pessoaRepository.findByCpf(dto.cpf())
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

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
    public List<PessoaRequestDto> listarTodos() {
        return pessoaRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Override
    public PessoaRequestDto consultarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return mapToDto(pessoa);
    }

    @Override
    @Transactional
    public PessoaResponseDto removerPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        pessoaRepository.delete(pessoa);
        return new PessoaResponseDto(pessoa.getIdPessoa(), "Pessoa removida com sucesso");
    }

    @Override
    public boolean existePorCpf(String cpf) {
        return pessoaRepository.findByCpf(cpf).isPresent();
    }


    private PessoaRequestDto mapToDto(Pessoa pessoa) {
        Endereco endereco = pessoa.getEndereco();
        return new PessoaRequestDto(
                pessoa.getNome(),
                pessoa.getNascimento() != null ? pessoa.getNascimento().toString() : null,
                pessoa.getCpf(),
                pessoa.getEmail(),
                new EnderecoDto(
                        endereco.getCep(),
                        endereco.getRua(),
                        endereco.getNumero(),
                        endereco.getCidade(),
                        endereco.getEstado()
                )
        );
    }

    private void validarDataNascimento(PessoaRequestDto dto, Pessoa pessoa) {
        LocalDate nascimento = LocalDate.parse(dto.dataNascimento());
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
                dto.cpf() == null || dto.email() == null || dto.dataNascimento() == null ||
                dto.endereco() == null ||
                dto.endereco().cep() == null || dto.endereco().rua() == null ||
                dto.endereco().numero() == null || dto.endereco().cidade() == null || dto.endereco().estado() == null) {
            throw new RuntimeException("Todos os campos são obrigatórios");
        }
    }
}
