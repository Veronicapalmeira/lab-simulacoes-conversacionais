package br.ufg.inf.labsimulacoes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * As migrations só adicionam estruturas, para que a imagem anterior continue funcionando no mesmo
 * banco depois de uma reversão (RNF-12).
 */
@SpringBootTest
@Import(InfraestruturaDeTeste.class)
class MigrationsTest {

    private static final String MIGRATIONS = "classpath:db/migration";
    private static final String FUTURA = "classpath:db/migration-futura";
    private static final String ATRASADA = "classpath:db/migration-atrasada";

    /** Comandos que removem ou alteram algo que a imagem anterior pode estar usando. */
    private static final Pattern DESTRUTIVO = Pattern.compile(
            "\\b(DROP|RENAME|ALTER\\s+COLUMN|DELETE\\s+FROM)\\b|(?<!BEFORE\\s)\\bTRUNCATE\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern COMENTARIOS_E_TEXTOS = Pattern.compile("--[^\\n]*|'[^']*'");

    @Autowired
    private Flyway flyway;

    @Test
    void migrationsNaoTemComandosDestrutivos() throws IOException {
        Resource[] arquivos = new PathMatchingResourcePatternResolver().getResources(MIGRATIONS + "/*.sql");

        assertThat(arquivos).isNotEmpty();
        for (Resource arquivo : arquivos) {
            String sql = arquivo.getContentAsString(StandardCharsets.UTF_8);
            assertThat(comandosDestrutivos(sql)).as(arquivo.getFilename()).isFalse();
        }
    }

    @Test
    void verificacaoDetectaComandosDestrutivos() {
        assertThat(comandosDestrutivos("ALTER TABLE turma DROP COLUMN rotulo;")).isTrue();
        assertThat(comandosDestrutivos("ALTER TABLE turma RENAME TO classe;")).isTrue();
        assertThat(comandosDestrutivos("ALTER TABLE turma ALTER COLUMN rotulo TYPE INTEGER;")).isTrue();
        assertThat(comandosDestrutivos("TRUNCATE turma;")).isTrue();
        assertThat(comandosDestrutivos("DELETE FROM turma;")).isTrue();
    }

    @Test
    void verificacaoIgnoraComentariosETextosEAceitaComandosAditivos() {
        assertThat(comandosDestrutivos("-- DROP TABLE turma")).isFalse();
        assertThat(comandosDestrutivos("RAISE EXCEPTION 'DROP não é permitido';")).isFalse();
        assertThat(comandosDestrutivos("CREATE TRIGGER t BEFORE TRUNCATE ON turma")).isFalse();
        assertThat(comandosDestrutivos("ALTER TABLE turma ADD COLUMN sala TEXT;")).isFalse();
    }

    @Test
    void imagemAnteriorSobeEmBancoComMigrationMaisNova() {
        flywayNoEsquema("reversao", MIGRATIONS, FUTURA).migrate();

        assertThatCode(() -> flywayNoEsquema("reversao", MIGRATIONS).migrate()).doesNotThrowAnyException();
    }

    @Test
    void migrationMaisAntigaDeBranchMescladaDepoisEAplicada() {
        flywayNoEsquema("fora_de_ordem", MIGRATIONS).migrate();

        var resultado = flywayNoEsquema("fora_de_ordem", MIGRATIONS, ATRASADA).migrate();

        assertThat(resultado.migrationsExecuted).isEqualTo(1);
    }

    /** Parte da configuração real do Flyway, em um esquema separado para não afetar outros testes. */
    private Flyway flywayNoEsquema(String esquema, String... locais) {
        return Flyway.configure()
                .configuration(flyway.getConfiguration())
                .schemas(esquema)
                .defaultSchema(esquema)
                .locations(locais)
                .load();
    }

    private static boolean comandosDestrutivos(String sql) {
        String semTextos = COMENTARIOS_E_TEXTOS.matcher(sql).replaceAll(" ");
        return DESTRUTIVO.matcher(semTextos).find();
    }
}
