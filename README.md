# CooperVote API - Sistema de Votação Cooperativista

API REST para gerenciamento de sessões de votação em cooperativas.

## Início Rápido

### Pré-requisitos
- Docker
- Docker Compose

### Executar com Docker Compose

```bash
# Na raiz do projeto coopervote/
docker-compose up -d
```

A API estará disponível em: **http://localhost:8080**

Swagger UI: **http://localhost:8080/swagger-ui.html**

### Parar a aplicação

```bash
docker-compose down
```

---

## Visão Geral

Sistema completo para gestão de assembleias cooperativistas, permitindo:
- Cadastro de pautas para votação
- Abertura de sessões de votação com duração configurável
- Recebimento de votos (Sim/Não)
- Validação de CPF via client externo (simulado)
- Apuração de resultados
- Rate limiting para proteção

## Tecnologias

| Tecnologia | Versão |
|-----------|--------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Spring Data JPA | - |
| PostgreSQL | 16+ |
| Flyway | - |
| Springdoc OpenAPI | 2.7.0 |
| Lombok | - |
| Docker | - |

## Funcionalidades

### 1. Cadastrar Pauta
Cria uma nova pauta para votação em assembleia.

**Endpoint:** `POST /api/v1/agendas`

**Request:**
```json
{
  "title": "Aumento do Capital Social",
  "description": "Proposta de aumento do capital social em 10%"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "Aumento do Capital Social",
  "description": "Proposta de aumento do capital social em 10%",
  "createdAt": "2026-04-26T10:00:00"
}
```

### 2. Listar Pautas
Retorna todas as pautas cadastradas.

**Endpoint:** `GET /api/v1/agendas`

### 3. Abrir Sessão de Votação
Abre uma nova sessão de votação para uma pauta existente.

**Endpoint:** `POST /api/v1/sessions/agenda/{agendaId}`

**Request:**
```json
{
  "durationMinutes": 5
}
```

**Regras:**
- Duração padrão: 1 minuto
- Máximo: 60 minutos
- Apenas uma sessão por pauta

### 4. Registrar Voto
Registra o voto de um associado.

**Endpoint:** `POST /api/v1/votes/session/{sessionId}`

**Request:**
```json
{
  "cpf": "12345678901",
  "voteValue": true
}
```

**Validações:**
- CPF deve ter 11 dígitos
- CPF é validado via client externo
- Apenas um voto por CPF por sessão
- Sessão deve estar ativa

### 5. Obter Resultado
Retorna o resultado da votação.

**Endpoint:** `GET /api/v1/votes/session/{sessionId}/result`

**Response:**
```json
{
  "sessionId": 1,
  "agendaId": 1,
  "yesVotes": 15,
  "noVotes": 5,
  "totalVotes": 20
}
```

### 6. Encerrar Sessão
Encerra manualmente uma sessão de votação.

**Endpoint:** `POST /api/v1/sessions/{id}/close`

## Códigos de Resposta HTTP

| Código | Significado |
|--------|-------------|
| 201 | Criado com sucesso |
| 200 | Sucesso |
| 400 | Erro de validação |
| 404 | Recurso não encontrado |
| 409 | Conflito (voto duplicado) |
| 422 | Entidade não processável |
| 429 | Muitas requisições |
| 500 | Erro interno |

## Mensagens de Erro

Todas as mensagens de erro estão em português brasileiro:

| Erro | HTTP | Mensagem |
|------|------|----------|
| Pauta não encontrada | 404 | Pauta nao encontrada com ID: {id} |
| Sessão não encontrada | 404 | Sessao de votacao nao encontrada com ID: {id} |
| Sessão já existe | 409 | Ja existe uma sessao de votacao para esta pauta: {id} |
| Sessão encerrada | 422 | Sessao de votacao encerrada: {id} |
| Voto duplicado | 409 | Associado com CPF {cpf} ja votou nesta sessao ({sessionId}) |
| CPF inválido | 422 | CPF com formato invalido |
| CPF não pode votar | 422 | Status do CPF: UNABLE_TO_VOTE |

## Documentação API

Swagger UI disponível em: `/swagger-ui.html`

---

## Quick Start com Docker

```bash
# Subir toda a stack (API + PostgreSQL + Frontend)
cd ..
docker-compose up -d --build

# Ver logs
docker-compose logs -f api

# Parar
docker-compose down
```

A API estará disponível em: **http://localhost:8080**

---

## Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|----------|-------|
| `SPRING_DATASOURCE_URL` | URL de conexão PostgreSQL | jdbc:postgresql://localhost:5432/coopervote |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | coopervote |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | coopervote123 |
| `SPRING_PROFILES_ACTIVE` | Perfil Spring | prod |

### Via Docker Compose

As variáveis são configuradas automaticamente no `docker-compose.yml`:

```yaml
api:
  environment:
    SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/coopervote
    SPRING_DATASOURCE_USERNAME: coopervote
    SPRING_DATASOURCE_PASSWORD: coopervote123
```

### Personalizar

Crie um arquivo `.env` na raiz do projeto:

```env
DB_USER=meuusuario
DB_PASSWORD=minhasenha
DB_NAME=meubanco
```

---

## Desenvolvimento Local

### Pré-requisitos

- Java 21
- Maven 3.8+
- PostgreSQL 16 (ou Docker)

### 1. Compilar

```bash
./mvnw clean package -DskipTests
```

### 2. Rodar

```bash
./mvnw spring-boot:run
```

Ou com o JAR:

```bash
java -jar target/coopervote-0.0.1-SNAPSHOT.jar
```

### 3. Com PostgreSQL via Docker

```bash
# PostgreSQL isolado (sem a API)
docker run -d \
  --name coopervote-postgres-dev \
  -e POSTGRES_USER=coopervote \
  -e POSTGRES_PASSWORD=coopervote123 \
  -e POSTGRES_DB=coopervote \
  -p 5432:5432 \
  postgres:16-alpine
```

---

## Build Docker

### Pré-requisitos

- Docker instalado
- Variáveis de ambiente configuradas (ou usar valores padrão)

### Build da Imagem

```bash
# Na raiz do projeto coopervote/
docker build -t coopervote/api:latest .
```

### Executar Container (Standalone)

```bash
# Com banco PostgreSQL externo (já rodando)
docker run -d \
  --name coopervote-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/coopervote \
  -e SPRING_DATASOURCE_USERNAME=coopervote \
  -e SPRING_DATASOURCE_PASSWORD=coopervote123 \
  -e SPRING_PROFILES_ACTIVE=prod \
  coopervote/api:latest
```

**Nota**: Use `host.docker.internal` no Windows/Mac para acessar o banco na máquina host. No Linux, use o IP da rede (ex: 172.17.0.1).

### Executar com Banco via Docker

```bash
# 1. Subir o PostgreSQL
docker run -d \
  --name coopervote-postgres \
  -e POSTGRES_USER=coopervote \
  -e POSTGRES_PASSWORD=coopervote123 \
  -e POSTGRES_DB=coopervote \
  -p 5432:5432 \
  postgres:16-alpine

# 2. Subir a API conectando no banco
docker run -d \
  --name coopervote-api \
  --link coopervote-postgres:postgres \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/coopervote \
  -e SPRING_DATASOURCE_USERNAME=coopervote \
  -e SPRING_DATASOURCE_PASSWORD=coopervote123 \
  coopervote/api:latest
```

### Ver Logs

```bash
docker logs -f coopervote-api
```

### Parar e Remover

```bash
docker stop coopervote-api
docker rm coopervote-api
```

### Build com Jib (Maven)

```bash
./mvnw compile jib:build
```

---

## Flyway - Migrações

As migrações são executadas automaticamente ao iniciar a aplicação.

### Scripts de Migração

Localização: `src/main/resources/db/migration/`

```
V1__create_tables.sql  - Cria schema inicial
```

### Verificar Migrações

Acesse: `http://localhost:8080/actuator/flyway`

---

## Banco de Dados

### Tabelas

| Tabela | Descrição |
|--------|-----------|
| `agenda` | Pautas de votação |
| `voting_session` | Sessões de votação |
| `vote` | Votos registrados |

### Índices

- Unique: `uk_agenda_title` (título único)
- Unique: `uk_vote_cpf_session` (um voto por CPF por sessão)
- Index: `idx_vote_session_id` (busca por sessão)
- Index: `idx_voting_session_agenda_id` (busca por pauta)

---

## Rate Limiting

- Limite: 60 requisições por minuto por IP
- Retorno: HTTP 429 quando excedido

---

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

Resposta esperada:
```json
{"status":"UP"}
```

---

## Arquitetura

```
src/main/java/com/coopervote/
├── CoopervoteApplication.java     # Classe principal
├── config/                       # Configurações
├── application/
│   ├── service/                 # Lógica de negócio
│   └── exception/              # Exceções
├── domain/
│   ├── model/                  # Entidades JPA
│   └── repository/             # Repositórios
├── infrastructure/
│   └── cpf/                   # Client de validação CPF
└── presentation/
    └── rest/
        ├── controller/         # REST Controllers
        └── dto/                # Data Transfer Objects
```

---

## Deploy

### Railway

1. Conectar repositório Git
2. Adicionar PostgreSQL como dependência
3. Configurar variáveis de ambiente:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
4. Deploy automático

### Docker Compose (Produção)

```bash
# Subir stack completa
docker-compose up -d --build

# Rebuild específico
docker-compose up -d --build api
```

### Variáveis de Produção

```bash
export DB_USER=produser
export DB_PASSWORD=ProD_S3cr3t!
export DB_NAME=coopervote_prod
docker-compose up -d
```

---

## Licença

**© 2026 Lucas Porto. Todos os direitos reservados.**