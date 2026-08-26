package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.application.dto.NovoUsuario;
import br.com.studyingwithyou.application.dto.UsuarioAutenticado;
import br.com.studyingwithyou.domain.model.Usuario;
import br.com.studyingwithyou.domain.repository.UsuarioRepository;
import br.com.studyingwithyou.domain.security.SenhaHasher;
import br.com.studyingwithyou.shared.exception.RegraNegocioException;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class AutenticacaoService {

    private final UsuarioRepository repository;
    private final SenhaHasher senhaHasher;

    public AutenticacaoService(UsuarioRepository repository, SenhaHasher senhaHasher) {
        this.repository = Objects.requireNonNull(repository);
        this.senhaHasher = Objects.requireNonNull(senhaHasher);
    }

    public UsuarioAutenticado cadastrar(NovoUsuario entrada) {
        Objects.requireNonNull(entrada, "Os dados do usuario sao obrigatorios.");
        validarSenha(entrada.senha());
        String email = normalizarEmail(entrada.email());
        if (repository.existePorEmail(email)) {
            throw new RegraNegocioException("Ja existe um usuario com esse e-mail.");
        }
        Usuario usuario = criarUsuario(
                UUID.randomUUID(),
                entrada.nome(),
                email,
                senhaHasher.gerarHash(entrada.senha()),
                true);
        return ocultarSenha(repository.salvar(usuario));
    }

    public UsuarioAutenticado autenticar(String email, String senha) {
        if (senha == null || senha.isBlank()) {
            throw new RegraNegocioException("E-mail ou senha invalidos.");
        }
        Usuario usuario = repository.buscarPorEmail(normalizarEmail(email))
                .orElseThrow(() -> new RegraNegocioException("E-mail ou senha invalidos."));
        if (!usuario.ativo() || !senhaHasher.corresponde(senha, usuario.senhaHash())) {
            throw new RegraNegocioException("E-mail ou senha invalidos.");
        }
        return ocultarSenha(usuario);
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new RegraNegocioException("A senha deve possuir pelo menos 8 caracteres.");
        }
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("O e-mail e obrigatorio.");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private Usuario criarUsuario(UUID id, String nome, String email, String senhaHash, boolean ativo) {
        try {
            return new Usuario(id, nome, email, senhaHash, ativo);
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }

    private UsuarioAutenticado ocultarSenha(Usuario usuario) {
        return new UsuarioAutenticado(usuario.id(), usuario.nome(), usuario.email());
    }
}
