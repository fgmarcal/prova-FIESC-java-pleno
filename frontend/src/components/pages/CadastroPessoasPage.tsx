import PessoaForm from '../shared/PessoaForm';
import PessoaSearch from '../shared/PessoaSearch';
import PessoaTable from '../shared/PessoaTable';

export default function CadastroPessoaPage() {
  return (
    <div className="flex flex-col gap-8">
      <PessoaForm />
      <PessoaTable />
      <PessoaSearch />
    </div>
  );
}
