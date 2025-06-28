package org.fiesc.felipe.api.modules.model.dto;

import java.io.Serializable;

public record EnderecoDto(
        String cep,
        String rua,
        Integer numero,
        String cidade,
        String estado
) implements Serializable {}
