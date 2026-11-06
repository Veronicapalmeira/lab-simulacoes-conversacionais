package br.ufg.inf.labsimulacoes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.core.Violations;

/**
 * Regras de fronteira entre módulos (RNF-11). Qualquer violação no código de produção faz o build
 * falhar. Os casos com fixtures garantem que a verificação continua detectando acesso a classe
 * interna e ciclos.
 */
class ArquiteturaModulosTest {

    private static final String FIXTURES = "br.ufg.inf.fixturesmodulith";

    /** O padrão do Modulith ignora classes de teste, onde as fixtures ficam. */
    private static final ImportOption INCLUI_TESTES = location -> true;

    private final ApplicationModules modulos = ApplicationModules.of(LabSimulacoesApplication.class);

    @Test
    void modulosRespeitamAsFronteiras() {
        modulos.verify();
    }

    @Test
    void sistemaTemOsSeisModulosDaArquiteturaEOPacoteDeEventos() {
        assertThat(modulos.stream().map(ApplicationModule::getIdentifier).map(Object::toString))
                .containsExactlyInAnyOrder(
                        "acesso", "cenarios", "sessoes", "auditoria", "metricas", "ia", "eventos");
    }

    @Test
    void acessoPelaApiDoModuloEPermitido() {
        assertThatCode(() -> fixture("conforme").verify()).doesNotThrowAnyException();
    }

    @Test
    void acessoAClasseInternaDeOutroModuloFalha() {
        assertThatThrownBy(() -> fixture("acessointerno").verify())
                .isInstanceOf(Violations.class)
                .hasMessageContaining("non-exposed type")
                .hasMessageContaining("modulob.interno.Detalhe");
    }

    @Test
    void cicloEntreModulosFalha() {
        assertThatThrownBy(() -> fixture("ciclo").verify())
                .isInstanceOf(Violations.class)
                .hasMessageContaining("Cycle detected");
    }

    @Test
    void dependenciaNaoDeclaradaFalha() {
        assertThatThrownBy(() -> fixture("dependencianaodeclarada").verify())
                .isInstanceOf(Violations.class)
                .hasMessageContaining("depends on module")
                .hasMessageContaining("modulob");
    }

    @Test
    void eventosNoModuloPublicadorFormamCiclo() {
        assertThatThrownBy(() -> fixture("eventonopublicador").verify())
                .isInstanceOf(Violations.class)
                .hasMessageContaining("Cycle detected");
    }

    @Test
    void eventosNoPacoteCompartilhadoNaoFormamCiclo() {
        assertThatCode(() -> fixture("eventocompartilhado").verify()).doesNotThrowAnyException();
    }

    private static ApplicationModules fixture(String cenario) {
        return ApplicationModules.of(FIXTURES + "." + cenario, INCLUI_TESTES);
    }
}
