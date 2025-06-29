import type { PessoaRequest } from "../types/PessoaRequest";
import { endpoints } from "../config/endpoints";
import { api } from "../config/axiosInstance";
import type { Pessoa } from "../entities/Pessoa";
import type { ApiResponse } from "../types/Response";

export class PessoaApi {

    static async listar(): Promise<Pessoa[]> {
        const {data} = await api.get<Pessoa[]>(endpoints.pessoa.listar());
        return data;
    }

    static async buscarPorCPF(cpf: string): Promise<Pessoa> {
        const {data} = await api.get<Pessoa>(endpoints.pessoa.buscarPorCPF(cpf));
        return data;
    }

    static async criar(pessoa: PessoaRequest): Promise<ApiResponse> {
        const {data} = await api.post<ApiResponse>(endpoints.pessoa.criar(), pessoa);
        return data;
    }

    static async atualizar(cpf: string, pessoa: PessoaRequest): Promise<ApiResponse> {
        const {data} = await api.put<ApiResponse>(endpoints.pessoa.atualizar(cpf), pessoa);
        return data;
    }

    static async excluir(cpf: string): Promise<void> {
        await api.delete(endpoints.pessoa.excluir(cpf));
    }

    static async reintegrar(cpf: string): Promise<ApiResponse> {
        const {data} = await api.post<ApiResponse>(endpoints.pessoa.reintegrar(cpf));
        return data;
    }
}