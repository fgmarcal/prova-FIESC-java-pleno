import type { SituacaoIntegracaoValue } from "../config/integrationStatus";
import type { Endereco } from "./Endereco";

export type Pessoa = {
    nome: string;
    nascimento?: string;
    cpf?: string;
    email?: string;
    endereco: Endereco;
    status: SituacaoIntegracaoValue;
    dataHoraInclusao:string;
    dataHoraAtualizacao?: string;
  };