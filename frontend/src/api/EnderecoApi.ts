import axios, { AxiosError } from 'axios';

const BASE_URL = 'http://localhost:8080';

export interface EnderecoResponse {
  cep: string;
  rua: string;
  bairro: string;
  cidade: string;
  estado: string;
}

export class EnderecoApi {
    static async buscaCep(cep: string): Promise<EnderecoResponse> {
      try {
        const { data } = await axios.get<EnderecoResponse>(`${BASE_URL}/endereco/cep/${cep}`);
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
