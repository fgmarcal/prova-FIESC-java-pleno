import { createContext } from "react";
import type { PessoaContextData } from "../types/PessoaContextData";

export const PessoaContext = createContext<PessoaContextData | undefined>(undefined);
