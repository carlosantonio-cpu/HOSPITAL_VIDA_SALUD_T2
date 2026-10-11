package com.andinacapital.notificacionservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication(scanBasePackages = "com.andinacapital")
public class NotificacionServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                NotificacionServiceApplication.class,
                args
        );
    }
}