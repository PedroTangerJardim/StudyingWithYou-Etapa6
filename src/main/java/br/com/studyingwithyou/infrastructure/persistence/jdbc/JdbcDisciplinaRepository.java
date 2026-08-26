package br.com.studyingwithyou.infrastructure.persistence.jdbc;

import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import br.com.studyingwithyou.shared.exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JdbcDisciplinaRepository implements DisciplinaRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcDisciplinaRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Disciplina salvar(Disciplina disciplina) {
        String sql = "INSERT INTO disciplinas (id, nome, professor, ativa) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), professor = VALUES(professor), ativa = VALUES(ativa)";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, disciplina.id().toString());
            comando.setString(2, disciplina.nome());
            comando.setString(3, disciplina.professor());
            comando.setBoolean(4, disciplina.ativa());
            comando.executeUpdate();
            return disciplina;
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel salvar a disciplina.", ex);
        }
    }

    @Override
    public Optional<Disciplina> buscarPorId(UUID id) {
        String sql = "SELECT id, nome, professor, ativa FROM disciplinas WHERE id = ?";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, id.toString());
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel consultar a disciplina.", ex);
        }
    }

    @Override
    public List<Disciplina> listarTodas() {
        String sql = "SELECT id, nome, professor, ativa FROM disciplinas";
        List<Disciplina> disciplinas = new ArrayList<>();
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                disciplinas.add(mapear(resultado));
            }
            return disciplinas;
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel listar as disciplinas.", ex);
        }
    }

    @Override
    public boolean existePorNome(String nome, UUID idIgnorado) {
        String sqlSemId = "SELECT COUNT(*) FROM disciplinas WHERE LOWER(nome) = LOWER(?)";
        String sqlComId = sqlSemId + " AND id <> ?";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(idIgnorado == null ? sqlSemId : sqlComId)) {
            comando.setString(1, nome);
            if (idIgnorado != null) {
                comando.setString(2, idIgnorado.toString());
            }
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() && resultado.getInt(1) > 0;
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel verificar a disciplina.", ex);
        }
    }

    private Disciplina mapear(ResultSet resultado) throws SQLException {
        return new Disciplina(
                UUID.fromString(resultado.getString("id")),
                resultado.getString("nome"),
                resultado.getString("professor"),
                resultado.getBoolean("ativa"));
    }
}
