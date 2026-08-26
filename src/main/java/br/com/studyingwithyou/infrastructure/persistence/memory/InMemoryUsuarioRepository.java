package br.com.studyingwithyou.infrastructure.persistence.memory;

import br.com.studyingwithyou.domain.model.Usuario;
import br.com.studyingwithyou.domain.repository.UsuarioRepository;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUsuarioRepository implements UsuarioRepository {

    private final Map<String, Usuario> dados = new ConcurrentHashMap<>();

    @Override
    public Usuario salvar(Usuario usuario) {
        dados.put(usuario.email(), usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return Optional.ofNullable(dados.get(normalizar(email)));
    }

    @Override
    public boolean existePorEmail(String email) {
        return dados.containsKey(normalizar(email));
    }

    private String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
