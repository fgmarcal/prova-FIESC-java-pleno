import { Card, Table, Button, Space, Popconfirm, Tooltip } from 'antd';
import { EditOutlined, DeleteOutlined, SyncOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { useEffect, useMemo, useState } from 'react';
import { SituacaoIntegracao, type SituacaoIntegracaoKey } from '../../../config/integrationStatus';
import { usePessoa } from '../../../context/usePessoa';
import type { Pessoa } from '../../../entities/Pessoa';
import { PessoaApi } from '../../../api/PessoaApi';
import { formatCpf } from '../../../utils/utils';


export default function PessoaTable() {
  const { setPessoaEditando } = usePessoa();
  const [pessoas, setPessoas] = useState<Pessoa[]>([]);

  const handleReintegrate = (record: Pessoa) => {
    console.log('Reintegrar:', record);
  }
  
  const handleDelete = (record: Pessoa) => {
    console.log('Remover:', record);
  }
  
  const loadTableData = async () => {
    const response = await PessoaApi.listar();
    setPessoas(response);
  };
  

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
  ], [setPessoaEditando]);

  useEffect(() => {
    loadTableData();
  },[]);

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
