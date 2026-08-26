package br.com.studyingwithyou.infrastructure.persistence.memory;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryAtividadeRepository implements AtividadeRepository {

    private final Map<UUID, Atividade> dados = new ConcurrentHashMap<>();

    @Override
    public Atividade salvar(Atividade atividade) {
        dados.put(atividade.id(), atividade);
        return atividade;
    }

    @Override
    public Optional<Atividade> buscarPorId(UUID id) {
        return Optional.ofNullable(dados.get(id));
    }

    @Override
    public List<Atividade> listarTodas() {
        return new ArrayList<>(dados.values());
    }

    @Override
    public void excluir(UUID id) {
        dados.remove(id);
    }
}
