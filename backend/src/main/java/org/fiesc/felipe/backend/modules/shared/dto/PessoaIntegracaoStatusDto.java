package org.fiesc.felipe.backend.modules.shared.dto;

import java.io.Serializable;

public record PessoaIntegracaoStatusDto(
        String cpf,
        String situacao,
        String mensagem
) implements Serializable {}
