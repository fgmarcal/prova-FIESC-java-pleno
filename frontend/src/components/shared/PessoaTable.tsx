import { Table, Card } from 'antd';

export default function PessoaTable() {
  return (
    <Card title="Pessoas Cadastradas" className="w-full mt-4">
      <Table dataSource={[]} columns={[]} />
    </Card>
  );
}
