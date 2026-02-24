package com.example.ms_report_async.infraestructure.async;


import com.example.ms_report_async.application.dto.JobMessage;
import com.example.ms_report_async.domain.service.ProcessImportFileUseCase;
import com.example.ms_report_async.infraestructure.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitImportListener {

    private final ProcessImportFileUseCase useCase;

    public RabbitImportListener(ProcessImportFileUseCase useCase) {
        this.useCase = useCase;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void receive(JobMessage message) {
        useCase.execute(message.fileKey());
    }
}

