package org.fiesc.felipe.api.modules.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.api.modules.model.entity.Endereco;
import org.fiesc.felipe.api.modules.model.entity.Pessoa;
import org.fiesc.felipe.api.modules.repository.PessoaRepository;
import org.fiesc.felipe.api.modules.repository.EnderecoRepository;
import org.fiesc.felipe.api.modules.service.external.CorreiosIntegrationService;
import org.fiesc.felipe.api.modules.service.interfaces.PessoaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
            throw new RuntimeException("Pessoa inválida");
        }

        if (pessoaRepository.findByCpf(dto.cpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }

        validaCamposObrigatorios(dto);

        Pessoa pessoa = new Pessoa();
        pessoa.setNome(dto.nome());
        pessoa.setCpf(dto.cpf());
        pessoa.setEmail(dto.email());

        LocalDate nascimento = LocalDate.parse(dto.dataNascimento());
        if (nascimento.isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de nascimento não pode ser futura");
        }
        pessoa.setNascimento(nascimento);

        // Validar CEP externo (via Correios)
        EnderecoDto enderecoValido = correiosIntegrationService.buscarEnderecoPorCep(dto.endereco().cep());
        if (enderecoValido == null) {
            throw new RuntimeException("CEP inválido ou não encontrado");
        }

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

    private void validaCamposObrigatorios(PessoaRequestDto dto) {
        if (dto.nome() == null || dto.nome().isBlank() ||
                dto.cpf() == null || dto.email() == null || dto.dataNascimento() == null ||
                dto.endereco() == null ||
                dto.endereco().cep() == null || dto.endereco().rua() == null ||
                dto.endereco().numero() == null || dto.endereco().cidade() == null || dto.endereco().estado() == null) {
            throw new RuntimeException("Todos os campos são obrigatórios");
        }
    }
}
