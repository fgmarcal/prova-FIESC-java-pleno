import type { SituacaoIntegracaoKey } from "../config/integrationStatus";
import type { Endereco } from "./Endereco";

export type Pessoa = {
    id: string;
    nome: string;
    nascimento?: string;
    cpf?: string;
    email?: string;
    endereco: Endereco;
    status: SituacaoIntegracaoKey;
  };