package br.ufg.inf.labsimulacoes.acesso.infraestrutura;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration(proxyBeanMethods = false)
class ConfiguracaoDeSenha {

    private static final int BYTES_DO_SALT = 16;
    private static final int BYTES_DO_HASH = 32;
    private static final int PARALELISMO = 1;
    private static final int MEMORIA_KIB = 19_456;
    private static final int ITERACOES = 2;

    /** Argon2id com os parâmetros mínimos recomendados pela OWASP (RNF-09). */
    @Bean
    PasswordEncoder codificadorDeSenha() {
        return new Argon2PasswordEncoder(BYTES_DO_SALT, BYTES_DO_HASH, PARALELISMO, MEMORIA_KIB, ITERACOES);
    }
}
