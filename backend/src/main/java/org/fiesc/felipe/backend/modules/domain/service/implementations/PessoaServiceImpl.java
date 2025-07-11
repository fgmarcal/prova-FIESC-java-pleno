package org.fiesc.felipe.backend.modules.domain.service.implementations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.modules.infrastructure.external.CorreiosIntegrationService;
import org.fiesc.felipe.backend.modules.infrastructure.external.PessoaApiClient;
import org.fiesc.felipe.backend.modules.domain.service.interfaces.EnderecoService;
import org.fiesc.felipe.backend.modules.domain.service.interfaces.PessoaService;
import org.fiesc.felipe.backend.modules.shared.exceptions.NotFoundException;
import org.fiesc.felipe.backend.modules.shared.dto.*;
import org.fiesc.felipe.backend.modules.domain.entity.Pessoa;
import org.fiesc.felipe.backend.modules.shared.enums.SituacaoIntegracao;
import org.fiesc.felipe.backend.modules.infrastructure.queue.producer.PessoaIntegracaoProducer;
import org.fiesc.felipe.backend.modules.infrastructure.repository.PessoaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PessoaServiceImpl implements PessoaService {

    private final PessoaRepository pessoaRepository;
    private final EnderecoService enderecoService;
    private final CorreiosIntegrationService correiosService;
    private final PessoaIntegracaoProducer pessoaIntegracaoProducer;
    private final PessoaApiClient pessoaApiClient;
    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @Override
    @Transactional
    public PessoaResponseDto criar(PessoaRequestDto dto) {
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
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO.toString());
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

        try {
            pessoaSalva.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE.toString());
            pessoaRepository.save(pessoaSalva);
            pessoaIntegracaoProducer.enviarPessoaParaFila(dto);
        } catch (Exception e) {
            log.error("Erro ao enviar pessoa para fila: {}", e.getMessage(), e);
        }

        return new PessoaResponseDto(pessoaSalva.getIdPessoa(), "Pessoa cadastrada com sucesso");
    }

    @Override
    @Transactional
    public PessoaResponseDto atualizar(String cpf, PessoaRequestDto dto) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        preencherDadosPessoa(pessoa, dto);
        pessoa.setEndereco(enderecoService.salvarOuAtualizar(dto.endereco(), pessoa));

        try {
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE.toString());
            pessoaRepository.save(pessoa);
            pessoaIntegracaoProducer.enviarPessoaParaFila(dto);
        } catch (Exception e) {
            log.error("Erro ao enviar pessoa para fila: {}", e.getMessage(), e);
            pessoa.setSituacaoIntegracao(SituacaoIntegracao.ERRO.toString());
            pessoaRepository.save(pessoa);
        }

        return new PessoaResponseDto(pessoa.getIdPessoa(), "Pessoa atualizada com sucesso");
    }

    @Transactional
    @Override
    public void remover(String cpf) {
        try {
            var response = pessoaApiClient.removerPessoa(cpf);
            if(response != null){
               pessoaRepository.deleteByCpf(cpf);
            }
        } catch (Exception e) {
            log.error("Erro ao remover CPF {} na API: {}", cpf, e.getMessage(), e);
            throw new RuntimeException("Erro ao remover pessoa na API: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PessoaApiResponseDto> listarTodos() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Pessoa> pessoas = pessoaRepository.findAll();

        Map<String, PessoaApiResponseDto> dadosApi = new HashMap<>();
        try {
            pessoaApiClient.listarTodas().forEach(apiPessoa -> {
                dadosApi.put(apiPessoa.cpf(), apiPessoa);
            });
        } catch (Exception e) {
            log.info("Erro integrar a lista completa: {}", e.getMessage(), e);
        }
        return pessoas.stream()
                .map(pessoa -> {
                    PessoaApiResponseDto apiPessoa = dadosApi.get(pessoa.getCpf());
                    return new PessoaApiResponseDto(
                            pessoa.getNome(),
                            pessoa.getCpf(),
                            pessoa.getNascimento() != null ? pessoa.getNascimento().format(formatter) : null,
                            pessoa.getEmail(),
                            new EnderecoDto(
                                    pessoa.getEndereco().getCep(),
                                    pessoa.getEndereco().getRua(),
                                    pessoa.getEndereco().getNumero(),
                                    pessoa.getEndereco().getCidade(),
                                    pessoa.getEndereco().getEstado()
                            ),
                            apiPessoa != null ? apiPessoa.dataHoraInclusao() : null,
                            apiPessoa != null ? apiPessoa.dataHoraAtualizacao() : null,
                            pessoa.getSituacaoIntegracao()
                    );
                })
                .toList();
    }


    @Override
    public PessoaApiResponseDto consultarPorCpf(String cpf) {
        var apiResponse = pessoaApiClient.consultarPessoaPorCpf(cpf);
        return mergePessoaData(apiResponse);
    }

    @Override
    @Transactional
    public ResponseDto reenviarIntegracao(String cpf) {
        Pessoa pessoa = pessoaRepository.findByCpf(cpf)
                .orElseThrow(() -> new NotFoundException("Pessoa não encontrada"));

        String situacao = pessoa.getSituacaoIntegracao();
        if (!situacao.equals(SituacaoIntegracao.PENDENTE.toString()) &&
                !situacao.equals(SituacaoIntegracao.ERRO.toString())) {
            throw new RuntimeException("Integração só pode ser reenviada se a situação for Pendente ou Erro");
        }

        PessoaRequestDto dto = mapToDto(pessoa);

        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE.toString());
        pessoaRepository.save(pessoa);

        this.integrarPessoa(dto);
        log.info("Integração reenviada manualmente para CPF {}", cpf);
        return new ResponseDto("Pessoa enviada para integração com sucesso");
    }

    @Override
    @Transactional
    public ResponseDto integrarPessoa(PessoaRequestDto dto) {
        try {
            pessoaApiClient.atualizarPessoa(dto.cpf(), dto);
            atualizarSituacao(dto.cpf(), SituacaoIntegracao.SUCESSO.toString());
            pessoaIntegracaoProducer.enviarStatus(new PessoaIntegracaoStatusDto(
                    dto.cpf(), SituacaoIntegracao.SUCESSO.toString(), "Pessoa atualizada com sucesso"
            ));
            return new ResponseDto("Pessoa enviada para atualização com sucesso");
        } catch (RuntimeException e) {
            try {
                pessoaApiClient.criarPessoa(dto);
                atualizarSituacao(dto.cpf(), SituacaoIntegracao.SUCESSO.toString());
                pessoaIntegracaoProducer.enviarStatus(new PessoaIntegracaoStatusDto(
                        dto.cpf(), SituacaoIntegracao.SUCESSO.toString(), "Pessoa cadastrada com sucesso"
                ));
                return new ResponseDto("Pessoa enviada para criação com sucesso");
            } catch (Exception ex) {
                atualizarSituacao(dto.cpf(), SituacaoIntegracao.ERRO.toString());
                pessoaIntegracaoProducer.enviarStatus(new PessoaIntegracaoStatusDto(
                        dto.cpf(), SituacaoIntegracao.ERRO.toString(), "Erro ao cadastrar pessoa: " + ex.getMessage()
                ));
            }
        } catch (Exception ex) {
            atualizarSituacao(dto.cpf(), SituacaoIntegracao.ERRO.toString());
            pessoaIntegracaoProducer.enviarStatus(new PessoaIntegracaoStatusDto(
                    dto.cpf(), SituacaoIntegracao.ERRO.toString(), "Erro ao atualizar pessoa: " + ex.getMessage()
            ));
        }
        return null;
    }

    private PessoaApiResponseDto mergePessoaData(PessoaApiResponseDto dto) {
        Pessoa pessoa = pessoaRepository.findByCpf(dto.cpf())
                .orElseThrow(()-> new NotFoundException("Pessoa não existe"));
        return new PessoaApiResponseDto(
                dto.nome(),
                dto.cpf(),
                dto.nascimento(),
                dto.email(),
                dto.endereco(),
                dto.dataHoraInclusao(),
                dto.dataHoraAtualizacao(),
                pessoa.getSituacaoIntegracao()
        );
    }

    private void atualizarSituacao(String cpf, String situacao) {
        pessoaRepository.findByCpf(cpf).ifPresent(p -> {
            p.setSituacaoIntegracao(situacao);
            pessoaRepository.save(p);
        });
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
        if (dto.nascimento() != null && !dto.nascimento().isBlank()) {
            pessoa.setNascimento(LocalDate.parse(dto.nascimento(), FORMATADOR));
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
                pessoa.getNascimento() != null ? pessoa.getNascimento().format(FORMATADOR) : null,
                pessoa.getCpf(),
                pessoa.getEmail(),
                enderecoDto
        );
    }
}
