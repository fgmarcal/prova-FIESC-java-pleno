import { Input, Card } from 'antd';

export default function PessoaSearch() {
  return (
    <Card title="Buscar Pessoa" className="w-full mt-4">
      <Input.Search placeholder="Buscar por nome ou CPF" enterButton />
    </Card>
  );
}
