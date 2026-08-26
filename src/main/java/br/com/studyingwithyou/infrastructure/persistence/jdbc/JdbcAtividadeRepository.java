package br.com.studyingwithyou.infrastructure.persistence.jdbc;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Prioridade;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import br.com.studyingwithyou.shared.exception.PersistenciaException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class JdbcAtividadeRepository implements AtividadeRepository {

    private final ConnectionFactory connectionFactory;

    public JdbcAtividadeRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Atividade salvar(Atividade atividade) {
        String sql = "INSERT INTO atividades "
                + "(id, titulo, descricao, disciplina_id, data_entrega, prioridade, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE "
                + "titulo = VALUES(titulo), descricao = VALUES(descricao), "
                + "disciplina_id = VALUES(disciplina_id), data_entrega = VALUES(data_entrega), "
                + "prioridade = VALUES(prioridade), status = VALUES(status)";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            preencher(comando, atividade);
            comando.executeUpdate();
            return atividade;
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel salvar a atividade.", ex);
        }
    }

    @Override
    public Optional<Atividade> buscarPorId(UUID id) {
        String sql = "SELECT id, titulo, descricao, disciplina_id, data_entrega, prioridade, status "
                + "FROM atividades WHERE id = ?";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, id.toString());
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel consultar a atividade.", ex);
        }
    }

    @Override
    public List<Atividade> listarTodas() {
        String sql = "SELECT id, titulo, descricao, disciplina_id, data_entrega, prioridade, status FROM atividades";
        List<Atividade> atividades = new ArrayList<>();
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                atividades.add(mapear(resultado));
            }
            return atividades;
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel listar as atividades.", ex);
        }
    }

    @Override
    public void excluir(UUID id) {
        String sql = "DELETE FROM atividades WHERE id = ?";
        try (Connection conexao = connectionFactory.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, id.toString());
            comando.executeUpdate();
        } catch (SQLException ex) {
            throw new PersistenciaException("Nao foi possivel excluir a atividade.", ex);
        }
    }

    private void preencher(PreparedStatement comando, Atividade atividade) throws SQLException {
        comando.setString(1, atividade.id().toString());
        comando.setString(2, atividade.titulo());
        comando.setString(3, atividade.descricao());
        comando.setString(4, atividade.disciplinaId().toString());
        comando.setDate(5, java.sql.Date.valueOf(atividade.dataEntrega()));
        comando.setString(6, atividade.prioridade().name());
        comando.setString(7, atividade.status().name());
    }

    private Atividade mapear(ResultSet resultado) throws SQLException {
        return new Atividade(
                UUID.fromString(resultado.getString("id")),
                resultado.getString("titulo"),
                resultado.getString("descricao"),
                UUID.fromString(resultado.getString("disciplina_id")),
                resultado.getDate("data_entrega").toLocalDate(),
                Prioridade.valueOf(resultado.getString("prioridade")),
                StatusAtividade.valueOf(resultado.getString("status")));
    }
}
