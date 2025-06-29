import { Card, Table, Button, Space, Popconfirm, Tooltip } from 'antd';
import { EditOutlined, DeleteOutlined, SyncOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { useCallback, useEffect, useMemo, useState } from 'react';
import { SituacaoIntegracao, type SituacaoIntegracaoValue } from '../../../config/integrationStatus';
import { usePessoa } from '../../../context/usePessoa';
import type { Pessoa } from '../../../entities/Pessoa';
import { PessoaApi } from '../../../api/PessoaApi';
import { addCpfFormat } from '../../../utils/utils';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import dayjs from 'dayjs';
import { notifyError, notifySuccess } from '../notifications/notification';

dayjs.extend(customParseFormat);


export default function PessoaTable() {
  const { setPessoaEditando, setEditMode, reloadTrigger } = usePessoa();
  const [pessoas, setPessoas] = useState<Pessoa[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleReintegrate = useCallback(async (record: Pessoa) => {
    const {cpf} = record;
    try{
      if(cpf){
        setIsLoading(true);
        await PessoaApi.reintegrar(cpf);
        notifySuccess('Sucesso!');
      }else{
        notifyError('CPF não encontrado para reintegração.');
      }
    }catch (error) {
      notifyError(error);
    }
    setIsLoading(false);
  }, []);

  const handleEditMode = useCallback((record: Pessoa) => {
    setPessoaEditando(record);
    setEditMode(true);
  }, [setPessoaEditando, setEditMode]);
  
  const handleDelete = useCallback(async (record: Pessoa) => {
    const {cpf} = record;
    try{
      if(cpf){
        setIsLoading(true);
        await PessoaApi.excluir(cpf);
        setPessoas((prev) => prev.filter((pessoa) => pessoa.cpf !== cpf));
        notifySuccess('Pessoa removida com sucesso!');
      }else{
        notifyError('CPF não encontrado para remoção.');
      }
    }catch (error) {
      notifyError(error);
    }
    setIsLoading(false);
  }, []);
  
  const loadTableData = async () => {
    const response = await PessoaApi.listar();
    setPessoas(response);
  };
  

  const columns: ColumnsType<Pessoa> = useMemo(() => [
    {
      title: 'Nome',
      dataIndex: 'nome',
      key: 'nome',
    },
    {
      title: 'Nascimento',
      dataIndex: 'nascimento',
      render: (value) => value,
      key: 'nascimento',
    },
    {
      title: 'CPF',
      dataIndex: 'cpf',
      render: (cpf) => addCpfFormat(cpf),
      key: 'cpf',
    },
    {
      title: 'Cidade',
      render: (_, record) => `${record.endereco.cidade} / ${record.endereco.estado}`,
      key: 'cidade',
    },
    {
      title: 'Situação da Integração',
      dataIndex: 'status',
      render: (status) => status as SituacaoIntegracaoValue,
      key: 'status',
    },
    {
      title: 'Ação',
      key: 'acao',
      render: (_, record) => (
        <Space>
          <Tooltip title="Editar">
            <Button
              icon={<EditOutlined />}
              onClick={() => handleEditMode(record)}
              disabled={isLoading}
            />
          </Tooltip>

          {(record.status === SituacaoIntegracao.PENDENTE || record.status === SituacaoIntegracao.ERRO) && (
            <Tooltip title="Reintegrar">
              <Button
                icon={<SyncOutlined />}
                onClick={() => handleReintegrate(record)}
                disabled={isLoading}
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
              <Button danger icon={<DeleteOutlined />} disabled={isLoading}/>
            </Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ], [isLoading, handleEditMode, handleReintegrate, handleDelete]);

  useEffect(() => {
    loadTableData();
  
    const interval = setInterval(() => {
      loadTableData();
    }, 60000);
  
    return () => clearInterval(interval);
  }, [reloadTrigger]);

  return (
    <Card title="Pessoas Cadastradas" className="w-full mt-4">
      <Table
        rowKey="cpf"
        dataSource={pessoas}
        columns={columns}
        pagination={false}
        scroll={{ y: 200 }}
      />
    </Card>
  );
}
