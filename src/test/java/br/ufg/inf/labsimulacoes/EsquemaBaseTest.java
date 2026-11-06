package br.ufg.inf.labsimulacoes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/** Regras que o próprio banco garante (Stack, seção 6). Cada teste desfaz o que gravou. */
@SpringBootTest
@Import(InfraestruturaDeTeste.class)
@Transactional
class EsquemaBaseTest {

    @Autowired
    private JdbcTemplate jdbc;

    private long usuario;
    private long turma;
    private long versao;

    @BeforeEach
    void criarDadosBase() {
        usuario = inserirUsuario("professora@escola.test");
        turma = inserirTurma("8º A");
        long cenario = id("INSERT INTO cenario (autor_id) VALUES (?) RETURNING id", usuario);
        versao = inserirVersao(cenario, 1);
    }

    @Test
    void migrationCriaAsTabelasDaEntregaUm() {
        var tabelas = jdbc.queryForList(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'", String.class);

        assertThat(tabelas).contains(
                "usuario", "usuario_perfil", "turma", "parametro", "habilidade_bncc", "cenario",
                "cenario_versao", "versao_habilidade", "devolucao", "sessao", "evento_sessao", "mensagem",
                "resposta_ia_meta", "log_auditoria");
    }

    @Test
    void emailEUnicoSemDiferenciarMaiusculas() {
        assertThatThrownBy(() -> inserirUsuario("PROFESSORA@escola.test")).isInstanceOf(DataAccessException.class);
    }

    @Test
    void usuarioPodeAcumularPerfis() {
        jdbc.update("INSERT INTO usuario_perfil (usuario_id, perfil) VALUES (?, 'PROFESSOR')", usuario);

        assertThatCode(() -> jdbc.update(
                "INSERT INTO usuario_perfil (usuario_id, perfil) VALUES (?, 'CURADOR')", usuario))
                .doesNotThrowAnyException();
    }

    @Test
    void perfilForaDaListaEhRecusado() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO usuario_perfil (usuario_id, perfil) VALUES (?, 'ESTUDANTE')", usuario))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void turmaNaoTemDuasSessoesEmAndamento() {
        inserirSessao(turma);

        assertThatThrownBy(() -> inserirSessao(turma)).isInstanceOf(DataAccessException.class);
    }

    @Test
    void outraTurmaPodeTerSessaoEmAndamento() {
        inserirSessao(turma);

        assertThatCode(() -> inserirSessao(inserirTurma("8º B"))).doesNotThrowAnyException();
    }

    @Test
    void sessaoEncerradaLiberaATurma() {
        long sessao = inserirSessao(turma);
        jdbc.update("UPDATE sessao SET status = 'ENCERRADA', encerrada_em = now() WHERE id = ?", sessao);

        assertThatCode(() -> inserirSessao(turma)).doesNotThrowAnyException();
    }

    @Test
    void pausaExigeMotivo() {
        long sessao = inserirSessao(turma);

        assertThatThrownBy(() -> jdbc.update("UPDATE sessao SET status = 'PAUSADA' WHERE id = ?", sessao))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void engajamentoVaiDeUmACinco() {
        long sessao = inserirSessao(turma);

        assertThatThrownBy(() -> jdbc.update("UPDATE sessao SET engajamento = 6 WHERE id = ?", sessao))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void duracaoPrevistaFicaEntreDezETrintaMinutos() {
        assertThatThrownBy(() -> jdbc.update(
                "UPDATE cenario_versao SET duracao_prevista_min = 31 WHERE id = ?", versao))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void versaoNaoAprovadaPodeSerEditada() {
        assertThatCode(() -> jdbc.update("UPDATE cenario_versao SET titulo = 'Novo' WHERE id = ?", versao))
                .doesNotThrowAnyException();
    }

    @Test
    void versaoAprovadaNaoPodeSerAlterada() {
        aprovarVersao();

        assertThatThrownBy(() -> jdbc.update("UPDATE cenario_versao SET titulo = 'Novo' WHERE id = ?", versao))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("RN-08");
    }

    @Test
    void versaoAprovadaNaoPodeSerApagada() {
        aprovarVersao();

        assertThatThrownBy(() -> jdbc.update("DELETE FROM cenario_versao WHERE id = ?", versao))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("RN-08");
    }

    @Test
    void devolucaoExigeComentario() {
        assertThatThrownBy(() -> jdbc.update(
                "INSERT INTO devolucao (cenario_versao_id, curador_id, comentario) VALUES (?, ?, '   ')",
                versao, usuario))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void mensagemDaTurmaNaoTemMetadadosDeIa() {
        long sessao = inserirSessao(turma);
        long meta = id("""
                INSERT INTO resposta_ia_meta (provedor, modelo, versao_prompt)
                VALUES ('openai', 'modelo', 'v1') RETURNING id""");

        assertThatThrownBy(() -> jdbc.update("""
                INSERT INTO mensagem (sessao_id, autoria, texto, resposta_ia_meta_id)
                VALUES (?, 'TURMA', 'Pergunta', ?)""", sessao, meta))
                .isInstanceOf(DataAccessException.class);
    }

    @Test
    void metadadosSobrevivemAExclusaoDaMensagem() {
        long sessao = inserirSessao(turma);
        long meta = id("""
                INSERT INTO resposta_ia_meta (provedor, modelo, versao_prompt)
                VALUES ('openai', 'modelo', 'v1') RETURNING id""");
        jdbc.update("""
                INSERT INTO mensagem (sessao_id, autoria, texto, resposta_ia_meta_id)
                VALUES (?, 'IA', 'Resposta', ?)""", sessao, meta);

        jdbc.update("DELETE FROM mensagem WHERE sessao_id = ?", sessao);

        assertThat(jdbc.queryForObject("SELECT count(*) FROM resposta_ia_meta WHERE id = ?", Long.class, meta))
                .isEqualTo(1);
    }

    @Test
    void logDeAuditoriaNaoPodeSerAlterado() {
        long log = inserirLog();

        assertThatThrownBy(() -> jdbc.update("UPDATE log_auditoria SET acao = 'OUTRA' WHERE id = ?", log))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("RN-35");
    }

    @Test
    void logDeAuditoriaNaoPodeSerApagado() {
        long log = inserirLog();

        assertThatThrownBy(() -> jdbc.update("DELETE FROM log_auditoria WHERE id = ?", log))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("RN-35");
    }

    @Test
    void logDeAuditoriaNaoPodeSerEsvaziado() {
        assertThatThrownBy(() -> jdbc.execute("TRUNCATE log_auditoria"))
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("RN-35");
    }

    private long inserirUsuario(String email) {
        return id("INSERT INTO usuario (nome, email, senha_hash) VALUES ('Nome', ?, 'hash') RETURNING id", email);
    }

    private long inserirTurma(String rotulo) {
        return id("""
                INSERT INTO turma (escola, ano_serie, turno, rotulo)
                VALUES ('Escola', '8º ano', 'Manhã', ?) RETURNING id""", rotulo);
    }

    private long inserirVersao(long cenario, int numero) {
        return id("""
                INSERT INTO cenario_versao (cenario_id, numero, titulo, conteudo, criado_por_id)
                VALUES (?, ?, 'Título', '{}', ?) RETURNING id""", cenario, numero, usuario);
    }

    private long inserirSessao(long idTurma) {
        return id("""
                INSERT INTO sessao (professor_id, turma_id, cenario_versao_id)
                VALUES (?, ?, ?) RETURNING id""", usuario, idTurma, versao);
    }

    private long inserirLog() {
        return id("""
                INSERT INTO log_auditoria (usuario_id, acao, entidade)
                VALUES (?, 'APROVAR', 'cenario') RETURNING id""", usuario);
    }

    private void aprovarVersao() {
        jdbc.update("""
                UPDATE cenario_versao SET status = 'APROVADA', aprovado_por_id = ?, aprovado_em = now()
                WHERE id = ?""", usuario, versao);
    }

    private long id(String sql, Object... argumentos) {
        return jdbc.queryForObject(sql, Long.class, argumentos);
    }
}
