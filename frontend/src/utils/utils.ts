export const addCpfFormat = (cpf: string): string => {
    return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
  };
  
export const removeCpfFormat = (cpf: string): string => {
  return cpf.replace(/\D/g, '');
};