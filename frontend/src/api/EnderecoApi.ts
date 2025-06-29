import type { EnderecoResponse } from '../types/EnderecoResponse';
import { endpoints } from '../config/endpoints';
import { api } from '../config/axiosInstance';

export class EnderecoApi {
    static async buscaCep(cep: string): Promise<EnderecoResponse> {
        const { data } = await api.get<EnderecoResponse>(endpoints.endereco.buscaCep(cep));
        return data;
    }
  }
