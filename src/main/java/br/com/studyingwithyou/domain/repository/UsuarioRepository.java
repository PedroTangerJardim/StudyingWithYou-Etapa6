package br.com.studyingwithyou.domain.repository;

import br.com.studyingwithyou.domain.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);

    boolean existePorEmail(String email);
}
