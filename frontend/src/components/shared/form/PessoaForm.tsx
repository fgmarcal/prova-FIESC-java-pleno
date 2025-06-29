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
  import { useEffect, useState } from 'react';
  import { EnderecoApi } from '../../../api/EnderecoApi';
  import dayjs from 'dayjs';
import { notifyError, notifySuccess } from '../notifications/notification';
import { usePessoa } from '../../../context/usePessoa';
import type { PessoaFormData } from '../../../types/PessoaFormData';
import type { PessoaRequest } from '../../../types/PessoaRequest';
import { PessoaApi } from '../../../api/PessoaApi';
import { addCpfFormat } from '../../../utils/utils';
  
  export default function PessoaForm() {
    const { pessoaEditando, editMode, setEditMode, triggerReload } = usePessoa();
    const [form] = Form.useForm<PessoaFormData>();
    const [isLoading, setIsLoading] = useState(false);
    const [requiredField, setRequiredField] = useState<boolean>(false);

    const clearFields = () => {
      form.resetFields();
      setRequiredField(false);
      setIsLoading(false);
      setEditMode(false);
    }
  
    const buscarCep = async () => {
        const cep = form.getFieldValue('cep')?.replace(/\D/g, '');
        if (!cep || cep.length !== 8) return;
      
        setIsLoading(true);
        try {
          const endereco = await EnderecoApi.buscaCep(cep);
      
          form.setFieldsValue({
            rua: endereco.rua,
            cidade: endereco.cidade,
            estado: endereco.estado,
          });
          notifySuccess("Sucesso!");
        } catch (error) {
            notifyError(error);
        } finally {
          setIsLoading(false);
        }
      };

      const handleApiRequest = async(pessoaEditando: PessoaRequest | null, payload: PessoaRequest) => {
        setIsLoading(true);
        try {
          if (pessoaEditando && pessoaEditando.cpf) {
            await PessoaApi.atualizar(pessoaEditando.cpf, payload);
            notifySuccess('Pessoa atualizada com sucesso!');
          } else {
            await PessoaApi.criar(payload);
            notifySuccess('Pessoa salva com sucesso!');
          }
        } catch (error) {
          notifyError(error);
        }finally {
          clearFields();
          triggerReload();
        }
      }
      
      const onFinish = async (values: PessoaFormData) => {
        const {
          cep,
          rua,
          nascimento,
          cidade,
          estado,
          numero,
          ...dadosPessoais
        } = values;
      
        const nascimentoFormatado =
          dayjs.isDayjs(nascimento) ? nascimento.format('DD/MM/YYYY') : nascimento ?? undefined;

        const payload:PessoaRequest = {
          ...dadosPessoais,
          cpf: values.cpf?.replace(/\D/g, ''),
          nascimento: nascimentoFormatado,
          endereco: {
            cep,
            rua,
            cidade,
            estado,
            numero,
          },
        };
      
        await handleApiRequest(pessoaEditando, payload);
      };
      

    useEffect(() => {
      if (editMode && pessoaEditando) {
        const { endereco, nascimento,cpf,...dadosPessoais } = pessoaEditando;
        form.setFieldsValue({
          ...dadosPessoais,
          cpf: cpf ? addCpfFormat(cpf) : '',
          nascimento: nascimento ? dayjs(nascimento, "DD/MM/YYYY") : null,
          ...endereco,
        });
      } else {
        form.resetFields();
      }
    }, [pessoaEditando, form, editMode]);

    useEffect(() => {
      const hasCep = form.getFieldValue('cep');
      setRequiredField(!!hasCep);
    }, [form]);
  
    return (
      <Card title="Cadastro de Pessoa" style={{fontSize:'900', fontWeight:'bold'}}>
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
            <Form.Item
              label="Data de nascimento"
              name="nascimento"
              rules={[
                {
                  validator: (_, value) => {
                    if (!value) return Promise.resolve();
                    return value.isAfter(dayjs())
                      ? Promise.reject(new Error('A data não pode ser no futuro'))
                      : Promise.resolve();
                  },
                },
              ]}
            >
              <DatePicker
                format="DD/MM/YYYY"
                className="w-full"
                disabledDate={(current) => current && current > dayjs().endOf('day')}
              />
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
              <fieldset className="border border-gray-300 p-8 rounded-md relative">
                <legend className="text-sm font-medium px-2">Endereço</legend>
                <Row gutter={8} align="bottom">
                  <Col span={6}>
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
                        loading={isLoading}
                        icon={<SearchOutlined />}
                        type='primary'
                        htmlType="button"
                      />
                    </Form.Item>
                  </Col>
                </Row>
                <Row gutter={16} align="middle">
                  <Col span={18}>
                    <Form.Item label="Rua" name="rua" rules={[{required:requiredField}]}>
                      <Input disabled={isLoading}/>
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
                        {required:requiredField}
                      ]}
                    >
                      <Input type="default" disabled={isLoading} />
                    </Form.Item>
                  </Col>
                </Row>
  
                <Row gutter={16}>
                  <Col span={18}>
                    <Form.Item label="Cidade" name="cidade" rules={[{required:requiredField}]}>
                      <Input disabled={isLoading}/>
                    </Form.Item>
                  </Col>
                  <Col span={3}>
                    <Form.Item label="Estado" name="estado" rules={[{required:requiredField}]}>
                      <Input disabled={isLoading}/>
                    </Form.Item>
                  </Col>
                </Row>
              </fieldset>
            </Col>
          </Row>
  
          <div className="flex justify-start gap-2 mt-6">
            <Button icon={<ReloadOutlined />} htmlType="button" onClick={clearFields} style={{margin:'0.8rem'}} disabled={isLoading}>
              Limpar
            </Button>
            <Button icon={<SaveOutlined />} type="primary" htmlType="submit" style={{margin:'0.8rem'}} disabled={isLoading}>
              Salvar
            </Button>
          </div>
        </Form>
      </Card>
    );
  }
  