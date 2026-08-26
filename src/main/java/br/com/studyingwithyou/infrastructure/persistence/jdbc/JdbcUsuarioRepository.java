package br.com.studyingwithyou.infrastructure.persistence.jdbc;

import br.com.studyingwithyou.domain.model.Usuario;
import br.com.studyingwithyou.domain.repository.UsuarioRepository;
import br.com.studyingwithyou.shared.exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class JdbcUsuarioRepository implements UsuarioRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcUsuarioRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, nome, email, senha_hash, ativo) VALUES (?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), email = VALUES(email), "
                + "senha_hash = VALUES(senha_hash), ativo = VALUES(ativo)";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, usuario.id().toString());
            comando.setString(2, usuario.nome());
            comando.setString(3, usuario.email());
            comando.setString(4, usuario.senhaHash());
            comando.setBoolean(5, usuario.ativo());
            comando.executeUpdate();
            return usuario;
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel salvar o usuario.", ex);
        }
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = "SELECT id, nome, email, senha_hash, ativo FROM usuarios WHERE LOWER(email) = LOWER(?)";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, normalizar(email));
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel consultar o usuario.", ex);
        }
    }

    @Override
    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE LOWER(email) = LOWER(?)";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, normalizar(email));
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel verificar o usuario.", ex);
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        return new Usuario(
                UUID.fromString(resultado.getString("id")),
                resultado.getString("nome"),
                resultado.getString("email"),
                resultado.getString("senha_hash"),
                resultado.getBoolean("ativo"));
    }

    private String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
