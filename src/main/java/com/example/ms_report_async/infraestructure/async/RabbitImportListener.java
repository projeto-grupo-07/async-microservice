package com.example.ms_report_async.infraestructure.async;

import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitImportListener {

    private static final Logger logger = LoggerFactory.getLogger(RabbitImportListener.class);
    private final ProcessImportFileUseCase useCase;
    private final Gson gson;

    public RabbitImportListener(ProcessImportFileUseCase useCase) {
        this.useCase = useCase;
        this.gson = new Gson();
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String jsonMessage) {
        logger.info("Mensagem recebida do RabbitMQ: {}", jsonMessage);

        try {
            JsonObject jsonObject = gson.fromJson(jsonMessage, JsonObject.class);
            
            String jobId = jsonObject.get("jobId").getAsString();
            String fileKey = jsonObject.get("fileKey").getAsString();

            logger.debug("Iniciando processamento. JobId: {}, FileKey: {}", jobId, fileKey);
            useCase.execute(fileKey, jobId);
            logger.info("Processamento concluído com sucesso. JobId: {}, FileKey: {}", jobId, fileKey);

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem JSON: {}", jsonMessage, e);
            throw new RuntimeException("Falha ao ler JSON do RabbitMQ", e);
        }
    }
}
