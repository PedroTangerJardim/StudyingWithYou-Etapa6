package br.com.studyingwithyou.domain.repository;

import br.com.studyingwithyou.domain.model.Atividade;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtividadeRepository {

    Atividade salvar(Atividade atividade);

    Optional<Atividade> buscarPorId(UUID id);

    List<Atividade> listarTodas();

    void excluir(UUID id);
}
