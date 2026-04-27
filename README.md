# CooperVote API - Sistema de Votação Cooperativista

API REST para gerenciamento de sessões de votação em cooperativas.

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

## Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|----------|-------|
| `SPRING_DATASOURCE_URL` | URL de conexão PostgreSQL | jdbc:postgresql://localhost:5432/coopervote |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | coopervote |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | coopervote123 |

### application.properties

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jpa.hibernate.ddl-auto=validate
springdoc.swagger-ui.enabled=true
```

## Banco de Dados

### Tabelas

```sql
agenda          -- Pautas de votação
voting_session -- Sessões de votação
vote            -- Votos registrados
```

### Índices

- Unique: `uk_agenda_title` (título único)
- Unique: `uk_vote_cpf_session` (um voto por CPF por sessão)
- Index: `idx_vote_session_id` (busca por sessão)
- Index: `idx_voting_session_agenda_id` (busca por pauta)

## Rate Limiting

- Limite: 60 requisições por minuto por IP
- Retorno: HTTP 429 quando excedido

## Build Local

```bash
./mvnw clean package -DskipTests
java -jar target/coopervote-0.0.1-SNAPSHOT.jar
```

## Deploy

### Railway
1. Conectar repositório Git
2. Configurar variáveis de ambiente
3. Deploy automático

### Supabase (Banco)
- Criar projeto
- Flyway cria as tabelas automaticamente

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

## Licença

**© 2026 Lucas Porto. Todos os direitos reservados.**
