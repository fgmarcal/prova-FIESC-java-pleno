package org.fiesc.felipe.backend.modules.model.dto;

import org.fiesc.felipe.backend.modules.model.enums.SituacaoIntegracao;

import java.io.Serializable;

public record PessoaIntegracaoStatusDto(
        String cpf,
        String situacao,
        String mensagem
) implements Serializable {}
