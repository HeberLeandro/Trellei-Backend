# Trellei Backend

Backend de um clone do Trello desenvolvido como projeto de estudo para aprofundar conhecimentos em **Java, Spring Boot, DDD, Arquitetura Hexagonal, testes, mensageria e observabilidade**.

O objetivo do projeto não é somente fazer uma API funcionar. A ideia é evoluí-la progressivamente, usando o projeto como laboratório para praticar decisões de arquitetura que aparecem em aplicações Java reais.

---

## 1. Objetivos do projeto

### Objetivo funcional

Construir uma aplicação semelhante ao Trello, inicialmente com:

- usuários;
- autenticação;
- boards;
- listas dentro dos boards;
- cards dentro das listas;
- movimentação de cards entre listas.

Em uma segunda etapa:

- múltiplos usuários por board;
- permissões por usuário;
- chat em tempo real dentro do board;
- comentários e atividades;
- notificações.

### Objetivo de estudo

Usar a aplicação para praticar:

- Spring Boot;
- Spring Security e JWT;
- JPA/Hibernate;
- PostgreSQL;
- H2 para desenvolvimento/testes rápidos;
- DDD; 
- Arquitetura Hexagonal / Ports and Adapters;
- testes unitários e de integração;
- Testcontainers;
- Flyway ou Liquibase;
- mensageria com Kafka ou RabbitMQ;
- WebSocket;
- observabilidade;
- logs estruturados;
- métricas com Actuator + Prometheus;
- dashboards com Grafana;
- tracing com OpenTelemetry;
- Docker;
- documentação OpenAPI/Swagger;
- CI/CD.

---

# 2. Estratégia de evolução

A arquitetura não deve começar complexa. O projeto será evoluído em etapas.

```text
Funcionalidade simples
        ↓
DDD Light
        ↓
Separação Domain / Application / Infrastructure
        ↓
Testes unitários
        ↓
Ports and Adapters
        ↓
Integrações externas
        ↓
Mensageria
        ↓
Observabilidade
        ↓
Escalabilidade
```

A regra principal é:

> **Adicionar complexidade somente quando ela ajudar a resolver um problema ou ensinar um conceito.**

O objetivo é evitar criar uma arquitetura extremamente sofisticada para um CRUD simples.

---

# 3. Arquitetura adotada

O projeto começa com **DDD Light** e uma separação inspirada em **Arquitetura Hexagonal**.

As principais áreas são:

```text
Domain
  ↓
Application
  ↓
Infrastructure
  ↓
Web / Database / Messaging / External Services
```

Uma forma melhor de visualizar:

```text
                 ┌──────────────────────┐
                 │      REST API         │
                 │   Controllers/Web     │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │     Application      │
                 │      Use Cases       │
                 └──────────┬───────────┘
                            │
                            ▼
                 ┌──────────────────────┐
                 │       Domain         │
                 │ Entities / VOs /     │
                 │ Domain Services /    │
                 │ Domain Rules         │
                 └──────────┬───────────┘
                            │
                    Ports / Interfaces
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
        ┌──────────┐  ┌───────────┐  ┌───────────┐
        │ Database │  │ Messaging │  │ External  │
        │   JPA    │  │ Kafka/etc │  │   APIs    │
        └──────────┘  └───────────┘  └───────────┘
```

O ponto mais importante é que **as regras do domínio não devem depender de Spring, JPA, Kafka ou HTTP sempre que isso puder ser evitado**.

---

# 4. DDD Light

DDD significa **Domain-Driven Design**. Neste projeto, o objetivo não é implementar todos os conceitos possíveis do DDD, mas usar os conceitos que realmente ajudam a modelar o problema.

## 4.1 Entidades

Uma entidade possui identidade própria.

Exemplos:

```text
User
Board
BoardList
Card
Comment
```

Um `Board` continua sendo o mesmo board mesmo que seu nome ou cor sejam alterados.

---

## 4.2 Value Objects

Um Value Object representa um valor do domínio e não possui identidade própria.

Exemplos futuros:

```text
Email
BoardName
CardTitle
Color
```

Exemplo conceitual:

```java
public record Email(String value) {

    public Email {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

A vantagem é colocar a regra de validade junto do próprio conceito.

---

## 4.3 Aggregate Root

Um agregado é um conjunto de objetos que precisa manter certas regras de consistência.

Para este projeto, uma modelagem possível é:

```text
Board
 ├── BoardList
 │    ├── Card
 │    ├── Card
 │    └── ...
 ├── BoardList
 │    └── Card
 └── ...
```

O `Board` pode funcionar como **Aggregate Root**.

Isso significa que, em vez de qualquer código alterar diretamente qualquer parte interna do agregado, as alterações importantes passam pelo `Board`.

Exemplo conceitual:

```java
board.addList("Todo");
board.addList("Doing");
board.moveCard(cardId, targetListId);
```

Em vez de:

```java
list.getCards().remove(card);
targetList.getCards().add(card);
```

espalhado pelo sistema.

A ideia é fazer o próprio domínio proteger suas invariantes.

---

# 5. Exemplo: Board como Aggregate Root

Uma versão inicial e simples poderia ser:

```java
public class Board {

    private final Integer id;
    private String name;
    private String color;
    private final User owner;

    public Board(Integer id, String name, String color, User owner) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Board name is required");
        }

        if (color == null || color.isBlank()) {
            throw new IllegalArgumentException("Board color is required");
        }

        if (owner == null) {
            throw new IllegalArgumentException("Board owner is required");
        }

        this.id = id;
        this.name = name;
        this.color = color;
        this.owner = owner;
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Board name cannot be empty");
        }

        this.name = newName;
    }

    public void changeColor(String newColor) {
        if (newColor == null || newColor.isBlank()) {
            throw new IllegalArgumentException("Board color cannot be empty");
        }

        this.color = newColor;
    }
}
```

A entidade começa a ter **comportamento**, em vez de ser apenas um objeto com getters e setters.

---

# 6. Domain Service

Um Domain Service representa uma regra de negócio que não pertence naturalmente a uma única entidade.

Exemplo futuro: regras de membros do board.

```java
public class BoardMembershipService {

    public void addMember(Board board, User user) {
        if (board.hasMember(user)) {
            throw new IllegalArgumentException("User is already a member");
        }

        board.addMember(user);
    }

    public void removeMember(Board board, User user) {
        if (board.isOwner(user)) {
            throw new IllegalArgumentException("Board owner cannot be removed");
        }

        board.removeMember(user);
    }
}
```

Esse service fica no domínio porque ele contém **regra de negócio**, não regra de HTTP ou banco de dados.

---

# 7. Application Layer e Use Cases

A camada de aplicação representa **o que o sistema pode fazer**.

Exemplos:

```text
CreateBoard
RenameBoard
DeleteBoard
GetUserBoards
CreateBoardList
RenameBoardList
CreateCard
MoveCard
AddBoardMember
RemoveBoardMember
SendBoardMessage
```

## Por que usar `UseCase`?

`UseCase` não é uma obrigação do Java ou do Spring. É uma convenção de projeto muito usada em arquiteturas como Clean Architecture e Hexagonal.

O nome ajuda a deixar explícito que a classe representa uma **ação do sistema**.

Exemplo:

```java
public class CreateBoardUseCase {

    private final BoardRepository boardRepository;

    public CreateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board execute(String name, String color, User owner) {
        Board board = new Board(null, name, color, owner);
        return boardRepository.save(board);
    }
}
```

Também poderiam ser usados nomes como:

```text
CreateBoardService
BoardCreator
CreateBoardCommandHandler
```

Mas `CreateBoardUseCase` é interessante para este projeto porque deixa o papel da classe evidente.

---

# 8. Fluxo de uma requisição

O fluxo esperado para criar um board é:

```text
HTTP POST /boards
       ↓
BoardController
       ↓
CreateBoardUseCase
       ↓
Board (domínio)
       ↓
BoardRepository (porta)
       ↓
JpaBoardRepository (adapter)
       ↓
Hibernate / JPA
       ↓
Database
```

### Controller

O Controller deve cuidar de HTTP.

```java
@PostMapping
public ResponseEntity<BoardResponse> create(
        @RequestBody CreateBoardRequest request) {

    Board board = createBoardUseCase.execute(
        request.name(),
        request.color(),
        currentUser
    );

    return ResponseEntity.ok(BoardResponse.from(board));
}
```

Ele não deve conter regras como:

```java
if (board.getOwner() == currentUser) {
   ...
}
```

Essas regras pertencem ao domínio/aplicação.

---

# 9. Repository como Port

Em Hexagonal Architecture, uma porta é uma abstração que permite que o domínio/aplicação converse com algo externo sem conhecer a implementação.

Exemplo:

```java
public interface BoardRepository {

    Board save(Board board);

    Optional<Board> findById(Integer id);

    List<Board> findByOwner(User user);
}
```

A aplicação conhece apenas essa interface.

A implementação pode ser:

```java
JpaBoardRepository
```

ou futuramente:

```text
MongoBoardRepository
CachedBoardRepository
RemoteBoardRepository
```

A troca da tecnologia não precisa alterar o Use Case.

---

# 10. Ports and Adapters

A principal ideia da arquitetura hexagonal é:

> **O código central não deve depender dos detalhes externos.**

Exemplos de adapters:

```text
Web Adapter
  → REST Controller

Persistence Adapter
  → JPA / PostgreSQL

Messaging Adapter
  → Kafka / RabbitMQ

Notification Adapter
  → Email / Firebase

External API Adapter
  → APIs de terceiros
```

Isso será especialmente útil quando o projeto ganhar:

- Kafka/RabbitMQ;
- WebSocket;
- cache;
- serviços externos;
- observabilidade;
- múltiplas fontes de dados.

---

# 11. Estrutura de pacotes recomendada

Uma estrutura inicial:

```text
src/main/java/com/github/heberleandro/trelleibackend/

├── TrelleiBackendApplication.java
│
├── domain/
│   ├── board/
│   │   ├── entity/
│   │   │   ├── Board.java
│   │   │   ├── BoardList.java
│   │   │   └── Card.java
│   │   │
│   │   ├── valueobject/
│   │   │   └── BoardColor.java
│   │   │
│   │   ├── repository/
│   │   │   └── BoardRepository.java
│   │   │
│   │   └── service/
│   │       └── BoardDomainService.java
│   │
│   └── user/
│       ├── entity/
│       │   └── User.java
│       │
│       ├── valueobject/
│       │   └── Email.java
│       │
│       └── repository/
│           └── UserRepository.java
│
├── application/
│   ├── board/
│   │   ├── create/
│   │   │   ├── CreateBoardUseCase.java
│   │   │   └── CreateBoardCommand.java
│   │   │
│   │   ├── get/
│   │   │   ├── GetBoardUseCase.java
│   │   │   └── GetBoardQuery.java
│   │   │
│   │   ├── update/
│   │   │   └── UpdateBoardUseCase.java
│   │   │
│   │   └── delete/
│   │       └── DeleteBoardUseCase.java
│   │
│   └── user/
│       └── ...
│
├── infrastructure/
│   ├── persistence/
│   │   └── jpa/
│   │       ├── entity/
│   │       │   ├── BoardJpaEntity.java
│   │       │   └── UserJpaEntity.java
│   │       │
│   │       ├── repository/
│   │       │   ├── SpringDataBoardRepository.java
│   │       │   └── JpaBoardRepositoryAdapter.java
│   │       │
│   │       └── mapper/
│   │           ├── BoardJpaMapper.java
│   │           └── UserJpaMapper.java
│   │
│   ├── messaging/
│   │   ├── kafka/
│   │   └── rabbitmq/
│   │
│   └── configuration/
│       ├── SecurityConfig.java
│       ├── JpaConfig.java
│       └── ...
│
└── interfaces/
    └── web/
        ├── board/
        │   ├── BoardController.java
        │   └── dto/
        │       ├── CreateBoardRequest.java
        │       └── BoardResponse.java
        │
        ├── auth/
        │   ├── AuthController.java
        │   └── dto/
        │       ├── LoginRequest.java
        │       └── LoginResponse.java
        │
        └── exception/
            └── GlobalExceptionHandler.java
```

> Uma evolução possível é organizar por **feature/bounded context** em vez de criar grandes pastas técnicas globais. O projeto pode chegar nisso quando o número de funcionalidades aumentar.

---

# 12. JPA e domínio

Neste projeto existe uma decisão importante de arquitetura.

## Opção A — DDD Light

As classes de domínio também são entidades JPA:

```java
@Entity
public class Board {
    ...
}
```

Vantagens:

- menos código;
- menos mappers;
- mais simples para começar.

Desvantagens:

- domínio fica acoplado ao JPA;
- regras de persistência e domínio ficam misturadas.

## Opção B — Hexagonal mais rígida

Separar:

```text
Board.java
BoardJpaEntity.java
BoardMapper.java
```

Vantagens:

- domínio realmente independente;
- testes mais simples;
- maior separação arquitetural.

Desvantagens:

- mais código;
- mais mappers;
- mais complexidade.

### Estratégia para este projeto

Começar com **DDD Light**, pois o objetivo atual é aprender o domínio e os casos de uso sem criar abstração excessiva.

Depois, em um momento planejado, criar um branch/refatoração para experimentar a separação completa de domínio e persistência.

Isso também transforma a própria evolução do projeto em uma atividade de estudo.

---

# 13. Testes

A cobertura de testes deve acompanhar a evolução da arquitetura.

## 13.1 Testes de domínio

São os testes mais rápidos.

Testar:

```text
Board
BoardList
Card
Domain Services
Value Objects
```

Exemplo:

```java
@Test
void shouldRenameBoard() {
    Board board = board();

    board.rename("Novo nome");

    assertEquals("Novo nome", board.getName());
}
```

Também testar casos inválidos:

```java
@Test
void shouldNotAcceptEmptyBoardName() {
    assertThrows(IllegalArgumentException.class, () ->
        new Board(null, "", "#FFFFFF", owner)
    );
}
```

---

## 13.2 Testes de Use Case

O objetivo é testar a orquestração.

Normalmente podemos mockar portas:

```java
@Mock
private BoardRepository boardRepository;
```

E verificar:

```text
Repository foi chamado?
Regra correta foi aplicada?
Erro esperado foi lançado?
```

---

## 13.3 Testes web

Para controllers:

```text
@WebMvcTest
MockMvc
```

Validar:

- status HTTP;
- request;
- response;
- validação;
- autenticação/autorização;
- tratamento de erros.

---

## 13.4 Testes de persistência

Usar:

```text
@DataJpaTest
```

para validar mappings, queries e comportamento do JPA.

---

## 13.5 Testes de integração

Mais adiante:

```text
@SpringBootTest
Testcontainers
PostgreSQL real
Kafka real
```

A ideia é evitar confiar somente no H2 quando o comportamento real da aplicação depende de recursos específicos do PostgreSQL.

---

# 14. H2 x PostgreSQL

Desenvolvimento local:

```text
profile: dev
→ H2
```

Produção:

```text
profile: prod
→ PostgreSQL
```

A configuração pode seguir:

```text
application.yml
application-dev.yml
application-prod.yml
application-test.yml
```

O ambiente deve ser selecionado por configuração externa, por exemplo:

```text
SPRING_PROFILES_ACTIVE=dev
```

ou:

```text
SPRING_PROFILES_ACTIVE=prod
```

### Importante

H2 é excelente para velocidade e desenvolvimento, mas não deve ser tratado como substituto perfeito do PostgreSQL.

Quando a aplicação evoluir, Testcontainers será usado para testes de integração com PostgreSQL real.

---

# 15. Migração de banco

Depois da fase inicial, estudar:

```text
Flyway
```

ou:

```text
Liquibase
```

A ideia é evitar depender de:

```yaml
spring.jpa.hibernate.ddl-auto: update
```

em ambientes reais.

Fluxo esperado:

```text
V1__create_users.sql
V2__create_boards.sql
V3__create_board_lists.sql
V4__create_cards.sql
V5__create_board_members.sql
```

Isso ensina versionamento de banco e facilita deploys reproduzíveis.

---

# 16. Autenticação e autorização

A aplicação deve evoluir para:

```text
Spring Security
      ↓
JWT
      ↓
Authentication
      ↓
Authorization
```

No início:

```text
User → Board owner
```

Depois:

```text
BoardMember
 ├── OWNER
 ├── ADMIN
 ├── MEMBER
 └── VIEWER
```

Isso abre espaço para estudar autorização baseada em papéis e permissões.

---

# 17. Evolução do domínio do Board

Uma evolução possível:

```text
Board
 ├── owner
 ├── members
 └── lists
      ├── cards
      │    ├── title
      │    ├── description
      │    ├── labels
      │    ├── dueDate
      │    ├── assignees
      │    └── comments
      │
      └── ...
```

Posteriormente:

```text
Board
 ├── Members
 ├── Lists
 ├── Cards
 ├── ActivityLog
 ├── Messages
 └── Notifications
```

Isso permitirá praticar agregados, eventos de domínio e comunicação assíncrona.

---

# 18. Mensageria

Depois que o CRUD estiver estável, adicionar mensageria.

Uma boa primeira funcionalidade é um **Activity Event**.

Exemplos:

```text
BoardCreated
BoardRenamed
CardCreated
CardMoved
MemberAdded
MemberRemoved
```

Fluxo:

```text
User moves Card
      ↓
MoveCardUseCase
      ↓
Domain
      ↓
CardMovedEvent
      ↓
Message Broker
      ↓
Consumers
 ┌──────────────┬──────────────┬──────────────┐
 ▼              ▼              ▼
Activity Log   Notification   Analytics
```

Isso ensina:

- eventos;
- produtores;
- consumidores;
- idempotência;
- retries;
- dead letter queue;
- processamento assíncrono.

---

# 19. Kafka ou RabbitMQ?

Os dois são bons para estudo, mas ensinam conceitos um pouco diferentes.

### Kafka

Interessante para estudar:

- event streaming;
- partitions;
- consumer groups;
- ordering;
- retenção;
- replay de eventos.

### RabbitMQ

Interessante para estudar:

- filas;
- exchanges;
- routing keys;
- acknowledgements;
- retries;
- dead letter queues.

### Sugestão para o projeto

Começar com **um deles**, não os dois ao mesmo tempo.

Depois, caso queira estudar arquitetura, criar um adapter alternativo e comparar as abordagens.

---

# 20. Outbox Pattern

Quando começar a publicar eventos, estudar o problema de consistência entre:

```text
Database
   +
Message Broker
```

Um fluxo ingênuo:

```text
1. Salva no banco
2. Publica no Kafka
```

Se o banco salvar e o Kafka falhar, os estados ficam inconsistentes.

Com Outbox:

```text
Transaction
 ├── Atualiza domínio
 └── Salva evento na Outbox
          ↓
     Outbox Processor
          ↓
       Kafka
```

Esse é um excelente assunto para nível pleno/sênior.

---

# 21. Chat em tempo real

Para o chat do board, estudar:

```text
WebSocket
STOMP
Redis Pub/Sub (opcional)
```

Fluxo:

```text
Browser
   ↕
WebSocket
   ↕
Backend
   ↕
Message Broker
```

Evolução futura:

```text
User A
   ↓
WebSocket Server 1
   ↓
Redis / Broker
   ↓
WebSocket Server 2
   ↓
User B
```

Isso permite estudar escalabilidade horizontal e comunicação em tempo real.

---

# 22. Observabilidade

Depois da aplicação funcional, adicionar observabilidade.

## Health check

```text
Spring Boot Actuator
```

Endpoints úteis:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

---

## Métricas

Usar:

```text
Micrometer
Prometheus
Grafana
```

Exemplos de métricas:

```text
HTTP request count
HTTP latency
Error rate
JVM memory
Database pool
Kafka consumer lag
```

---

# 23. Logs estruturados

Em vez de logs difíceis de pesquisar:

```text
Board created
```

evoluir para logs estruturados com informações como:

```text
requestId
userId
boardId
eventType
timestamp
```

Isso prepara o projeto para ferramentas como:

```text
ELK
OpenSearch
Loki
```

---

# 24. Distributed Tracing

Quando houver mais componentes, estudar:

```text
OpenTelemetry
```

Um request pode ser acompanhado:

```text
HTTP Request
   ↓
Controller
   ↓
UseCase
   ↓
Database
   ↓
Kafka
   ↓
Consumer
   ↓
Notification
```

Com tracing fica possível enxergar o caminho completo e identificar gargalos.

---

# 25. Cache

Uma próxima evolução interessante:

```text
Redis
```

Casos possíveis:

- boards mais acessados;
- sessões;
- rate limiting;
- presença de usuários no chat;
- dados temporários.

Isso abre espaço para estudar:

- cache-aside;
- TTL;
- invalidação;
- cache distribuído.

---

# 26. Concorrência e consistência

Quando múltiplos usuários puderem editar o mesmo board, estudar:

```text
Optimistic Locking
@Version
```

Exemplo:

```java
@Version
private Long version;
```

Isso ajuda a evitar problemas quando dois usuários alteram o mesmo recurso ao mesmo tempo.

Outro tema interessante:

```text
Pessimistic Locking
```

Comparar os dois no próprio projeto.

---

# 27. Rate limiting

Quando a aplicação tiver endpoints públicos, estudar:

```text
Rate Limit
```

Exemplo:

```text
POST /auth/login
```

Impedir excesso de tentativas em um intervalo de tempo.

Redis pode ser usado como armazenamento distribuído para esse controle.

---

# 28. Resiliência

Para futuras integrações externas, estudar:

```text
Resilience4j
```

Conceitos:

- Retry;
- Timeout;
- Circuit Breaker;
- Bulkhead;
- Rate Limiter.

Não aplicar tudo de uma vez. Criar uma integração externa pequena e usar ela como laboratório.

---

# 29. Documentação da API

Adicionar OpenAPI/Swagger.

A API deve documentar:

```text
POST   /auth/login
POST   /boards
GET    /boards
GET    /boards/{id}
PATCH  /boards/{id}
DELETE /boards/{id}
POST   /boards/{id}/lists
POST   /lists/{id}/cards
PATCH  /cards/{id}/move
```

Mais tarde, incluir exemplos de request/response e documentação dos erros.

---

# 30. Tratamento de erros

Criar uma estratégia global.

Exemplo:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    ...
}
```

Padronizar respostas:

```json
{
  "timestamp": "2026-09-11T12:00:00Z",
  "status": 404,
  "code": "BOARD_NOT_FOUND",
  "message": "Board not found",
  "path": "/boards/10"
}
```

Isso evita cada controller responder erros de uma maneira diferente.

---

# 31. Roadmap de desenvolvimento

## Fase 1 — Base atual

- [x] Spring Boot
- [x] User
- [x] Board
- [x] autenticação inicial
- [x] H2 para desenvolvimento
- [x] PostgreSQL para produção
- [ ] organização definitiva de pacotes

## Fase 2 — Qualidade do domínio

- [ ] refatorar `Board`
- [ ] criar regras de domínio
- [ ] definir Aggregate Root
- [ ] criar primeiros Value Objects
- [ ] remover lógica de negócio dos controllers
- [ ] criar Use Cases
- [ ] criar testes unitários

## Fase 3 — Boards completos

- [ ] BoardList
- [ ] Card
- [ ] criar lista
- [ ] renomear lista
- [ ] criar card
- [ ] editar card
- [ ] mover card
- [ ] deletar card
- [ ] testes de domínio
- [ ] testes dos Use Cases

## Fase 4 — Persistência profissional

- [ ] Flyway
- [ ] PostgreSQL
- [ ] Testcontainers
- [ ] testes de integração
- [ ] otimização de queries
- [ ] índices
- [ ] paginação

## Fase 5 — Multiusuário

- [ ] BoardMember
- [ ] roles/permissões
- [ ] convite para board
- [ ] autorização por recurso
- [ ] optimistic locking

## Fase 6 — Eventos e mensageria

- [ ] Domain Events
- [ ] Kafka ou RabbitMQ
- [ ] consumers
- [ ] retry
- [ ] dead letter
- [ ] idempotência
- [ ] Outbox Pattern

## Fase 7 — Chat

- [ ] WebSocket
- [ ] mensagens
- [ ] histórico
- [ ] presença
- [ ] notificações
- [ ] Redis, se necessário

## Fase 8 — Observabilidade

- [ ] Actuator
- [ ] Micrometer
- [ ] Prometheus
- [ ] Grafana
- [ ] logs estruturados
- [ ] OpenTelemetry
- [ ] tracing

## Fase 9 — Produção

- [ ] Docker
- [ ] docker-compose
- [ ] variáveis de ambiente
- [ ] CI/CD
- [ ] health checks
- [ ] documentação da API
- [ ] segurança
- [ ] rate limiting

---

# 32. Ordem recomendada de implementação

Para não cair na armadilha de tentar implementar tudo ao mesmo tempo:

```text
1. Board
   ↓
2. Tests
   ↓
3. Use Cases
   ↓
4. BoardList
   ↓
5. Card
   ↓
6. Integration tests
   ↓
7. Flyway
   ↓
8. Multi-user
   ↓
9. Events
   ↓
10. Kafka/RabbitMQ
   ↓
11. WebSocket
   ↓
12. Redis
   ↓
13. Observability
   ↓
14. Docker/CI/CD
```

---

# 33. Checklist para cada nova funcionalidade

Antes de implementar uma funcionalidade, responder:

### Domínio

- Qual é a regra de negócio?
- Qual entidade possui essa responsabilidade?
- Preciso de um Value Object?
- Existe uma invariável que precisa ser protegida?
- Essa regra pertence a um Domain Service?

### Application

- Qual é o Use Case?
- Quais portas ele precisa?
- Qual é a entrada?
- Qual é a saída?
- Quais exceções de negócio podem ocorrer?

### Web

- Qual endpoint representa esse caso de uso?
- Qual DTO entra?
- Qual DTO sai?
- Como serão os erros HTTP?

### Persistência

- Preciso alterar o modelo?
- Preciso de uma migration?
- Existe algum problema de performance?
- Preciso de índice?

### Testes

- Testei a regra do domínio?
- Testei o Use Case?
- Testei o endpoint?
- Preciso de um teste de integração com banco real?

### Operação

- Preciso de log?
- Preciso de métrica?
- Essa operação pode ser assíncrona?
- Existe algum evento que deveria ser publicado?

---

# 34. Objetivo final do projeto

Ao final, o Trellei deve ser mais do que um clone visual do Trello.

Ele deve funcionar como um **projeto de laboratório para engenharia de software**, permitindo estudar o ciclo completo:

```text
Domínio
   ↓
Código
   ↓
Testes
   ↓
Persistência
   ↓
API
   ↓
Segurança
   ↓
Mensageria
   ↓
Cache
   ↓
Observabilidade
   ↓
Containerização
   ↓
Deploy
```

A ideia é poder olhar para cada camada e responder:

> **Por que esta decisão arquitetural existe? Qual problema ela resolve? Quais são os trade-offs?**

Esse é o principal objetivo de estudo do projeto.

---

# 35. Regra de ouro do projeto

Não adicionar tecnologia apenas para poder colocar a tecnologia no currículo.

Adicionar quando ela permitir estudar um problema real:

```text
Kafka      → eventos / processamento assíncrono
Redis      → cache / estado distribuído
WebSocket  → comunicação em tempo real
Flyway     → versionamento de banco
Testcontainers → integração realista
Prometheus → métricas
Grafana    → visualização
OpenTelemetry → tracing
Resilience4j → resiliência
```

Assim cada tecnologia terá um **motivo arquitetural** dentro do projeto.
