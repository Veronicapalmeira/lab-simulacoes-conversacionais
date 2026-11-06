package br.ufg.inf.labsimulacoes.acesso.infraestrutura;

import br.ufg.inf.labsimulacoes.acesso.dominio.Usuario;
import br.ufg.inf.labsimulacoes.acesso.dominio.Usuarios;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class UsuariosJpa implements Usuarios {

    private final UsuarioRepositorioJpa repositorio;

    UsuariosJpa(UsuarioRepositorioJpa repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return repositorio.buscarPorEmailNormalizado(Usuario.normalizarEmail(email))
                .map(UsuarioEntidade::paraDominio);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return repositorio.save(UsuarioEntidade.de(usuario)).paraDominio();
    }
}
