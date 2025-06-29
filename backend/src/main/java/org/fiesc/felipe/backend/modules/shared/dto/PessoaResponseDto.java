package org.fiesc.felipe.backend.modules.shared.dto;

import java.io.Serializable;

public record PessoaResponseDto(
        Long idPessoa,
        String mensagem
) implements Serializable {}
