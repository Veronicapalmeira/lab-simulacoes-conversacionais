package br.ufg.inf.labsimulacoes.acesso.dominio;

import java.util.Locale;

/** Profissional da escola com acesso ao sistema. Estudantes não têm conta (RN-11). */
public record Usuario(Long id, String nome, String email, String senhaHash, boolean ativo) {

    public Usuario {
        exigirPreenchido(nome, "nome");
        exigirPreenchido(email, "e-mail");
        exigirPreenchido(senhaHash, "hash da senha");
        email = normalizarEmail(email);
    }

    public static Usuario novo(String nome, String email, String senhaHash) {
        return new Usuario(null, nome, email, senhaHash, true);
    }

    /** O e-mail é único sem diferenciar maiúsculas, como no índice uq_usuario_email. */
    public static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    public boolean podeEntrar() {
        return ativo;
    }

    /** Omite o hash, para que ele não vá parar em logs. */
    @Override
    public String toString() {
        return "Usuario[id=%d, email=%s, ativo=%s]".formatted(id, email, ativo);
    }

    private static void exigirPreenchido(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório");
        }
    }
}
