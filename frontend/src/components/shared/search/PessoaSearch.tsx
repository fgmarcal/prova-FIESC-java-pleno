import { useState } from 'react';
import { Card, Form, Input, Button, Descriptions } from 'antd';
import { PessoaApi } from '../../../api/PessoaApi';
import type { Pessoa } from '../../../entities/Pessoa';
import { notifyError, notifySuccess } from '../notifications/notification';
import { removeCpfFormat } from '../../../utils/utils';
import dayjs from 'dayjs';

export default function PessoaSearch() {
  const [form] = Form.useForm();
  const [pessoa, setPessoa] = useState<Pessoa | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSearch = async (value:{cpf:string}) => {
    const {cpf} = value
    try {
      setLoading(true);
      const result = await PessoaApi.buscarPorCPF(removeCpfFormat(cpf));
      setPessoa(result);
      notifySuccess("Sucesso");
    } catch (err) {
      notifyError(err);
      setPessoa(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Card title="Consultar pessoas integradas" className="w-full mt-4">
      <Form layout="inline" form={form} onFinish={handleSearch}>
        <Form.Item
          name="cpf"
          label="CPF"
          rules={[
            {
              pattern: /^\d{3}\.\d{3}\.\d{3}-\d{2}$/,
              message: 'CPF inválido',
            },
          ]}
        >
          <Input
            placeholder="000.000.000-00"
            onChange={(e) => {
              const formatted = e.target.value
                .replace(/\D/g, '')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d)/, '$1.$2')
                .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
              form.setFieldValue('cpf', formatted);
            }}
          />
        </Form.Item>
        <Form.Item>
          <Button htmlType="submit" type="primary" loading={loading}>
            Consultar
          </Button>
        </Form.Item>
      </Form>

      {pessoa && (
        <Descriptions
          bordered
          column={1}
          style={{ marginTop: '1rem' }}
          styles={{label: { fontWeight: 500 }}}
        >
          <Descriptions.Item label="Nome">{pessoa.nome}</Descriptions.Item>
          <Descriptions.Item label="Nascimento">{pessoa.nascimento}</Descriptions.Item>
          <Descriptions.Item label="Situação da Integração">{pessoa.status}</Descriptions.Item>
          <Descriptions.Item label="Data/Hora da Inclusão">
            {dayjs(pessoa.dataHoraInclusao).format('DD/MM/YYYY HH:mm:ss') ?? "N/A"}
          </Descriptions.Item>
          <Descriptions.Item label="Data/Hora da última alteração">
            {dayjs(pessoa.dataHoraAtualizacao).format('DD/MM/YYYY HH:mm:ss') ?? "N/A"}
          </Descriptions.Item>
        </Descriptions>
      )}
    </Card>
  );
}
