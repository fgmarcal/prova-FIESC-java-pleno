import type { PessoaRequest } from "./PessoaRequest";

export interface PessoaContextData {
    pessoaEditando: PessoaRequest | null;
    setPessoaEditando: (pessoa: PessoaRequest | null) => void;
    editMode: boolean;
    setEditMode: (editMode: boolean) => void;
    reloadTrigger: boolean;
    triggerReload: () => void;
  }