const BASE_URL = 'http://localhost:8080';

export const endpoints = {
    endereco:{
        buscaCep: (cep: string) => `${BASE_URL}/endereco/cep/${cep}`,
    }
}