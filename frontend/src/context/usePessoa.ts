import { useContext } from "react";
import type { PessoaContextData } from "../types/PessoaContextData";
import { PessoaContext } from "./PessoaContext";

export const usePessoa = (): PessoaContextData => {
    const context = useContext(PessoaContext);
    if (!context) {
      throw new Error('usePessoa deve ser usado dentro de PessoaProvider');
    }
    return context;
  };