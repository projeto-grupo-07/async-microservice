package com.example.ms_report_async.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.QueueBuilder;


import java.util.Queue;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "import_csv_queue";
    public static final String EXCHANGE = "import_exchange";
    public static final String ROUTING_KEY = "import.csv";

    @Bean
    public Queue importQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public DirectExchange importExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding importBinding() {
        return BindingBuilder
                .bind(importQueue())
                .to(importExchange())
                .with(ROUTING_KEY);
    }
}
