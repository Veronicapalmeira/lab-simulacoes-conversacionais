package br.ufg.inf.labsimulacoes.acesso.infraestrutura;

import br.ufg.inf.labsimulacoes.acesso.dominio.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Mapeamento da tabela usuario. As anotações de JPA ficam aqui porque o domínio não usa framework. */
@Entity
@Table(name = "usuario")
class UsuarioEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativo;

    protected UsuarioEntidade() {}

    private UsuarioEntidade(Usuario usuario) {
        this.id = usuario.id();
        this.nome = usuario.nome();
        this.email = usuario.email();
        this.senhaHash = usuario.senhaHash();
        this.ativo = usuario.ativo();
    }

    static UsuarioEntidade de(Usuario usuario) {
        return new UsuarioEntidade(usuario);
    }

    Usuario paraDominio() {
        return new Usuario(id, nome, email, senhaHash, ativo);
    }
}
