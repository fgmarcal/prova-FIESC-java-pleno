import PessoaForm from '../shared/form/PessoaForm';
import PessoaSearch from '../shared/search/PessoaSearch';
import PessoaTable from '../shared/table/PessoaTable';

export default function CadastroPessoaPage() {
  return (
    <div className="flex flex-col gap-8 m-10">
      <PessoaForm />
      <PessoaTable />
      <PessoaSearch />
    </div>
  );
}
