package org.fiesc.felipe.api.modules.service;

import org.fiesc.felipe.api.modules.exceptions.NotFoundException;
import org.fiesc.felipe.api.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.entity.Endereco;
import org.fiesc.felipe.api.modules.model.entity.Pessoa;
import org.fiesc.felipe.api.modules.repository.PessoaRepository;
import org.fiesc.felipe.api.modules.service.external.CorreiosIntegrationService;
import org.fiesc.felipe.api.modules.service.implementation.PessoaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PessoaServiceImplTest {

    private PessoaRepository pessoaRepository;
    private CorreiosIntegrationService correiosService;
    private PessoaServiceImpl pessoaService;

    @BeforeEach
    void setUp() {
        pessoaRepository = mock(PessoaRepository.class);
        correiosService = mock(CorreiosIntegrationService.class);
        pessoaService = new PessoaServiceImpl(pessoaRepository, correiosService);
    }

    @Test
    void deveCriarPessoaComSucesso() {
        // Arrange
        PessoaRequestDto dto = new PessoaRequestDto(
                "João Silva", "01/01/1990", "12345678901", "joao@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.empty());

        EnderecoDto enderecoDto = new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC");
        when(correiosService.buscarEnderecoPorCep("88000000")).thenReturn(enderecoDto);

        // Act
        pessoaService.criarPessoa(dto);

        // Assert
        ArgumentCaptor<Pessoa> captor = ArgumentCaptor.forClass(Pessoa.class);
        verify(pessoaRepository, times(1)).save(captor.capture());

        Pessoa salva = captor.getValue();
        assertEquals("João Silva", salva.getNome());
        assertEquals("12345678901", salva.getCpf());
        assertEquals("joao@email.com", salva.getEmail());
        assertEquals("Rua A", salva.getEndereco().getRua());
    }

    @Test
    void deveLancarErroQuandoCpfJaExiste() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "João Silva", "01/01/1990", "12345678901", "joao@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(new Pessoa()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(dto));
        assertEquals("Pessoa já existe", ex.getMessage());
    }

    @Test
    void deveLancarErroQuandoDataNascimentoFutura() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "Maria", "31/12/2999", "123456789", "maria@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC")
        );

        when(pessoaRepository.findByCpf("12345678900")).thenReturn(Optional.empty());
        when(correiosService.buscarEnderecoPorCep("88000000")).thenReturn(
                new EnderecoDto("88000000", "Rua A", 100, "Florianópolis", "SC"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> pessoaService.criarPessoa(dto));
        assertEquals("Data de nascimento não pode ser futura", ex.getMessage());
    }

    @Test
    void deveAtualizarPessoaComSucesso() throws NotFoundException {
        Pessoa pessoaExistente = new Pessoa();
        pessoaExistente.setEndereco(new Endereco());

        PessoaRequestDto dto = new PessoaRequestDto(
                "João Atualizado", "10/10/1980", "12345678901", "joao@novo.com",
                new EnderecoDto("88000000", "Rua Nova", 200, "Florianópolis", "SC")
        );

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoaExistente));
        when(correiosService.buscarEnderecoPorCep("88000000"))
                .thenReturn(new EnderecoDto("88000000", "Rua Nova", 200, "Florianópolis", "SC"));

        pessoaService.atualizarPessoa(dto);

        assertEquals("João Atualizado", pessoaExistente.getNome());
        assertEquals("12345678901", pessoaExistente.getCpf());
        assertEquals("joao@novo.com", pessoaExistente.getEmail());
        assertEquals("Rua Nova", pessoaExistente.getEndereco().getRua());
        verify(pessoaRepository, times(1)).save(pessoaExistente);
    }

    @Test
    void deveLancarErroAoAtualizarPessoaInexistente() {
        PessoaRequestDto dto = new PessoaRequestDto(
                "Não Existe", "01/01/2000", "00000000000", "email@email.com",
                new EnderecoDto("88000000", "Rua A", 100, "Cidade", "SC")
        );

        when(pessoaRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.atualizarPessoa(dto));
    }

    @Test
    void deveConsultarPessoaPorCpfComSucesso() throws NotFoundException {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Fulano");
        pessoa.setCpf("12345678901");
        pessoa.setNascimento(LocalDate.of(1990, 1, 1));
        pessoa.setEmail("fulano@email.com");

        Endereco endereco = new Endereco();
        endereco.setCep("88000000");
        endereco.setRua("Rua Teste");
        endereco.setNumero(100);
        endereco.setCidade("Cidade");
        endereco.setEstado("SC");

        pessoa.setEndereco(endereco);

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));

        var result = pessoaService.consultarPorCpf("12345678901");

        assertEquals("Fulano", result.nome());
        assertEquals("12345678901", result.cpf());
        assertEquals("01/01/1990", result.nascimento());
        assertEquals("Rua Teste", result.endereco().rua());
    }

    @Test
    void deveLancarErroAoConsultarPessoaInexistente() {
        when(pessoaRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.consultarPorCpf("00000000000"));
    }

    @Test
    void deveRemoverPessoaPorCpfComSucesso() throws NotFoundException {
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(1L);
        pessoa.setCpf("12345678901");

        when(pessoaRepository.findByCpf("12345678901")).thenReturn(Optional.of(pessoa));

        var response = pessoaService.removerPorCpf("12345678901");

        verify(pessoaRepository).delete(pessoa);
        assertEquals(1L, response.idPessoa());
        assertEquals("Pessoa removida com sucesso", response.mensagem());
    }

    @Test
    void deveLancarErroAoRemoverPessoaInexistente() {
        when(pessoaRepository.findByCpf("00000000000")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pessoaService.removerPorCpf("00000000000"));
    }

    @Test
    void deveListarTodasAsPessoas() {
        Pessoa p1 = new Pessoa();
        p1.setNome("A");
        p1.setCpf("1");
        p1.setNascimento(LocalDate.of(1990, 1, 1));
        p1.setEmail("a@email.com");

        Endereco e1 = new Endereco();
        e1.setCep("111");
        e1.setRua("Rua A");
        e1.setNumero(10);
        e1.setCidade("Cidade A");
        e1.setEstado("SC");
        p1.setEndereco(e1);

        Pessoa p2 = new Pessoa();
        p2.setNome("B");
        p2.setCpf("2");
        p2.setNascimento(LocalDate.of(1980, 2, 2));
        p2.setEmail("b@email.com");

        Endereco e2 = new Endereco();
        e2.setCep("222");
        e2.setRua("Rua B");
        e2.setNumero(20);
        e2.setCidade("Cidade B");
        e2.setEstado("PR");
        p2.setEndereco(e2);

        when(pessoaRepository.findAll()).thenReturn(java.util.List.of(p1, p2));

        var lista = pessoaService.listarTodos();
        assertEquals(2, lista.size());
        assertEquals("A", lista.get(0).nome());
        assertEquals("B", lista.get(1).nome());
    }

}
