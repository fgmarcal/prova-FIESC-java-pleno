import type { Pessoa } from "../types/Pessoa";
import { endpoints } from "../config/endpoints";
import { api } from "../config/axiosInstance";

export class PessoaApi {

    static async listar(): Promise<Pessoa[]> {
        const {data} = await api.get<Pessoa[]>(endpoints.pessoa.listar());
        return data;
    }

    static async buscarPorCPF(cpf: string): Promise<Pessoa> {
        const {data} = await api.get<Pessoa>(endpoints.pessoa.buscarPorCPF(cpf));
        return data;
    }

    static async criar(pessoa: Pessoa): Promise<Pessoa> {
        const {data} = await api.post<Pessoa>(endpoints.pessoa.criar(), pessoa);
        return data;
    }

    static async atualizar(cpf: string, pessoa: Pessoa): Promise<Pessoa> {
        const {data} = await api.put<Pessoa>(endpoints.pessoa.atualizar(cpf), pessoa);
        return data;
    }

    static async excluir(cpf: string): Promise<void> {
        await api.delete(endpoints.pessoa.excluir(cpf));
    }

    static async reintegrar(cpf: string): Promise<Pessoa> {
        const {data} = await api.post<Pessoa>(endpoints.pessoa.reintegrar(cpf));
        return data;
    }
}