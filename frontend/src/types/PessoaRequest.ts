import type { Endereco } from "../entities/Endereco";

export type PessoaRequest = {
    nome: string;
    nascimento?: string;
    cpf?: string;
    email?: string;
    endereco: Endereco;
  };