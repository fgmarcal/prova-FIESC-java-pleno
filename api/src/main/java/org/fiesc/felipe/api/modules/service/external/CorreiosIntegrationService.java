package org.fiesc.felipe.api.modules.service.external;

import org.fiesc.felipe.api.modules.model.dto.EnderecoDto;

public interface CorreiosIntegrationService {
    EnderecoDto buscarEnderecoPorCep(String cep);
}
