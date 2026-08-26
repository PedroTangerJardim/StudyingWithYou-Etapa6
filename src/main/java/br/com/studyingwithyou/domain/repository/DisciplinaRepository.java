package br.com.studyingwithyou.domain.repository;

import br.com.studyingwithyou.domain.model.Disciplina;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DisciplinaRepository {

    Disciplina salvar(Disciplina disciplina);

    Optional<Disciplina> buscarPorId(UUID id);

    List<Disciplina> listarTodas();

    boolean existePorNome(String nome, UUID idIgnorado);
}
