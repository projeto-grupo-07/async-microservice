# MS Report Async

Microserviço assíncrono responsável por processar arquivos CSV, consolidar dados e gerar relatórios em PDF. O projeto foi construído com **Spring Boot 3**, **Java 21** e segue uma arquitetura em camadas inspirada em **Clean Architecture / Ports & Adapters**.

## Visão geral

O fluxo principal funciona assim:

1. Uma mensagem é publicada na fila do RabbitMQ com os dados do job.
2. O serviço consome essa mensagem de forma assíncrona.
3. O arquivo CSV é baixado do bucket S3 de origem (`trusted`).
4. O CSV é parseado e consolidado.
5. Um PDF com o relatório é gerado e armazenado no S3.
6. O relatório pode ser consultado depois via endpoint REST.

O relatório gerado contempla os principais agrupamentos do processo:

- **Top 5 produtos com menor preço médio**
- **Top 5 produtos com maior volume total**
- **Top 5 produtos com menor frete médio**

## Stack utilizada

- Java 21
- Spring Boot 3.4.4
- Spring Web
- Spring AMQP / RabbitMQ
- AWS SDK v2 para S3
- Apache Commons CSV
- OpenPDF
- SpringDoc OpenAPI / Swagger
- Lombok

## Estrutura do projeto

```text
src/main/java/com/example/ms_report_async/
├── application
│   ├── dto
│   └── usecase
├── domain
│   ├── entity
│   ├── repository
│   └── service
└── infraestructure
    ├── async
    ├── config
    ├── csv
    ├── logging
    ├── pdf
    ├── rest
    └── s3
```

## Requisitos

- Java 21
- Maven 3.9+ ou o wrapper do projeto (`./mvnw`)
- RabbitMQ disponível para o consumo da fila
- Acesso ao AWS S3

## Como executar localmente

### 1. Configurar variáveis de ambiente

O projeto usa valores padrão em `application.properties`, mas você pode sobrescrevê-los com variáveis de ambiente:

- `RABBITMQ_PORT` (padrão: `5672`)
- `RABBITMQ_USER` (padrão: `admin`)
- `RABBITMQ_PASS` (padrão: `admin`)
- `AWS_S3_BUCKET_TRUSTED` (padrão: `brinks-bucket-trusted`)
- `AWS_S3_BUCKET_CLIENT` (padrão: `brinks-bucket-client`)

### 2. Subir o projeto

```bash
./mvnw spring-boot:run
```

A aplicação sobe na porta **8081**.

### 3. Gerar o pacote

```bash
./mvnw clean package
```

## Executando com Docker

### Build da imagem

```bash
docker build -t ms-report-async .
```

### Subir o container

```bash
docker run --rm -p 8081:8081 \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USER=admin \
  -e RABBITMQ_PASS=admin \
  -e AWS_S3_BUCKET_TRUSTED=brinks-bucket-trusted \
  -e AWS_S3_BUCKET_CLIENT=brinks-bucket-client \
  ms-report-async
```

## Endpoints

### Health check

- `GET /health`
- Retorna `200 OK` com o texto `Ok!`

Exemplo:

```bash
curl http://localhost:8081/health
```

### Download do relatório PDF

- `GET /import/report`
- Parâmetros esperados:
  - `ano`
  - `mes`
  - `jobId`

Exemplo:

```bash
curl -o relatorio.pdf "http://localhost:8081/import/report?ano=2024&mes=8&jobId=job-123"
```

Se o relatório existir, a resposta será um PDF com `Content-Type: application/pdf`.

## Integração com RabbitMQ

O serviço consome mensagens da fila:

- **Queue**: `import_csv_queue`
- **Exchange**: `import_exchange`
- **Routing key**: `import.csv`

O payload esperado segue este formato:

```json
{
  "ano": 2024,
  "mes": 8,
  "jobId": "job-123",
  "fileKey": "arquivo.csv"
}
```

## OpenAPI / Swagger

A documentação interativa fica disponível em:

- `http://localhost:8081/swagger-ui.html`

## Configuração de logs

Os logs são gravados em:

- `logs/ms-report-async.log`

Também há configuração de rotação de logs no `application.properties` e no `logback-spring.xml`.

## Observações

- O bucket S3 usado como origem dos arquivos é o **trusted bucket**.
- O relatório gerado é armazenado de forma organizada por caminho no formato `ano=YYYY/mes=MM/`.
- O projeto já inclui um `Dockerfile` com build em múltiplos estágios.

## Licença

Este projeto usa licença MIT conforme a configuração da documentação OpenAPI.

