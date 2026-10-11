package com.andinacapital.notificacion.listener;

import com.andinacapital.notificacion.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionListener {

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void recibirCitaConfirmada(String mensaje) {
        System.out.println("========================================");
        System.out.println("     NOTIFICACION DE CITA CONFIRMADA");
        System.out.println("========================================");
        System.out.println("Evento recibido desde RabbitMQ:");
        System.out.println(mensaje);
        System.out.println("========================================");
    }
}