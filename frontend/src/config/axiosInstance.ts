import axios, { AxiosError } from 'axios';
import type { ErrorResponse } from '../types/ErrorResponse';
import { BASE_URL } from './endpoints';

export const api = axios.create({
  baseURL: BASE_URL,
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
