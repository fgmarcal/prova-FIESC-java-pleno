import type { SituacaoIntegracaoKey } from "../config/integrationStatus";
import type { Endereco } from "./Endereco";

export type Pessoa = {
    nome: string;
    nascimento?: string;
    cpf?: string;
    email?: string;
    endereco: Endereco;
    status: SituacaoIntegracaoKey;
    dataHoraInclusao:string;
    dataHoraAtualizacao?: string;
  };