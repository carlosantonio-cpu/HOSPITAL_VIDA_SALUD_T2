package com.andinacapital.prestamo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "hospital.exchange";
    public static final String QUEUE = "cita.confirmada.queue";
    public static final String ROUTING_KEY = "cita.confirmada";

    @Bean
    public TopicExchange hospitalExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue citaConfirmadaQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding citaConfirmadaBinding(
            Queue citaConfirmadaQueue,
            TopicExchange hospitalExchange) {

        return BindingBuilder
                .bind(citaConfirmadaQueue)
                .to(hospitalExchange)
                .with(ROUTING_KEY);
    }
}