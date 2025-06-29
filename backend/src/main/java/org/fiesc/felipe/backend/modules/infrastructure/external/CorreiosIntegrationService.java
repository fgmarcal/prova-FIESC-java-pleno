package org.fiesc.felipe.backend.modules.infrastructure.external;

import org.fiesc.felipe.backend.modules.shared.dto.EnderecoDto;

public interface CorreiosIntegrationService {
    EnderecoDto buscarEnderecoPorCep(String cep);
}
