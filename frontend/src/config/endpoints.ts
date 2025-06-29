import { loadBaseURL } from "./loadEnv";

export const BASE_URL = loadBaseURL();

const ENDERECO = BASE_URL + '/endereco';
const PESSOA = BASE_URL + '/pessoa';

export const endpoints = {
    endereco:{
        buscaCep: (cep: string) => `${ENDERECO}/cep/${cep}`,
    },
    pessoa:{
        listar: () => `${PESSOA}/all`,
        buscarPorCPF: (cpf: string) => `${PESSOA}/cpf/${cpf}`,
        criar: () => `${PESSOA}`,
        atualizar: (cpf: string) => `${PESSOA}/cpf/${cpf}`,
        excluir: (cpf: string) => `${PESSOA}/cpf/${cpf}`,
        reintegrar: (cpf: string) => `${PESSOA}/integrar/${cpf}`,
    }
}