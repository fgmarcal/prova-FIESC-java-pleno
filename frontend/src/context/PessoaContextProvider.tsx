import { useState } from 'react';
import type { ReactNode } from 'react';
import type { Pessoa } from '../types/Pessoa';
import { PessoaContext } from './PessoaContext';

export const PessoaProvider = ({ children }: { children: ReactNode }) => {
  const [pessoaEditando, setPessoaEditando] = useState<Pessoa | null>(null);

  return (
    <PessoaContext.Provider value={{ pessoaEditando, setPessoaEditando }}>
      {children}
    </PessoaContext.Provider>
  );
};


