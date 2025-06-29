import type { Dayjs } from "dayjs";

export interface PessoaFormData {
    nome: string;
    nascimento?: Dayjs|string|null;
    cpf?: string;
    email?: string;
    cep?: string;
    numero?: number;
    rua?: string;
    cidade?: string;
    estado?: string;
  }