package org.fiesc.felipe.backend.modules.model.dto;

import java.io.Serializable;

public record PessoaResponseDto(
        Long idPessoa,
        String mensagem
) implements Serializable {}
