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

## 🛠️ Tecnologias Utilizadas

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