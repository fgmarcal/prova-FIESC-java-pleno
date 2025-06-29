package org.fiesc.felipe.backend.modules.service;

import org.fiesc.felipe.backend.modules.exceptions.NotFoundException;
import org.fiesc.felipe.backend.modules.model.dto.*;
import org.fiesc.felipe.backend.modules.model.entity.Endereco;
import org.fiesc.felipe.backend.modules.model.entity.Pessoa;
import org.fiesc.felipe.backend.modules.model.enums.SituacaoIntegracao;
import org.fiesc.felipe.backend.modules.queue.producer.PessoaIntegracaoProducer;
import org.fiesc.felipe.backend.modules.repository.PessoaRepository;
import org.fiesc.felipe.backend.modules.service.external.CorreiosIntegrationService;
import org.fiesc.felipe.backend.modules.service.external.PessoaApiClient;
import org.fiesc.felipe.backend.modules.service.implementations.PessoaServiceImpl;
import org.fiesc.felipe.backend.modules.service.interfaces.EnderecoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PessoaServiceImplTest {

    private PessoaRepository pessoaRepository;
    private EnderecoService enderecoService;
    private CorreiosIntegrationService correiosIntegrationService;
    private PessoaIntegracaoProducer pessoaIntegracaoProducer;
    private PessoaApiClient pessoaApiClient;
    private PessoaServiceImpl pessoaService;
    private PessoaRequestDto mockPessoaDto(String cpf) {
        return new PessoaRequestDto(
                "Fulano de Tal", "01/01/1990", cpf, "fulano@email.com",
                new EnderecoDto("88000000", "Rua", 100, "Cidade", "SC")
        );
    }


    @BeforeEach
    void setUp() {
        pessoaRepository = mock(PessoaRepository.class);
        enderecoService = mock(EnderecoService.class);
        correiosIntegrationService = mock(CorreiosIntegrationService.class);
        pessoaIntegracaoProducer = mock(PessoaIntegracaoProducer.class);
        pessoaApiClient = mock(PessoaApiClient.class);

        pessoaService = new PessoaServiceImpl(
                pessoaRepository,
                enderecoService,
                correiosIntegrationService,
                pessoaIntegracaoProducer,
                pessoaApiClient
        );
    }

    @Test
    void deveCriarPessoaComSucessoEEnviarParaFila() {
        // Arrange
        PessoaRequestDto dto = new PessoaRequestDto(
                "João da Silva",
                "01/01/1990",
                "12345678901",
                "joao@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        when(pessoaRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(correiosIntegrationService.buscarEnderecoPorCep("88000000"))
                .thenReturn(new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC"));

        when(enderecoService.salvarOuAtualizar(any(), any())).thenReturn(new Endereco());

        Pessoa pessoaSalva = new Pessoa();
        pessoaSalva.setIdPessoa(1L);
        pessoaSalva.setCpf(dto.cpf());
        pessoaSalva.setNome(dto.nome());
        pessoaSalva.setEmail(dto.email());
        pessoaSalva.setSituacaoIntegracao(SituacaoIntegracao.NAO_ENVIADO.toString());

        when(pessoaRepository.save(any())).thenReturn(pessoaSalva);

        // Act
        PessoaResponseDto response = pessoaService.criar(dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.idPessoa());
        assertEquals("Pessoa cadastrada com sucesso", response.mensagem());

        verify(pessoaRepository, times(2)).save(any()); // 1x salva normal, 1x com PENDENTE
        verify(pessoaIntegracaoProducer).enviarPessoaParaFila(dto);
    }

    @Test
    void deveLancarExcecaoQuandoCpfJaCadastrado() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "João da Silva",
                "01/01/1990",
                "12345678901",
                "joao@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        when(pessoaRepository.existsByCpf("12345678901")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> pessoaService.criar(dto));
        assertEquals("CPF já cadastrado", ex.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoNomeForInvalido() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "João", // apenas um nome
                "01/01/1990",
                "12345678901",
                "joao@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () -> pessoaService.criar(dto));
        assertEquals("Nome deve conter ao menos dois nomes.", ex.getMessage());
    }

    @Test
    void deveAtualizarPessoaComSucessoEEnviarParaFila() {
        // Arrange
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(1L);
        pessoa.setCpf("12345678901");

        PessoaRequestDto dto = new PessoaRequestDto(
                "Maria Silva", "15/05/1985", "12345678901", "maria@email.com",
                new EnderecoDto("88000000", "Rua Nova", 200, "Floripa", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));
        when(enderecoService.salvarOuAtualizar(any(), eq(pessoa))).thenReturn(new Endereco());
        when(pessoaRepository.save(pessoa)).thenReturn(pessoa);

        // Act
        PessoaResponseDto response = pessoaService.atualizar("12345678901", dto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.idPessoa());
        assertEquals("Pessoa atualizada com sucesso", response.mensagem());

        assertEquals(SituacaoIntegracao.PENDENTE.toString(), pessoa.getSituacaoIntegracao());
        verify(pessoaRepository).save(pessoa);
        verify(pessoaIntegracaoProducer).enviarPessoaParaFila(dto);
    }

    @Test
    void deveLancarExcecaoQuandoPessoaNaoEncontradaNaAtualizacao() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "Maria Silva", "15/05/1985", "12345678901", "maria@email.com",
                new EnderecoDto("88000000", "Rua Nova", 200, "Floripa", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pessoaService.atualizar("12345678901", dto));

        assertEquals("Pessoa não encontrada", ex.getMessage());
        verify(pessoaRepository, never()).save(any());
        verify(pessoaIntegracaoProducer, never()).enviarPessoaParaFila(any());
    }

    @Test
    void deveMarcarErroSeFalhaAoEnviarParaFilaNaAtualizacao() {
        // Arrange
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(1L);
        pessoa.setCpf("12345678901");

        PessoaRequestDto dto = new PessoaRequestDto(
                "João Teste", "01/01/1990", "12345678901", "teste@email.com",
                new EnderecoDto("88000000", "Rua B", 100, "Floripa", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));
        when(enderecoService.salvarOuAtualizar(any(), eq(pessoa))).thenReturn(new Endereco());

        doThrow(new RuntimeException("Erro na fila")).when(pessoaIntegracaoProducer).enviarPessoaParaFila(dto);

        // Act
        PessoaResponseDto response = pessoaService.atualizar("12345678901", dto);

        // Assert
        assertNotNull(response);
        assertEquals("Pessoa atualizada com sucesso", response.mensagem());

        // Verifica que a situação foi marcada como ERRO
        assertEquals(SituacaoIntegracao.ERRO.toString(), pessoa.getSituacaoIntegracao());
        verify(pessoaRepository, times(2)).save(pessoa); // PENDENTE, depois ERRO
    }

    @Test
    void deveRemoverPessoaComSucessoQuandoApiRetornaOk() {
        // Arrange
        String cpf = "12345678901";

    when(pessoaApiClient.removerPessoa(cpf)).thenReturn(new PessoaResponseDto(null, ""));

        // Act
        assertDoesNotThrow(() -> pessoaService.remover(cpf));

        // Assert
        verify(pessoaApiClient).removerPessoa(cpf);
        verify(pessoaRepository).deleteByCpf(cpf);
    }

    @Test
    void naoDeveRemoverSeRespostaDaApiForNull() {
        // Arrange
        String cpf = "12345678901";

        when(pessoaApiClient.removerPessoa(cpf)).thenReturn(null);

        // Act
        assertDoesNotThrow(() -> pessoaService.remover(cpf));

        // Assert
        verify(pessoaApiClient).removerPessoa(cpf);
        verify(pessoaRepository, never()).deleteByCpf(cpf);
    }

    @Test
    void deveLancarExcecaoQuandoApiFalharNaRemocao() {
        // Arrange
        String cpf = "12345678901";

        when(pessoaApiClient.removerPessoa(cpf))
                .thenThrow(new RuntimeException("Erro na API externa"));

        // Act
        RuntimeException ex = assertThrows(RuntimeException.class, () -> pessoaService.remover(cpf));
        // Assert
        assertTrue(ex.getMessage().contains("Erro ao remover pessoa na API"));

        verify(pessoaApiClient).removerPessoa(cpf);
        verify(pessoaRepository, never()).deleteByCpf(any());
    }

    @Test
    void deveReenviarIntegracaoSeSituacaoForPendente() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCpf("12345678901");
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.PENDENTE.toString());
        pessoa.setEndereco(new Endereco());

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));
        when(pessoaRepository.save(pessoa)).thenReturn(pessoa);

        ResponseDto response = pessoaService.reenviarIntegracao("12345678901");

        assertEquals("Pessoa enviada para integração com sucesso", response.message());
        assertEquals(SituacaoIntegracao.SUCESSO.toString(), pessoa.getSituacaoIntegracao());

        verify(pessoaRepository, times(2)).save(pessoa);
    }

    @Test
    void deveReenviarIntegracaoSeSituacaoForErro() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCpf("12345678901");
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.ERRO.toString());
        pessoa.setEndereco(new Endereco());

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));

        ResponseDto response = pessoaService.reenviarIntegracao("12345678901");

        assertEquals("Pessoa enviada para integração com sucesso", response.message());
        verify(pessoaRepository, times(2)).save(pessoa);
    }

    @Test
    void deveLancarErroSeSituacaoNaoPermitirReenvio() {
        Pessoa pessoa = new Pessoa();
        pessoa.setCpf("12345678901");
        pessoa.setSituacaoIntegracao(SituacaoIntegracao.SUCESSO.toString());

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> pessoaService.reenviarIntegracao("12345678901"));

        assertEquals("Integração só pode ser reenviada se a situação for Pendente ou Erro", ex.getMessage());
        verify(pessoaRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundExceptionSePessoaNaoExistirAoReenviarIntegracao() {
        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> pessoaService.reenviarIntegracao("12345678901"));
    }

    @Test
    void deveIntegrarPessoaComSucessoViaAtualizacao() {
        PessoaRequestDto dto = mockPessoaDto("12345678901");

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(dto.cpf());

        when(pessoaRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(pessoa));

        when(pessoaApiClient.atualizarPessoa(dto.cpf(), dto)).thenReturn(null);

        ResponseDto response = pessoaService.integrarPessoa(dto);

        assertEquals("Pessoa enviada para atualização com sucesso", response.message());
        assertEquals(SituacaoIntegracao.SUCESSO.toString(), pessoa.getSituacaoIntegracao());

        verify(pessoaRepository).save(pessoa);
        verify(pessoaIntegracaoProducer).enviarStatus(
                new PessoaIntegracaoStatusDto(dto.cpf(), SituacaoIntegracao.SUCESSO.toString(), "Pessoa atualizada com sucesso")
        );
    }

    @Test
    void deveIntegrarPessoaComSucessoViaCriacaoQuandoAtualizacaoFalha() {
        PessoaRequestDto dto = mockPessoaDto("12345678901");

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(dto.cpf());

        when(pessoaRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(pessoa));

        doThrow(new RuntimeException("Erro atualização")).when(pessoaApiClient).atualizarPessoa(dto.cpf(), dto);
        when(pessoaApiClient.criarPessoa(dto)).thenReturn(null);

        ResponseDto response = pessoaService.integrarPessoa(dto);

        assertEquals("Pessoa enviada para criação com sucesso", response.message());
        assertEquals(SituacaoIntegracao.SUCESSO.toString(), pessoa.getSituacaoIntegracao());

        verify(pessoaRepository).save(pessoa);
        verify(pessoaIntegracaoProducer).enviarStatus(
                new PessoaIntegracaoStatusDto(dto.cpf(), SituacaoIntegracao.SUCESSO.toString(), "Pessoa cadastrada com sucesso")
        );
    }

    @Test
    void deveMarcarErroQuandoAtualizacaoECriacaoFalharem() {
        PessoaRequestDto dto = mockPessoaDto("12345678901");

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(dto.cpf());

        when(pessoaRepository.findByCpf(dto.cpf())).thenReturn(Optional.of(pessoa));

        doThrow(new RuntimeException("Erro atualização")).when(pessoaApiClient).atualizarPessoa(dto.cpf(), dto);
        doThrow(new RuntimeException("Erro criação")).when(pessoaApiClient).criarPessoa(dto);

        ResponseDto response = pessoaService.integrarPessoa(dto);

        assertNull(response); 
        assertEquals(SituacaoIntegracao.ERRO.toString(), pessoa.getSituacaoIntegracao());

        verify(pessoaRepository).save(pessoa);
        verify(pessoaIntegracaoProducer).enviarStatus(
                argThat(status ->
                        status.cpf().equals(dto.cpf()) &&
                                status.situacao().equals(SituacaoIntegracao.ERRO.toString()) &&
                                status.mensagem().startsWith("Erro ao cadastrar pessoa"))
        );
    }

    @Test
    void deveListarTodasAsPessoasComDadosMescladosDaApi() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Fulano");
        pessoa.setCpf("12345678901");
        pessoa.setNascimento(LocalDate.of(1990, 1, 1));
        pessoa.setEmail("fulano@email.com");
        pessoa.setSituacaoIntegracao("SUCESSO");

        Endereco endereco = new Endereco();
        endereco.setCep("88000000");
        endereco.setRua("Rua A");
        endereco.setNumero(100);
        endereco.setCidade("Floripa");
        endereco.setEstado("SC");
        pessoa.setEndereco(endereco);

        when(pessoaRepository.findAll()).thenReturn(List.of(pessoa));

        PessoaApiResponseDto apiDto = new PessoaApiResponseDto(
                "Fulano",
                "12345678901",
                "01/01/1990",
                "fulano@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Floripa", "SC"),
                "2024-06-01T10:00:00",
                "2024-06-10T12:00:00",
                "SUCESSO"
        );

        when(pessoaApiClient.listarTodas()).thenReturn(List.of(apiDto));

        List<PessoaApiResponseDto> lista = pessoaService.listarTodos();

        assertEquals(1, lista.size());
        PessoaApiResponseDto result = lista.get(0);
        assertEquals("Fulano", result.nome());
        assertEquals("12345678901", result.cpf());
        assertEquals("01/01/1990", result.nascimento());
        assertEquals("2024-06-01T10:00:00", result.dataHoraInclusao());
        assertEquals("2024-06-10T12:00:00", result.dataHoraAtualizacao());
    }

    @Test
    void deveListarMesmoSeApiFalhar() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Sem API");
        pessoa.setCpf("99999999999");
        pessoa.setNascimento(LocalDate.of(1980, 5, 10));
        pessoa.setEmail("semapi@email.com");
        pessoa.setSituacaoIntegracao("PENDENTE");

        Endereco endereco = new Endereco();
        endereco.setCep("11111111");
        endereco.setRua("Rua X");
        endereco.setNumero(1);
        endereco.setCidade("Cidade");
        endereco.setEstado("PR");
        pessoa.setEndereco(endereco);

        when(pessoaRepository.findAll()).thenReturn(List.of(pessoa));
        when(pessoaApiClient.listarTodas()).thenThrow(new RuntimeException("API offline"));

        List<PessoaApiResponseDto> lista = pessoaService.listarTodos();

        assertEquals(1, lista.size());
        PessoaApiResponseDto result = lista.get(0);
        assertEquals("Sem API", result.nome());
        assertNull(result.dataHoraInclusao()); // não veio da API
    }

    @Test
    void deveConsultarPessoaPorCpfComDadosMesclados() {
        String cpf = "12345678901";

        Pessoa pessoa = new Pessoa();
        pessoa.setCpf(cpf);
        pessoa.setSituacaoIntegracao("SUCESSO");

        when(pessoaRepository.findByCpf(cpf)).thenReturn(Optional.of(pessoa));

        PessoaApiResponseDto apiResponse = new PessoaApiResponseDto(
                "Fulano", cpf, "01/01/1990", "fulano@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Floripa", "SC"),
                "2024-06-01T10:00:00", "2024-06-10T12:00:00", null
        );

        when(pessoaApiClient.consultarPessoaPorCpf(cpf)).thenReturn(apiResponse);

        PessoaApiResponseDto result = pessoaService.consultarPorCpf(cpf);

        assertEquals("Fulano", result.nome());
        assertEquals("SUCESSO", result.status()); // vem do banco
        assertEquals("01/01/1990", result.nascimento());
        assertEquals("Rua A", result.endereco().rua());

        verify(pessoaApiClient).consultarPessoaPorCpf(cpf);
        verify(pessoaRepository).findByCpf(cpf);
    }

    @Test
    void deveLancarNotFoundExceptionSePessoaNaoExistirNoBanco() {
        String cpf = "00000000000";

        PessoaApiResponseDto apiResponse = new PessoaApiResponseDto(
                "Fulano", cpf, "01/01/1990", "email@email.com",
                new EnderecoDto("88000000", "Rua", 1, "Cidade", "SC"),
                null, null, null
        );

        when(pessoaApiClient.consultarPessoaPorCpf(cpf)).thenReturn(apiResponse);
        when(pessoaRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.consultarPorCpf(cpf));
    }

}
