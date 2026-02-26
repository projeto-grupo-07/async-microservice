package com.example.ms_report_async.infraestructure.async;


import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class RabbitImportListener {

    private final ProcessImportFileUseCase useCase;
    private final ObjectMapper objectMapper;

    public RabbitImportListener(ProcessImportFileUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(String jsonMessage) {
        System.out.println("MENSAGEM RECEBIDA DO RABBITMQ: " + jsonMessage);

        try {
            // 3. Extraia os valores de forma segura lendo o JSON
            JsonNode jsonNode = objectMapper.readTree(jsonMessage);
            String jobId = jsonNode.get("jobId").toString();
            String fileKey = jsonNode.get("fileKey").asText();

            // 4. Repasse os dois para o UseCase
            useCase.execute(fileKey, jobId);

        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem JSON: " + e.getMessage());
            throw new RuntimeException("Falha ao ler JSON do RabbitMQ", e);
        }
    }
}

