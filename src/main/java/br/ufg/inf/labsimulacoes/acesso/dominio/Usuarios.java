package br.ufg.inf.labsimulacoes.acesso.dominio;

import java.util.Optional;

/** Porta de persistência dos usuários. A implementação fica na infraestrutura. */
public interface Usuarios {

    /** Procura sem diferenciar maiúsculas de minúsculas. */
    Optional<Usuario> buscarPorEmail(String email);

    Usuario salvar(Usuario usuario);
}
