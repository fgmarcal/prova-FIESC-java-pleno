package org.fiesc.felipe.backend.modules.model.enums;

public enum SituacaoIntegracao {
    NAO_ENVIADO("Não enviado"),
    PENDENTE("Pendente"),
    SUCESSO("Sucesso"),
    ERRO("Erro");

    private final String descricao;

    SituacaoIntegracao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
