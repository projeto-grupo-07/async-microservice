package com.example.ms_report_async.infraestructure.async;

import com.example.ms_report_async.application.dto.JobMessage;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitImportProducer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitImportProducer.class);
    private final RabbitTemplate rabbit;

    public RabbitImportProducer(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public String publish(String fileKey) {
        String jobId = UUID.randomUUID().toString();
        logger.debug("Gerando novo JobId: {} para FileKey: {}", jobId, fileKey);

        String jsonPayload = String.format("{\"jobId\":\"%s\", \"fileKey\":\"%s\"}", jobId, fileKey);

        try {
            rabbit.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY,
                    jsonPayload
            );
            logger.info("Mensagem publicada no RabbitMQ com sucesso. Exchange: {}, RoutingKey: {}, JobId: {}",
                    RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, jobId);
        } catch (Exception e) {
            logger.error("Erro ao publicar mensagem no RabbitMQ para JobId: {}", jobId, e);
            throw new RuntimeException("Falha ao publicar mensagem no RabbitMQ", e);
        }

        return jobId;
    }
}

