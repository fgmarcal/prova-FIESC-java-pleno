package org.fiesc.felipe.api.modules.infrastructure.external;

import org.fiesc.felipe.api.modules.shared.dto.EnderecoDto;

public interface CorreiosIntegrationService {
    EnderecoDto buscarEnderecoPorCep(String cep);
}
