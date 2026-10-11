package com.andinacapital.notificacion.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE = "cita.confirmada.queue";

    @Bean
    public Queue citaConfirmadaQueue() {
        return new Queue(QUEUE, true);
    }
}