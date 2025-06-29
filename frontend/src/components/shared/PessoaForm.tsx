import {
    Button,
    Card,
    Col,
    DatePicker,
    Form,
    Input,
    Row,
  } from 'antd';
  import { ReloadOutlined, SaveOutlined, SearchOutlined } from '@ant-design/icons';
  import { useState } from 'react';
  import { EnderecoApi } from '../../api/EnderecoApi';
  import dayjs from 'dayjs';
import { notifyError, notifySuccess } from './notification';
  
  interface PessoaFormData {
    nome: string;
    nascimento?: string;
    cpf?: string;
    email?: string;
    cep?: string;
    numero?: string;
    logradouro?: string;
    bairro?: string;
    cidade?: string;
    estado?: string;
  }
  
  export default function PessoaForm() {
    const [form] = Form.useForm<PessoaFormData>();
    const [loadingCep, setLoadingCep] = useState(false);
  
    const buscarCep = async () => {
        const cep = form.getFieldValue('cep')?.replace(/\D/g, '');
        if (!cep || cep.length !== 8) return;
      
        setLoadingCep(true);
        try {
          const endereco = await EnderecoApi.buscaCep(cep);
      
          form.setFieldsValue({
            logradouro: endereco.rua,
            bairro: endereco.bairro,
            cidade: endereco.cidade,
            estado: endereco.estado,
          });
          notifySuccess("Sucesso!");
        } catch (error) {
            notifyError(error);
        } finally {
          setLoadingCep(false);
        }
      };
      
  
    const onFinish = (values: PessoaFormData) => {
      const payload = {
        ...values,
        cpf: values.cpf?.replace(/\D/g, ''),
        nascimento: values.nascimento ? dayjs(values.nascimento).toISOString() : undefined,
      };
  
      console.log('Salvar:', payload);
      notifySuccess('Pessoa salva com sucesso!');
    };
  
    return (
      <Card title="Cadastro de Pessoa" className="w-full">
        <Form
          form={form}
          layout="vertical"
          onFinish={onFinish}
          autoComplete="off"
        >
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item
                label="Nome completo"
                name="nome"
                rules={[
                  { required: true, message: 'Nome é obrigatório' },
                  {
                    validator: (_, value) =>
                      value?.trim().split(' ').length > 1
                        ? Promise.resolve()
                        : Promise.reject('Digite pelo menos 2 nomes'),
                  },
                  {
                    validator: (_, value) =>
                      value?.trim().split(' ').every((n: string) => /^[A-Z][a-z]+$/.test(n))
                        ? Promise.resolve()
                        : Promise.reject('Cada nome deve começar com letra maiúscula'),
                  },
                ]}
              >
                <Input placeholder="Ex: João da Silva" />
              </Form.Item>
            </Col>
          </Row>
  
          <Row gutter={16}>
            <Col span={8}>
              <Form.Item label="Data nascimento" name="nascimento">
                <DatePicker format="DD/MM/YYYY" className="w-full" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                label="CPF"
                name="cpf"
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
            </Col>
            <Col span={8}>
              <Form.Item
                label="E-mail"
                name="email"
                rules={[
                  {
                    type: 'email',
                    message: 'E-mail inválido',
                  },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
  
          <Row gutter={0}>
            <Col span={24}>
              <fieldset className="border border-gray-300 p-4 rounded-md relative">
                <legend className="text-sm font-medium px-2">Endereço</legend>
                <Row gutter={16} align="middle">
                  <Col span={8}>
                    <Form.Item
                      label="Cep"
                      name="cep"
                      rules={[
                        {
                          pattern: /^\d{5}-?\d{3}$/,
                          message: 'CEP inválido',
                        },
                      ]}
                    >
                      <Input placeholder="00000-000" />
                    </Form.Item>
                  </Col>
                  <Col span={2}>
                    <Form.Item label=" ">
                      <Button
                        onClick={buscarCep}
                        loading={loadingCep}
                        icon={<SearchOutlined />}
                        className="w-full"
                        htmlType="button"
                      />
                    </Form.Item>
                  </Col>
                  <Col span={14}>
                    <Form.Item label="Rua" name="logradouro">
                      <Input />
                    </Form.Item>
                  </Col>
                </Row>
  
                <Row gutter={16}>
                  <Col span={18}>
                    <Form.Item label="Cidade" name="cidade">
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col span={3}>
                    <Form.Item label="Estado" name="estado">
                      <Input />
                    </Form.Item>
                  </Col>
                  <Col span={3}>
                    <Form.Item
                      label="Número"
                      name="numero"
                      rules={[
                        {
                          pattern: /^\d+$/,
                          message: 'Apenas números',
                        },
                      ]}
                    >
                      <Input type="number" />
                    </Form.Item>
                  </Col>
                </Row>
              </fieldset>
            </Col>
          </Row>
  
          <div className="flex justify-end gap-2 mt-6">
            <Button icon={<ReloadOutlined />} htmlType="button" onClick={() => form.resetFields()}>
              Limpar
            </Button>
            <Button icon={<SaveOutlined />} type="primary" htmlType="submit">
              Salvar
            </Button>
          </div>
        </Form>
      </Card>
    );
  }
  