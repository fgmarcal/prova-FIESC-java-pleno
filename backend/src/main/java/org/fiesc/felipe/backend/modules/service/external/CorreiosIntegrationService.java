package org.fiesc.felipe.backend.modules.service.external;

import org.fiesc.felipe.backend.modules.model.dto.EnderecoDto;

public interface CorreiosIntegrationService {
    EnderecoDto buscarEnderecoPorCep(String cep);
}
