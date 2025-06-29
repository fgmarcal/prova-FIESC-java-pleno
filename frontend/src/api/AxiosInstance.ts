import axios, { AxiosError } from 'axios';
import type { ErrorResponse } from '../types/ErrorResponse';

export const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
});

api.interceptors.response.use(
  response => response,
  (error: AxiosError<ErrorResponse>) => {
    if (error.response?.data) {
      const err: ErrorResponse = error.response.data;
      return Promise.reject(err);
    }

    return Promise.reject({
      mensagem: 'Erro de conexão com o servidor',
      detalhe: error.message,
      timestamp: new Date().toISOString(),
      status: 0,
    });
  }
);
