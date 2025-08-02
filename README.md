# Rinha de Backend 2025 - Java + GraalVM

[![Java](https://img.shields.io/badge/Java-24-red.svg)](https://openjdk.org/projects/jdk/24/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![NATS](https://img.shields.io/badge/NATS-Messaging-purple.svg)](https://nats.io/)
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
     │              └────────────────┘    └─────────────────────┘
     │                       │
     │                       ▼
     │              ┌─────────────────┐
     │              │      NATS       │
     │              │   (Messaging)   │
     │              └─────────────────┘
     │                       │
     │                       ▼
     │              ┌─────────────────┐
     │              │   PostgreSQL    │
     │              │    Database     │
     │              └─────────────────┘
     │
┌────▼─────┐
│ GraalVM  │
│ Native   │
│ Image    │
└──────────┘
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

- ✅ Processamento assíncrono com **NATS Messaging**
- ✅ **GraalVM Native Image** para startup instantâneo
- ✅ **Circuit Breaker** com Resilience4j
- ✅ **Múltiplos dispatchers** configuráveis (20 por instância)
- ✅ **Queue Groups** para load balancing automático
- ✅ Fallback transparente entre processadores
- ✅ Correlação de IDs para rastreabilidade
- ✅ Reprocessamento automático de mensagens com falha

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

## 📡 Mensageria Assíncrona (NATS)

### Arquitetura de Processamento

O sistema utiliza **NATS** como message broker para processamento assíncrono e escalável:

```
HTTP Request → Controller → NATS Publisher → Queue → Multiple Consumers → Payment Processors
```

### Características da Mensageria

- 🚀 **Alto Throughput**: 40+ dispatchers simultâneos (20 por instância)
- 🔄 **Load Balancing**: Queue groups distribuem mensagens automaticamente
- 📦 **Garantia de Entrega**: Reprocessamento automático em caso de falha
- 🎯 **Baixa Latência**: NATS otimizado para alta performance
- 🛡️ **Circuit Breaker**: Proteção contra cascata de falhas

### Configuração de Concorrência

| Ambiente | Dispatchers | Threads | Capacidade |
|----------|-------------|---------|------------|
| Local    | 5           | 5       | ~50 msg/s  |
| Produção | 20          | 20      | ~200 msg/s |
| 2 Instâncias | 40      | 40      | ~400 msg/s |

### Queue Groups para Escalabilidade

```yaml
# Ambas instâncias compartilham o mesmo queue group
NATS_CONSUMER_QUEUE_GROUP=payment-processors

# Resultado: Load balancing automático
Mensagem 1 → api01 (20 dispatchers)
Mensagem 2 → api02 (20 dispatchers)  
Mensagem 3 → api01 (20 dispatchers)
Mensagem 4 → api02 (20 dispatchers)
```

## 🛠️ Tecnologias

### Core

- **Java 24** + **GraalVM Native Image**
- **Spring Boot 3.5.4**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** com índices otimizados

### Mensageria & HTTP

- **NATS 2** - Message broker de alta performance
- **OpenFeign** - Cliente HTTP declarativo
- **Resilience4j** - Circuit breaker e retry
- **Jackson** - Serialização JSON

### Desenvolvimento

- **Lombok** - Redução de boilerplate
- **TestContainers** - Testes de integração
- **Maven** - Gerenciamento de dependências

## ⚡ Execução

### 1. Subir Payment Processors

```bash
# Baixar processadores da competição
git clone https://github.com/zanfranceschi/rinha-de-backend-2025
cd rinha-de-backend-2025/payment-processor
docker-compose up -d
```

### 2. Compilar Imagem Nativa (Recomendado)

```bash
git clone https://github.com/danielwisky/rinha-de-backend-2025-graalvm.git
cd rinha-de-backend-2025-graalvm

# Compilar para imagem nativa (GraalVM)
mvn spring-boot:build-image -Pnative
```

### 3. Executar o Backend

```bash
# Subir toda a stack (2 instâncias + NATS + PostgreSQL + Nginx)
docker-compose up -d
```

### 4. Verificar Status

```bash
# Ver logs em tempo real
docker-compose logs -f api01 api02

# Status dos serviços
curl http://localhost:9999/actuator/health

# Teste de pagamento
curl -X POST http://localhost:9999/payments \
  -H "Content-Type: application/json" \
  -d '{"correlationId":"test-123","amount":19.90}'
```

### 5. Testes Locais

```bash
./mvnw test
```

## 🔧 Configuração

### Profiles

- `local` - Desenvolvimento local
- `container-test` - Testes com TestContainers
- `production` - Produção (padrão)

### Variáveis de Ambiente

#### Core Application

| Variável                         | Descrição                   | Padrão                                   |
|----------------------------------|-----------------------------|------------------------------------------|
| `SPRING_PROFILES_ACTIVE`         | Profile ativo               | `production`                             |
| `PAYMENT_PROCESSOR_DEFAULT_URL`  | URL do processador padrão   | `http://payment-processor-default:8080`  |
| `PAYMENT_PROCESSOR_FALLBACK_URL` | URL do processador fallback | `http://payment-processor-fallback:8080` |

#### Database

| Variável                    | Descrição          | Padrão                            |
|-----------------------------|--------------------|-----------------------------------|
| `POSTGRES_URI`              | URL do PostgreSQL  | `jdbc:postgresql://db:5432/rinha` |
| `POSTGRES_USER`             | Usuário do banco   | `rinha`                           |
| `POSTGRES_PASSWORD`         | Senha do banco     | `rinha`                           |

#### NATS Messaging

| Variável                       | Descrição                    | Padrão                    |
|--------------------------------|------------------------------|---------------------------|
| `NATS_URL`                     | URL do servidor NATS         | `nats://nats:4222`        |
| `NATS_SUBJECT`                 | Subject para mensagens       | `payments.processing`     |
| `NATS_CONSUMER_CONCURRENCY`    | Número de dispatchers        | `15`                      |
| `NATS_CONSUMER_QUEUE_GROUP`    | Queue group para load balance| `payment-processors`      |

## 👤 Autor

**Daniel Wisky**

- GitHub: [@danielwisky](https://github.com/danielwisky)

---

<p align="center">
  Desenvolvido com ❤️ para a <strong>Rinha de Backend 2025</strong><br>
  🎯 <em>GraalVM Native + NATS Messaging + Clean Architecture</em>
  <br><br>
  ⚡ <strong>Startup em 50ms</strong> | 🚀 <strong>400+ msg/s</strong> | 💾 <strong>~100MB RAM</strong>
</p>