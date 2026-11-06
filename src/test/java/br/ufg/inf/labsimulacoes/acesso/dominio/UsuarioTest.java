package br.ufg.inf.labsimulacoes.acesso.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class UsuarioTest {

    private static final String HASH = "$argon2id$hash";

    @Test
    void usuarioNovoPodeEntrar() {
        Usuario usuario = Usuario.novo("Ana", "ana@escola.test", HASH);

        assertThat(usuario.ativo()).isTrue();
        assertThat(usuario.podeEntrar()).isTrue();
    }

    @Test
    void usuarioInativoNaoPodeEntrar() {
        assertThat(new Usuario(1L, "Ana", "ana@escola.test", HASH, false).podeEntrar()).isFalse();
    }

    @Test
    void emailFicaNormalizado() {
        assertThat(Usuario.novo("Ana", "  Ana@Escola.TEST ", HASH).email()).isEqualTo("ana@escola.test");
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void camposObrigatoriosPrecisamEstarPreenchidos(String vazio) {
        assertThatThrownBy(() -> Usuario.novo(vazio, "ana@escola.test", HASH))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Usuario.novo("Ana", vazio, HASH))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Usuario.novo("Ana", "ana@escola.test", vazio))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void textoDoUsuarioNaoExpoeOHash() {
        assertThat(Usuario.novo("Ana", "ana@escola.test", HASH).toString()).doesNotContain(HASH);
    }
}
