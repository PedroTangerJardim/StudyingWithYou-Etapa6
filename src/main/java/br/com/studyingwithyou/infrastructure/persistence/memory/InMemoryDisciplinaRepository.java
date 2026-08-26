package br.com.studyingwithyou.infrastructure.persistence.memory;

import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryDisciplinaRepository implements DisciplinaRepository {

    private final Map<UUID, Disciplina> dados = new ConcurrentHashMap<>();

    @Override
    public Disciplina salvar(Disciplina disciplina) {
        dados.put(disciplina.id(), disciplina);
        return disciplina;
    }

    @Override
    public Optional<Disciplina> buscarPorId(UUID id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Disciplina> listarTodas() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public boolean existePorNome(String nome, UUID idIgnorado) {
        if (nome == null) {
            return false;
        }
        String procurado = nome.trim().toLowerCase(Locale.ROOT);
        return dados.values().stream()
                .filter(disciplina -> idIgnorado == null || !disciplina.id().equals(idIgnorado))
                .anyMatch(disciplina -> disciplina.nome().toLowerCase(Locale.ROOT).equals(procurado));
    }
}
