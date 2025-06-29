package org.fiesc.felipe.backend.modules.domain.service.interfaces;

import org.fiesc.felipe.backend.modules.shared.dto.EnderecoDto;
import org.fiesc.felipe.backend.modules.domain.entity.Endereco;
import org.fiesc.felipe.backend.modules.domain.entity.Pessoa;

public interface EnderecoService {
    Endereco salvarOuAtualizar(EnderecoDto dto, Pessoa pessoa);
}
