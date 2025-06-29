export const SituacaoIntegracao = {
    NAO_ENVIADO: 'Não enviado',
    PENDENTE: 'Pendente',
    SUCESSO: 'Sucesso',
    ERRO: 'Erro',
  } as const;
  
  export type SituacaoIntegracaoKey = keyof typeof SituacaoIntegracao;
  export type SituacaoIntegracaoValue = typeof SituacaoIntegracao[SituacaoIntegracaoKey];
  