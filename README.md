# Golden Raspberry Awards API

API RESTful em Spring Boot para carregar um arquivo CSV com indicados e vencedores da categoria **Worst Picture** do Golden Raspberry Awards, persistir os dados em banco H2 em memoria e expor o intervalo minimo e maximo entre vitorias consecutivas por produtor.

## Visao geral

- Le o CSV na inicializacao da aplicacao.
- Persiste filmes e produtores em H2 em memoria.
- Considera apenas filmes vencedores para o calculo.
- Retorna todos os produtores empatados no menor e no maior intervalo.
- Disponibiliza documentacao interativa com Swagger UI.

## Stack utilizada

- Java 25
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- H2 Database
- Apache Commons CSV
- Springdoc OpenAPI
- Swagger UI estatico
- JUnit 5
- Spring Boot Test
- MockMvc
- Maven Wrapper

## Decisoes arquiteturais

- **Arquitetura em camadas**: `controller`, `service`, `repository`, `entity`, `dto`, `mapper`, `config`, `exception` e `loader`.
- **Regra de negocio fora do controller**: o controller recebe a requisicao, delega ao servico e devolve a resposta.
- **Persistencia normalizada**: filmes e produtores sao persistidos em tabelas separadas com relacao N:N.
- **Bootstrap idempotente**: o carregamento inicial so ocorre se a base estiver vazia.
- **Fail fast estrutural**: CSV inexistente, ilegivel ou com headers invalidos derruba a inicializacao explicitamente.
- **Resposta deterministica**: listas `min` e `max` sao ordenadas por `producer`, `previousWin` e `followingWin`.

## Estrutura do projeto

```text
.
|-- .mvn/
|-- mvnw
|-- mvnw.cmd
|-- pom.xml
|-- src/
|   |-- main/
|   |   |-- java/com/outsera/razzies/
|   |   |   |-- controller/
|   |   |   |-- service/
|   |   |   |-- repository/
|   |   |   |-- entity/
|   |   |   |-- dto/
|   |   |   |-- mapper/
|   |   |   |-- config/
|   |   |   |-- exception/
|   |   |   `-- loader/
|   |   `-- resources/
|   |       |-- application.yml
|   |       `-- data/movielist.csv
|   `-- test/
|       |-- java/com/outsera/razzies/
|       `-- resources/
|           |-- application-test.yml
|           `-- datasets/
`-- README.md
```

## Como executar

### Pre-requisito

- Java 25 instalado e disponivel no `PATH`

### Subir a aplicacao

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A API sobe por padrao em:

```text
http://localhost:8080
```

### Swagger

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Como rodar os testes

No Linux/macOS:

```bash
./mvnw test
```

No Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Endpoint disponivel

```http
GET /api/v1/producers/award-intervals
```

### Exemplo de request

```bash
curl http://localhost:8080/api/v1/producers/award-intervals
```

### Exemplo de response

Com o dataset padrao deste repositorio, o resultado esperado e:

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

## Configuracao

### Banco em memoria

- Banco: H2
- URL: `jdbc:h2:mem:razziesdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Console H2 habilitado em: `http://localhost:8080/h2-console`

### Fonte do CSV

Por padrao, a aplicacao le:

```text
classpath:data/movielist.csv
```

E possivel sobrescrever a origem do arquivo:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--app.csv.localizacao=file:C:/dados/Movielist.csv"
```

No Windows, tambem funciona passando o caminho absoluto direto:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--app.csv.localizacao=C:/Users/Thales Alonso/Documents/filmes_brasileiros.csv"
```

## Regras de parsing do CSV

- O delimitador esperado e `;`.
- Headers obrigatorios: `year`, `title`, `studios`, `producers`, `winner`.
- O campo `winner` e considerado vencedor quando vier com qualquer valor nao vazio.
- O campo `producers` suporta multiplos produtores separados por virgula e/ou `and`.
- Espacos extras sao removidos e multiplos espacos internos sao normalizados.
- Produtores sao deduplicados por nome normalizado para evitar duplicidades causadas por whitespace.
- Linhas com `year` invalido ou `title`/`producers` vazios sao ignoradas com log de alerta.

## Testes de integracao implementados

- carga correta do CSV padrao
- persistencia em H2
- parsing de multiplos produtores no mesmo campo
- resposta do endpoint principal
- contrato JSON da resposta
- empate em `min`
- empate em `max`
- validacao de que apenas vencedores entram no calculo
- cenario sem intervalos validos, retornando listas vazias

## Observabilidade

Logs `INFO` foram adicionados para:

- inicio do bootstrap do CSV
- quantidade de registros validos processados
- quantidade de vencedores persistidos
- conclusao do bootstrap
- execucao do calculo principal dos intervalos

## Melhorias futuras

- expor metricas com Spring Boot Actuator
- suportar paginacao e filtros para novas consultas
- adicionar profile opcional com banco persistente
- versionar contratos com testes de consumer contract, se a API crescer
