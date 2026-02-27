package com.example.ms_report_async.infraestructure.async;

import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class RabbitImportListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitImportListener.class);
    private final ProcessImportFileUseCase useCase;
    private final ObjectMapper objectMapper;

    public RabbitImportListener(ProcessImportFileUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String jsonMessage) {
        logger.info("Mensagem recebida do RabbitMQ: {}", jsonMessage);

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);
            String jobId = jsonNode.get("jobId").toString();
            String fileKey = jsonNode.get("fileKey").asText();

            logger.debug("Iniciando processamento. JobId: {}, FileKey: {}", jobId, fileKey);
            useCase.execute(fileKey, jobId);
            logger.info("Processamento concluído com sucesso. JobId: {}, FileKey: {}", jobId, fileKey);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem JSON: {}", jsonMessage, e);
            throw new RuntimeException("Falha ao ler JSON do RabbitMQ", e);
        }
    }
}

