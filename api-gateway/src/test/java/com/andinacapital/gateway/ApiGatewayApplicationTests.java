package com.andinacapital.gateway;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void falloControladoParaEvidenciaCI() {
        Assertions.fail("Fallo controlado para demostrar GitHub Actions");
    }
}