package org.fiesc.felipe.api.modules.model.dto;

import org.fiesc.felipe.api.modules.model.enums.SituacaoIntegracao;

import java.io.Serializable;

public record PessoaIntegracaoStatusDto(
        String cpf,
        SituacaoIntegracao situacao,
        String mensagem
) implements Serializable {}
