import axios, { AxiosError } from 'axios';
import type { EnderecoResponse } from '../types/EnderecoResponse';
import { endpoints } from '../config/endpoints';

export class EnderecoApi {
    static async buscaCep(cep: string): Promise<EnderecoResponse> {
      try {
        const { data } = await axios.get<EnderecoResponse>(endpoints.endereco.buscaCep(cep));
        return data;
      } catch (err) {
        const error = err as AxiosError;
  
        if (error.response?.status === 404) {
          throw new AxiosError('CEP não encontrado');
        }
  
        throw new Error(
          error.response?.data
            ? `Erro ao buscar endereço: ${JSON.stringify(error.response.data)}`
            : 'Erro ao buscar endereço'
        );
      }
    }
  }
