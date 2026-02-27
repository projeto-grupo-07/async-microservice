package com.example.ms_report_async.infraestructure.logging;

import org.slf4j.Logger;
import org.slf4j.MDC;

/**
 * Classe utilitária para logging estruturado e profissional.
 *
 * Fornece métodos convenientes para logging com contexto de negócio,
 * como JobId, FileKey, e medição de tempo de execução.
 */
public class LoggingUtil {

    private static final String JOB_ID_KEY = "jobId";
    private static final String FILE_KEY_KEY = "fileKey";
    private static final String USER_KEY = "user";

    /**
     * Define o JobId no contexto MDC (Mapped Diagnostic Context)
     * para que apareça automaticamente em todos os logs subsequentes
     */
    public static void setJobId(String jobId) {
        if (jobId != null) {
            MDC.put(JOB_ID_KEY, jobId);
        }
    }

    /**
     * Define o FileKey no contexto MDC
     */
    public static void setFileKey(String fileKey) {
        if (fileKey != null) {
            MDC.put(FILE_KEY_KEY, fileKey);
        }
    }

    /**
     * Define o usuário no contexto MDC
     */
    public static void setUser(String user) {
        if (user != null) {
            MDC.put(USER_KEY, user);
        }
    }

    /**
     * Limpa o contexto MDC
     */
    public static void clearContext() {
        MDC.clear();
    }

    /**
     * Registra o início de uma operação
     */
    public static void logOperationStart(Logger logger, String operation, Object... args) {
        logger.info("Iniciando operação: {}", operation, args);
    }

    /**
     * Registra o sucesso de uma operação com tempo de execução
     */
    public static void logOperationSuccess(Logger logger, String operation, long startTimeMs) {
        long duration = System.currentTimeMillis() - startTimeMs;
        logger.info("Operação concluída com sucesso: {}. Duração: {}ms", operation, duration);
    }

    /**
     * Registra o erro de uma operação com tempo de execução
     */
    public static void logOperationError(Logger logger, String operation, long startTimeMs, Exception e) {
        long duration = System.currentTimeMillis() - startTimeMs;
        logger.error("Erro ao executar operação: {}. Duração: {}ms", operation, duration, e);
    }

    /**
     * Registra o sucesso de uma operação com tempo de execução e detalhes
     */
    public static void logOperationSuccess(Logger logger, String operation, long startTimeMs, String details) {
        long duration = System.currentTimeMillis() - startTimeMs;
        logger.info("Operação concluída com sucesso: {}. Detalhes: {}. Duração: {}ms", operation, details, duration);
    }
}

