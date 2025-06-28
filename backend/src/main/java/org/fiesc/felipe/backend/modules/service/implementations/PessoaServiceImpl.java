package org.fiesc.felipe.backend.modules.service.implementations;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.modules.model.dto.*;
import org.fiesc.felipe.backend.modules.model.entity.Endereco;
import org.fiesc.felipe.backend.modules.model.entity.Pessoa;
import org.fiesc.felipe.backend.modules.queue.PessoaProducer;
import org.fiesc.felipe.backend.modules.repository.PessoaRepository;
import org.fiesc.felipe.backend.modules.service.interfaces.EnderecoService;
import org.fiesc.felipe.backend.modules.service.interfaces.PessoaService;
import org.fiesc.felipe.backend.modules.service.external.CorreiosIntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;
    private final EnderecoService enderecoService;
    private final CorreiosIntegrationService correiosService;
    private final PessoaProducer pessoaProducer;


    @Override
    @Transactional
    public PessoaResponseDto salvar(PessoaRequestDto dto) {
        validarNome(dto.nome());

        if (dto.cpf() != null && pessoaRepository.existsByCpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Pessoa pessoa = new Pessoa();
        preencherDadosPessoa(pessoa, dto);

        EnderecoDto enderecoDto = dto.endereco();

        if (enderecoDto != null && enderecoDto.cep() != null) {
            EnderecoDto viaCep = correiosService.buscarEnderecoPorCep(enderecoDto.cep());
            if (viaCep != null) {
                enderecoDto = new EnderecoDto(
                        viaCep.cep(),
                        viaCep.rua(),
                        enderecoDto.numero(),
                        viaCep.cidade(),
                        viaCep.estado()
                );
            }
        }
        pessoa.setEndereco(enderecoService.salvarOuAtualizar(enderecoDto, pessoa));
        pessoa.setSituacaoIntegracao("Pendente");

        Pessoa pessoaSalva = pessoaRepository.save(pessoa);
        pessoaProducer.enviarPessoaParaFila(dto);

        return new PessoaResponseDto(pessoaSalva.getIdPessoa(), "Pessoa cadastrada com sucesso");
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(String cpf, PessoaRequestDto dto) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        preencherDadosPessoa(pessoa, dto);
        pessoa.setEndereco(enderecoService.salvarOuAtualizar(dto.endereco(), pessoa));

        Pessoa atualizada = pessoaRepository.save(pessoa);
        pessoaProducer.enviarPessoaParaFila(dto);

        return new PessoaResponseDto(atualizada.getIdPessoa(), "Pessoa atualizada com sucesso");
    }

    @Override
    public void remover(String cpf) {
        if (!pessoaRepository.existsByCpf(cpf)) {
            throw new RuntimeException("Pessoa não encontrada");
        }
        pessoaRepository.deleteByCpf(cpf);
    }

    @Override
    public List<PessoaRequestDto> listarTodos() {
        return pessoaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PessoaRequestDto consultarPorCpf(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
        return mapToDto(pessoa);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().split("\\s+").length < 2) {
            throw new RuntimeException("Nome deve conter ao menos dois nomes.");
        }
    }

    private void preencherDadosPessoa(Pessoa pessoa, PessoaRequestDto dto) {
        pessoa.setNome(capitalizeNome(dto.nome()));
        pessoa.setCpf(dto.cpf());
        pessoa.setEmail(dto.email());
        if (dto.dataNascimento() != null) {
            pessoa.setNascimento(LocalDate.parse(dto.dataNascimento()));
        }
    }

    private String capitalizeNome(String nome) {
        return List.of(nome.trim().split("\\s+")).stream()
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    private PessoaRequestDto mapToDto(Pessoa pessoa) {
        EnderecoDto enderecoDto = new EnderecoDto(
                pessoa.getEndereco().getCep(),
                pessoa.getEndereco().getRua(),
                pessoa.getEndereco().getNumero(),
                pessoa.getEndereco().getCidade(),
                pessoa.getEndereco().getEstado()
        );

        return new PessoaRequestDto(
                pessoa.getNome(),
                pessoa.getNascimento() != null ? pessoa.getNascimento().toString() : null,
                pessoa.getCpf(),
                pessoa.getEmail(),
                enderecoDto
        );
    }
}
