import { Card, Table, Button, Space, Popconfirm, Tooltip } from 'antd';
import { EditOutlined, DeleteOutlined, SyncOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { useMemo } from 'react';
import { SituacaoIntegracao, type SituacaoIntegracaoKey } from '../../../config/integrationStatus';
import type { Pessoa } from '../../../types/Pessoa';
import { usePessoa } from '../../../context/usePessoa';

const mockData: Pessoa[] = [
  {
    id: '1',
    nome: 'Fulano da Silva',
    nascimento: '1999-01-01',
    cpf: '00011122233',
    endereco: {
      cep: '80000-000',
      rua: 'Rua Exemplo',
      numero: 123,
      cidade: 'Curitiba',
      estado: 'Paraná',
    },
    status: 'PENDENTE',
  },
  {
    id: '2',
    nome: 'Ciclano de Souza',
    nascimento: '1999-01-01',
    cpf: '11111111111',
    endereco: {
      cep: '80000-000',
      rua: 'Rua Exemplo',
      numero: 123,
      cidade: 'Curitiba',
      estado: 'Paraná',
    },
    status: 'SUCESSO',
  },
  {
    id: '3',
    nome: 'Beltrano Medeiros',
    nascimento: '1999-01-01',
    cpf: '99988844444',
    endereco: {
      cep: '80000-000',
      rua: 'Rua Exemplo',
      numero: 123,
      cidade: 'Curitiba',
      estado: 'Paraná',
    },
    status: 'ERRO',
  },
  {
    id: '4',
    nome: 'Beltrano Medeiros',
    nascimento: '1999-01-01',
    cpf: '99988844444',
    endereco: {
      cep: '80000-000',
      rua: 'Rua Exemplo',
      numero: 123,
      cidade: 'Curitiba',
      estado: 'Paraná',
    },
    status: 'ERRO',
  },
];

const pessoas: Pessoa[] = mockData;

const formatCpf = (cpf: string): string => {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4');
};

const handleReintegrate = (record: Pessoa) => {
  console.log('Reintegrar:', record);
}

const handleDelete = (record: Pessoa) => {
  console.log('Remover:', record);
}

export default function PessoaTable() {
  const { setPessoaEditando } = usePessoa();

  const columns: ColumnsType<Pessoa> = useMemo(() => [
    {
      title: 'Nome',
      dataIndex: 'nome',
    },
    {
      title: 'Nascimento',
      dataIndex: 'nascimento',
      render: (value) => dayjs(value).format('DD/MM/YYYY'),
    },
    {
      title: 'CPF',
      dataIndex: 'cpf',
      render: (cpf) => formatCpf(cpf),
    },
    {
      title: 'Cidade',
      render: (_, record) => `${record.endereco.cidade} / ${record.endereco.estado}`,
    },
    {
      title: 'Situação da Integração',
      dataIndex: 'status',
      render: (status: SituacaoIntegracaoKey) => SituacaoIntegracao[status],
    },
    {
      title: 'Ação',
      key: 'acao',
      render: (_, record) => (
        <Space>
          <Tooltip title="Editar">
            <Button
              icon={<EditOutlined />}
              onClick={() => setPessoaEditando(record)}
            />
          </Tooltip>

          {(record.status === 'PENDENTE' || record.status === 'ERRO') && (
            <Tooltip title="Reintegrar">
              <Button
                icon={<SyncOutlined />}
                onClick={() => handleReintegrate(record)}
              />
            </Tooltip>
          )}

          <Popconfirm
            title="Confirmar remoção?"
            onConfirm={() => {
              handleDelete(record);
            }}
            okText="Sim"
            cancelText="Não"
          >
            <Tooltip title="Remover">
              <Button danger icon={<DeleteOutlined />} />
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ], []);

  return (
    <Card title="Pessoas Cadastradas" className="w-full mt-4">
      <Table
        rowKey="id"
        dataSource={pessoas}
        columns={columns}
        pagination={false}
        scroll={{ y: 200 }}
      />
    </Card>
  );
}
