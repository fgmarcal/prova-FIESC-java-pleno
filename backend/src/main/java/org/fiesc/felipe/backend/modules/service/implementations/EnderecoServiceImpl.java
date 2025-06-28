package org.fiesc.felipe.backend.modules.service.implementations;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.backend.modules.model.entity.Endereco;
import org.fiesc.felipe.backend.modules.model.entity.Pessoa;
import org.fiesc.felipe.backend.modules.repository.EnderecoRepository;
import org.fiesc.felipe.backend.modules.service.interfaces.EnderecoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnderecoServiceImpl implements EnderecoService {

    private final EnderecoRepository enderecoRepository;

    @Override
    @Transactional
    public Endereco salvarOuAtualizar(EnderecoDto dto, Pessoa pessoa) {
        if (dto == null) {
            return null;
        }

        Endereco endereco = pessoa.getEndereco();

        if (endereco == null) {
            endereco = new Endereco();
            endereco.setPessoa(pessoa);
        }

        endereco.setCep(dto.cep());
        endereco.setRua(dto.rua());
        endereco.setNumero(dto.numero());
        endereco.setCidade(dto.cidade());
        endereco.setEstado(dto.estado());

        return enderecoRepository.save(endereco);
    }
}
