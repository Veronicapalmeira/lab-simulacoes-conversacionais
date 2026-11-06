package br.ufg.inf.labsimulacoes;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Regras das camadas internas de cada módulo (Stack, seção 4). A dependência vai de cima para baixo,
 * a apresentação não fala direto com a infraestrutura e o domínio não depende de framework.
 *
 * <p>A infraestrutura pode usar o domínio, porque os repositórios trabalham com as entidades. Já o
 * domínio não pode usar a infraestrutura, porque ela traz os frameworks.
 */
class ArquiteturaCamadasTest {

    private static final String SISTEMA = "br.ufg.inf.labsimulacoes";
    private static final String FIXTURES = "br.ufg.inf.fixturesmodulith.camadas";

    private static final String APRESENTACAO = "..apresentacao..";
    private static final String APLICACAO = "..aplicacao..";
    private static final String DOMINIO = "..dominio..";
    private static final String INFRAESTRUTURA = "..infraestrutura..";

    /** As camadas ainda estão vazias, e uma regra sem classes para checar falharia por padrão. */
    private static final ArchRule REGRAS = CompositeArchRule
            .of(naoDepende(DOMINIO, APRESENTACAO, APLICACAO, INFRAESTRUTURA))
            .and(naoDepende(INFRAESTRUTURA, APRESENTACAO, APLICACAO))
            .and(naoDepende(APLICACAO, APRESENTACAO))
            .and(naoDepende(APRESENTACAO, INFRAESTRUTURA))
            .and(classes()
                    .that().resideInAPackage(DOMINIO)
                    .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "br.ufg.inf..")
                    .allowEmptyShould(true));

    private static ArchRule naoDepende(String camada, String... proibidas) {
        return noClasses()
                .that().resideInAPackage(camada)
                .should().dependOnClassesThat().resideInAnyPackage(proibidas)
                .allowEmptyShould(true);
    }

    @Test
    void sistemaRespeitaAsCamadas() {
        JavaClasses sistema = new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages(SISTEMA);

        REGRAS.check(sistema);
    }

    @Test
    void camadasConformesPassam() {
        assertThat(REGRAS.evaluate(fixture("conforme")).hasViolation()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "dominiocomframework",
        "dominiousaaplicacao",
        "infrausaaplicacao",
        "aplicacaousaapresentacao",
        "apresentacaousainfra"
    })
    void violacaoDeCamadaFalha(String cenario) {
        assertThat(REGRAS.evaluate(fixture(cenario)).hasViolation()).isTrue();
    }

    private static JavaClasses fixture(String cenario) {
        return new ClassFileImporter().importPackages(FIXTURES + "." + cenario);
    }
}
