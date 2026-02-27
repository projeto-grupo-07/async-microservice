package com.example.ms_report_async.infraestructure.logging;

/**
 * EXEMPLOS DE USO DO SISTEMA DE LOGGING PROFISSIONAL
 *
 * Esta classe contém exemplos práticos de como utilizar o sistema de logging
 * em diferentes cenários da aplicação.
 */
public class LoggingExamples {

    /*
    ========================================
    EXEMPLO 1: LOGGING BÁSICO EM CONTROLLER
    ========================================

    @RestController
    public class ImportControllerImpl {
        private static final Logger logger = LoggerFactory.getLogger(ImportControllerImpl.class);

        @PostMapping("/{fileKey}")
        public ResponseEntity<JobResponse> importFile(@PathVariable String fileKey) {
            logger.info("Recebendo requisição de importação. FileKey: {}", fileKey);
            try {
                String jobId = producer.publish(fileKey);
                logger.info("Importação enfileirada com sucesso. JobId: {}, FileKey: {}", jobId, fileKey);
                return ResponseEntity.accepted().body(new JobResponse(jobId));
            } catch (Exception e) {
                logger.error("Erro ao enfileirar importação para FileKey: {}", fileKey, e);
                throw e;
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 2: LOGGING COM MDC EM SERVICE
    ========================================

    @Service
    public class ProcessImportFileUseCaseImpl {
        private static final Logger logger = LoggerFactory.getLogger(ProcessImportFileUseCaseImpl.class);

        public String execute(String fileKey, String jobId) {
            // Definir JobId no contexto MDC para aparecer em todos os logs
            LoggingUtil.setJobId(jobId);
            LoggingUtil.setFileKey(fileKey);

            logger.info("Iniciando processamento. FileKey: {}", fileKey);
            long startTime = System.currentTimeMillis();

            try {
                // ... processamento
                LoggingUtil.logOperationSuccess(logger, "Processamento completo", startTime);
                return result;
            } catch (Exception e) {
                LoggingUtil.logOperationError(logger, "Processamento", startTime, e);
                throw e;
            } finally {
                // Limpar contexto após operação
                LoggingUtil.clearContext();
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 3: LOGGING COM MEDIÇÃO DE TEMPO
    ========================================

    public class CsvParserAdapter implements CsvParserPort {
        private static final Logger logger = LoggerFactory.getLogger(CsvParserAdapter.class);

        public List<ImportRow> parse(InputStream input) {
            logger.info("Iniciando parsing do arquivo CSV");
            long startTime = System.currentTimeMillis();

            try {
                List<ImportRow> rows = doParse(input);
                long duration = System.currentTimeMillis() - startTime;

                logger.info("CSV parsing concluído com sucesso. TotalLinhas: {}, DuracaoMs: {}",
                    rows.size(), duration);
                return rows;
            } catch (Exception e) {
                long duration = System.currentTimeMillis() - startTime;
                logger.error("Erro ao fazer parsing do CSV. DuracaoMs: {}", duration, e);
                throw new RuntimeException("Error parsing CSV", e);
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 4: LOGGING DE OPERAÇÕES S3
    ========================================

    public class S3Adapter implements S3Port {
        private static final Logger logger = LoggerFactory.getLogger(S3Adapter.class);

        public InputStream download(String bucket, String key) {
            logger.info("Iniciando download do S3. Bucket: {}, Key: {}", bucket, key);
            try {
                InputStream stream = s3Client.getObject(req);
                logger.debug("Download do S3 iniciado com sucesso. Bucket: {}, Key: {}", bucket, key);
                return stream;
            } catch (Exception e) {
                logger.error("Erro ao fazer download do S3. Bucket: {}, Key: {}", bucket, key, e);
                throw e;
            }
        }

        public void upload(String bucket, String key, byte[] content, String contentType) {
            logger.info("Iniciando upload para S3. Bucket: {}, Key: {}, TamanhoBytes: {}",
                bucket, key, content.length);
            try {
                s3Client.putObject(req, RequestBody.fromBytes(content));
                logger.info("Upload para S3 concluído com sucesso. Bucket: {}, Key: {}", bucket, key);
            } catch (Exception e) {
                logger.error("Erro ao fazer upload para S3. Bucket: {}, Key: {}", bucket, key, e);
                throw e;
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 5: LOGGING EM MESSAGE LISTENER
    ========================================

    @Component
    public class RabbitImportListener {
        private static final Logger logger = LoggerFactory.getLogger(RabbitImportListener.class);

        @RabbitListener(queues = RabbitMQConfig.QUEUE)
        public void receive(String jsonMessage) {
            logger.info("Mensagem recebida do RabbitMQ: {}", jsonMessage);

            try {
                JsonNode jsonNode = objectMapper.readTree(jsonMessage);
                String jobId = jsonNode.get("jobId").toString();
                String fileKey = jsonNode.get("fileKey").asText();

                logger.debug("Iniciando processamento. JobId: {}, FileKey: {}", jobId, fileKey);
                useCase.execute(fileKey, jobId);
                logger.info("Processamento concluído com sucesso. JobId: {}", jobId);

            } catch (Exception e) {
                logger.error("Erro ao processar mensagem JSON: {}", jsonMessage, e);
                throw new RuntimeException("Falha ao ler JSON do RabbitMQ", e);
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 6: LOGGING COM UTIL - FORMA CONCISA
    ========================================

    public class SomeService {
        private static final Logger logger = LoggerFactory.getLogger(SomeService.class);

        public void procesarDados(String jobId, String dados) {
            LoggingUtil.setJobId(jobId);

            long startTime = System.currentTimeMillis();
            try {
                // ... processar
                LoggingUtil.logOperationSuccess(logger, "Processamento de dados", startTime,
                    "100 registros processados");
            } catch (Exception e) {
                LoggingUtil.logOperationError(logger, "Processamento de dados", startTime, e);
            } finally {
                LoggingUtil.clearContext();
            }
        }
    }
    */

    /*
    ========================================
    EXEMPLO 7: SAÍDA ESPERADA NO CONSOLE
    ========================================

    Desenvolvimento (dev profile):

    2026-02-26 14:32:10.543 [http-nio-8080-exec-1] INFO com.example.ms_report_async.infraestructure.rest.impl.ImportControllerImpl [uuid-abc123] - Recebendo requisição de importação. FileKey: dados.csv
    2026-02-26 14:32:10.645 [http-nio-8080-exec-1] DEBUG com.example.ms_report_async.infraestructure.async.RabbitImportProducer [uuid-abc123] - Gerando novo JobId: uuid-123 para FileKey: dados.csv
    2026-02-26 14:32:10.750 [http-nio-8080-exec-1] INFO com.example.ms_report_async.infraestructure.async.RabbitImportProducer [uuid-123] - Mensagem publicada no RabbitMQ com sucesso
    2026-02-26 14:32:12.500 [SimpleAsyncTaskExecutor-1] INFO com.example.ms_report_async.infraestructure.async.RabbitImportListener [uuid-123] - Processamento concluído com sucesso. JobId: uuid-123

    Observe:
    - [uuid-123] é o JobId vindo do MDC (Mapped Diagnostic Context)
    - Cores diferentes para cada nível (INFO=azul, DEBUG=verde, ERROR=vermelho)
    - ThreadName entre parênteses
    - Timestamp com milissegundos
    */

    /*
    ========================================
    EXEMPLO 8: ARQUIVO DE LOG GERADO
    ========================================

    logs/ms-report-async.log:

    2026-02-26 14:32:10.543 [http-nio-8080-exec-1] INFO com.example.ms_report_async.infraestructure.rest.impl.ImportControllerImpl [uuid-abc123] - Recebendo requisição de importação. FileKey: dados.csv
    2026-02-26 14:32:10.645 [http-nio-8080-exec-1] DEBUG com.example.ms_report_async.infraestructure.async.RabbitImportProducer [uuid-abc123] - Gerando novo JobId
    2026-02-26 14:32:10.750 [http-nio-8080-exec-1] INFO com.example.ms_report_async.infraestructure.async.RabbitImportProducer [uuid-123] - Mensagem publicada
    2026-02-26 14:32:12.500 [SimpleAsyncTaskExecutor-1] INFO com.example.ms_report_async.infraestructure.async.RabbitImportListener [uuid-123] - Processamento concluído
    2026-02-26 14:33:05.200 [http-nio-8080-exec-2] ERROR com.example.ms_report_async.infraestructure.s3.S3Adapter [uuid-def456] - Erro ao fazer download do S3
    java.lang.Exception: NoSuchKeyException
        at software.amazon.awssdk.services.s3.S3Client.getObject(S3Client.java:...)
        ...

    logs/ms-report-async-error.log (apenas erros):

    2026-02-26 14:33:05.200 [http-nio-8080-exec-2] ERROR com.example.ms_report_async.infraestructure.s3.S3Adapter [uuid-def456] - Erro ao fazer download do S3
    */

    /*
    ========================================
    EXEMPLO 9: BUSCANDO LOGS POR JOBID
    ========================================

    # Ver todos os logs de uma operação específica:
    $ grep "uuid-123" logs/ms-report-async.log

    # Ver apenas erros de uma operação:
    $ grep "uuid-123" logs/ms-report-async-error.log

    # Ver logs em um intervalo de tempo:
    $ grep "2026-02-26 14:32" logs/ms-report-async.log

    # Contar total de erros:
    $ grep "ERROR" logs/ms-report-async-error.log | wc -l

    # Ver últimos 100 logs em tempo real:
    $ tail -100f logs/ms-report-async.log
    */

    /*
    ========================================
    EXEMPLO 10: DIFERENÇAS ANTES E DEPOIS
    ========================================

    ANTES (SEM LOGGING PROFISSIONAL):

    System.out.println("bucket name: " + bucketName);
    System.out.println("MENSAGEM RECEBIDA DO RABBITMQ: " + jsonMessage);
    System.err.println("Erro ao processar mensagem JSON: " + e.getMessage());

    PROBLEMAS:
    - Sem timestamp
    - Sem nível de severidade
    - Sem contexto (JobId, FileKey)
    - Sem arquivo de log
    - Sem medição de tempo
    - Sem separação de erros
    - Difícil de filtrar e buscar


    DEPOIS (COM LOGGING PROFISSIONAL):

    logger.info("Bucket name: {}", bucketName);
    logger.info("Mensagem recebida do RabbitMQ: {}", jsonMessage);
    logger.error("Erro ao processar mensagem JSON", e);

    BENEFÍCIOS:
    + Timestamp automático
    + Nível de severidade (INFO, ERROR, etc)
    + Contexto MDC (JobId, FileKey)
    + Salva em arquivo automaticamente
    + Medição de duração
    + Arquivo dedicado para erros
    + Fácil de filtrar com grep/tail
    + Stack trace completo
    + Rolling files automático
    */

    /*
    ========================================
    RESUMO - QUANDO USAR CADA NÍVEL
    ========================================

    logger.debug(...)
    - Informações de desenvolvimento
    - Valores de variáveis internas
    - Passos do processamento
    - Só visível em profile dev

    logger.info(...)
    - Início de operações importantes
    - Conclusão com sucesso
    - Transições de estado
    - Requisições aceitas

    logger.warn(...)
    - Situações incomuns mas recuperáveis
    - Valores fora do esperado
    - Deprecated methods

    logger.error(...)
    - Erros de negócio
    - Exceções não tratadas
    - Falhas críticas
    - Stack trace completo
    */
}

