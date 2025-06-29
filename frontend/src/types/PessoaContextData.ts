import type { PessoaRequest } from "./PessoaRequest";

export interface PessoaContextData {
    pessoaEditando: PessoaRequest | null;
    setPessoaEditando: (pessoa: PessoaRequest | null) => void;
  }