package org.fiesc.felipe.backend.modules.service.interfaces;

import org.fiesc.felipe.backend.modules.model.dto.EnderecoDto;
import org.fiesc.felipe.backend.modules.model.entity.Endereco;
import org.fiesc.felipe.backend.modules.model.entity.Pessoa;

public interface EnderecoService {
    Endereco salvarOuAtualizar(EnderecoDto dto, Pessoa pessoa);
}
