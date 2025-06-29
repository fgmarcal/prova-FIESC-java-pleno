# Prova Java PLENO SD - WEB - Configuração

Este projeto foi desenvolvido como parte da prova prática do processo seletivo 01522/2025 para a vaga de Desenvolvedor Full Stack Pleno no SENAI/FIESC.

---

## Funcionalidades Implementadas

- Estrutura de projeto baseada em **DDD (Domain-Driven Design)**.
- Módulo de cadastro e atualização de pessoas, com validação de dados.
- Integração com serviço externo para consulta de endereço via **ViaCEP**.
- Comunicação com API externa via cliente REST.
- Produção e envio de mensagens para fila **RabbitMQ**.
- Recebimento e consumo de mensagens (status de integração).
- Atualização de status de integração no banco de dados.
- Testes unitários cobrindo regras de negócio

---

## Tecnologias Utilizadas

### Backend

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **PostgreSQL**
- **RabbitMQ**
- **Lombok**
- **Mockito / JUnit 5**

### Frontend (SPA)

- **React 18 + Vite**
- **TypeScript**
- **Ant Design + Tailwind CSS**
- **Axios**

### Database
- **Postgres 15**

### Infraestrutura
- **Docker**

## Iniciando o projeto

- Crie um arquivo .env na raiz de cada pasta

-- Pasta ~backend~: Dados de desenvolvimento

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/backend_db
SPRING_DATASOURCE_USERNAME=user
SPRING_DATASOURCE_PASSWORD=pass
SPRING_RABBITMQ_HOST=localhost
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=guest
SPRING_RABBITMQ_PASSWORD=guest
API_PESSOA_BASE_URL=http://localhost:8081
FRONTEND_BASE_URL=http://localhost:5173
```

-- Pasta ~api~: Dados de desenvolvimento

