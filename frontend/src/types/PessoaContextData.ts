import type { Pessoa } from "./Pessoa";

export interface PessoaContextData {
    pessoaEditando: Pessoa | null;
    setPessoaEditando: (pessoa: Pessoa | null) => void;
  }