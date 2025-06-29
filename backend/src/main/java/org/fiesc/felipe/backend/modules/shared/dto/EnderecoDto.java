package org.fiesc.felipe.backend.modules.shared.dto;

public record EnderecoDto(
        String cep,
        String rua,
        Integer numero,
        String cidade,
        String estado
) {}
