# Rinha de Backend 2025 - Java + GraalVM

[![Java](https://img.shields.io/badge/Java-24-red.svg)](https://openjdk.org/projects/jdk/24/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![GraalVM](https://img.shields.io/badge/GraalVM-Native-orange.svg)](https://www.graalvm.org/)
[![Clean Architecture](https://img.shields.io/badge/Architecture-Clean-green.svg)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

**Backend intermediador de pagamentos** desenvolvido para a **Rinha de Backend 2025** - uma
competição que desafia desenvolvedores a criar sistemas de alta performance para processamento de
pagamentos com estratégias inteligentes de fallback.

## 🎯 O Desafio

Este projeto implementa um **backend intermediador** que recebe solicitações de pagamento e as
encaminha para dois serviços de processamento:

- 🟢 **Payment Processor Default** - Taxa menor, mas instável
- 🔴 **Payment Processor Fallback** - Taxa maior, backup para falhas

### Objetivo da Competição

**Maximizar o lucro** processando o maior número de pagamentos com as menores taxas possíveis,
mantendo alta disponibilidade mesmo com instabilidades nos processadores.

### Sistema de Pontuação

- 💰 **Lucro**: Quanto mais pagamentos com menor taxa, melhor
- ⚡ **Performance**: Bônus de 2% por cada 1ms abaixo de 11ms no p99
- 🚨 **Penalidade**: 35% de multa por inconsistências detectadas

### Componentes da Aplicação

```
┌──────────────┐    ┌────────────────┐    ┌─────────────────────┐
│ Load Balancer│────│    Backend     │────│ Payment Processor   │
│   (nginx)    │    │ (2+ instâncias)│    │    Default          │
└──────────────┘    │                │    └─────────────────────┘
     │              │                │    ┌─────────────────────┐
     │              │                │────│ Payment Processor   │
     │              │                │    │    Fallback         │
     └──────────────┴────────────────┘    └─────────────────────┘
                    │
                    ▼
            ┌──────────────┐
            │ PostgreSQL   │
            │   Database   │
            └──────────────┘
```

## 🚀 Funcionalidades

### Endpoints Implementados

#### 1. Processar Pagamento

```http
POST /payments
Content-Type: application/json

{
  "correlationId": "4a7901b8-7d26-4d9d-aa19-4dc1c7cf60b3",
  "amount": 19.90
}
```

**Resposta**: `HTTP 202 Accepted`

**Características**:

- ✅ Processamento assíncrono com Virtual Threads
- ✅ Retry automático (3 tentativas)
- ✅ Fallback transparente entre processadores
- ✅ Correlação de IDs para rastreabilidade

#### 2. Resumo de Pagamentos (Auditoria)

```http
GET /payments-summary?from=2020-07-10T12:34:56.000Z&to=2020-07-10T12:35:56.000Z
```

**Resposta**:

```json
{
  "default": {
    "totalRequests": 43236,
    "totalAmount": 415542345.98
  },
  "fallback": {
    "totalRequests": 423545,
    "totalAmount": 329347.34
  }
}
```

**Características**:

- ✅ Filtros opcionais por data (`from`, `to`)
- ✅ Agregações em tempo real usando Criteria API
- ✅ Índices otimizados para performance
- ✅ Retorna valores zerados quando não há dados

## 🛠️ Tecnologias

### Core

- **Java 24** + **GraalVM**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **PostgreSQL**
- **Hibernate**

## ⚡ Execução

### 1. Subir Payment Processors

```bash
# Baixar processadores da competição
git clone https://github.com/zanfranceschi/rinha-de-backend-2025
cd rinha-de-backend-2025/payment-processor
docker-compose up -d
```

### 2. Executar o Backend

```bash
git clone https://github.com/danielwisky/rinha-de-backend-2025-graalvm.git
cd rinha-de-backend-2025-graalvm
docker-compose up -d
```

### 3. Testes Locais

```bash
./mvnw test
```

## 🔧 Configuração

### Profiles

- `local` - Desenvolvimento local
- `container-test` - Testes com TestContainers
- `production` - Produção (padrão)

### Variáveis de Ambiente

| Variável                         | Descrição                   | Padrão                                   |
|----------------------------------|-----------------------------|------------------------------------------|
| `SPRING_PROFILES_ACTIVE`         | Profile ativo               | `production`                             |
| `PAYMENT_PROCESSOR_DEFAULT_URL`  | URL do processador padrão   | `http://payment-processor-default:8080`  |
| `PAYMENT_PROCESSOR_FALLBACK_URL` | URL do processador fallback | `http://payment-processor-fallback:8080` |
| `SPRING_DATASOURCE_URL`          | URL do PostgreSQL           | `jdbc:postgresql://db:5432/rinha`        |
| `SPRING_DATASOURCE_USERNAME`     | Usuário do banco            | `rinha`                                  |
| `SPRING_DATASOURCE_PASSWORD`     | Senha do banco              | `password`                               |

## 👤 Autor

**Daniel Wisky**

- GitHub: [@danielwisky](https://github.com/danielwisky)

---

<p align="center">
  Desenvolvido com ❤️ para a <strong>Rinha de Backend 2025</strong><br>
  🎯 <em>Clean Architecture + Virtual Threads + PostgreSQL</em>
</p>