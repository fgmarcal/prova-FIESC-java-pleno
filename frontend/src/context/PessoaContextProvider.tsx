import { useState } from 'react';
import type { ReactNode } from 'react';
import type { PessoaRequest } from '../types/PessoaRequest';
import { PessoaContext } from './PessoaContext';

export const PessoaProvider = ({ children }: { children: ReactNode }) => {
  const [pessoaEditando, setPessoaEditando] = useState<PessoaRequest | null>(null);

  return (
    <PessoaContext.Provider value={{ pessoaEditando, setPessoaEditando }}>
      {children}
    </PessoaContext.Provider>
  );
};


