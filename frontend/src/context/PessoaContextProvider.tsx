import { useState } from 'react';
import type { ReactNode } from 'react';
import type { PessoaRequest } from '../types/PessoaRequest';
import { PessoaContext } from './PessoaContext';

export const PessoaProvider = ({ children }: { children: ReactNode }) => {
  const [pessoaEditando, setPessoaEditando] = useState<PessoaRequest | null>(null);
  const [editMode, setEditMode] = useState<boolean>(false);
  const [reloadTrigger, setReloadTrigger] = useState(false);


  const triggerReload = () => setReloadTrigger(prev => !prev);


  return (
    <PessoaContext.Provider value={{ 
      pessoaEditando, 
      setPessoaEditando,
      editMode,
      setEditMode,
      reloadTrigger, 
      triggerReload
      }}>
      {children}
    </PessoaContext.Provider>
  );
};


