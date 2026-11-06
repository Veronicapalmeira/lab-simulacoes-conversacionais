package br.ufg.inf.labsimulacoes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeHealth;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(InfraestruturaDeTeste.class)
class LabSimulacoesApplicationTests {

    @Autowired
    private HealthEndpoint saude;

    @Test
    void contextoSobeConectadoAoBancoEAoRedis() {
        CompositeHealth resultado = (CompositeHealth) saude.health();

        assertThat(resultado.getStatus()).isEqualTo(Status.UP);
        assertThat(resultado.getComponents()).containsKeys("db", "redis");
    }
}
