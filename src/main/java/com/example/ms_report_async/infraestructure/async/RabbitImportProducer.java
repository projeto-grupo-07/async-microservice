package com.example.ms_report_async.infraestructure.async;


import com.example.ms_report_async.application.dto.JobMessage;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitImportProducer {

    private final RabbitTemplate rabbit;

    public RabbitImportProducer(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    public String publish(String fileKey) {
        String jobId = UUID.randomUUID().toString();

        String jsonPayload = String.format("{\"jobId\":\"%s\", \"fileKey\":\"%s\"}", jobId, fileKey);

        rabbit.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                jsonPayload
        );

        return jobId;
    }
}

