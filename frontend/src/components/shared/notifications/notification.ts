import { message } from 'antd';
import type { ErrorResponse } from '../../../types/ErrorResponse';

export const notifySuccess = (msg: string) => {
  message.success(msg, 3);
};

export const notifyWarning = (msg: string) => {
  message.warning(msg, 3);
};

export const notifyError = (error: unknown) => {
  const e = error as Partial<ErrorResponse>;

  if (e.mensagem) {
    const detalhes = e.detalhe ? `\n${e.detalhe}` : '';
    console.error('[API ERROR]', e.mensagem, detalhes);
    message.error(`${e.mensagem}${detalhes}`, 4);
  } else if (error instanceof Error) {
    message.error(error.message, 4);
  } else {
    message.error('Erro inesperado', 4);
  }
};
