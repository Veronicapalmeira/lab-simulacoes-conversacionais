package br.ufg.inf.labsimulacoes.acesso.aplicacao;

import br.ufg.inf.labsimulacoes.acesso.dominio.Usuario;
import br.ufg.inf.labsimulacoes.acesso.dominio.Usuarios;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Único caminho para criar usuários, para que a senha só chegue ao banco como hash (RNF-09). */
@Service
public class CadastroDeUsuarios {

    private final Usuarios usuarios;
    private final PasswordEncoder codificador;

    CadastroDeUsuarios(Usuarios usuarios, PasswordEncoder codificador) {
        this.usuarios = usuarios;
        this.codificador = codificador;
    }

    @Transactional
    public Usuario cadastrar(String nome, String email, String senha) {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("O campo senha é obrigatório");
        }
        return usuarios.salvar(Usuario.novo(nome, email, codificador.encode(senha)));
    }
}
