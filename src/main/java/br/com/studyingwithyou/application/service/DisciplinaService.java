package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.application.dto.NovaDisciplina;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import br.com.studyingwithyou.shared.exception.EntidadeNaoEncontradaException;
import br.com.studyingwithyou.shared.exception.RegraNegocioException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class DisciplinaService {

    private final DisciplinaRepository repository;

    public DisciplinaService(DisciplinaRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Disciplina cadastrar(NovaDisciplina entrada) {
        Objects.requireNonNull(entrada, "Os dados da disciplina sao obrigatorios.");
        if (repository.existePorNome(entrada.nome(), null)) {
            throw new RegraNegocioException("Ja existe uma disciplina com esse nome.");
        }
        return repository.salvar(criarDisciplina(UUID.randomUUID(), entrada.nome(), entrada.professor(), true));
    }

    public Disciplina atualizar(UUID id, NovaDisciplina entrada) {
        Objects.requireNonNull(entrada, "Os dados da disciplina sao obrigatorios.");
        Disciplina atual = buscarPorId(id);
        if (repository.existePorNome(entrada.nome(), id)) {
            throw new RegraNegocioException("Ja existe outra disciplina com esse nome.");
        }
        return repository.salvar(criarDisciplina(id, entrada.nome(), entrada.professor(), atual.ativa()));
    }

    public Disciplina desativar(UUID id) {
        Disciplina disciplina = buscarPorId(id);
        if (!disciplina.ativa()) {
            throw new RegraNegocioException("A disciplina ja esta desativada.");
        }
        return repository.salvar(disciplina.desativar());
    }

    public Disciplina ativar(UUID id) {
        Disciplina disciplina = buscarPorId(id);
        if (disciplina.ativa()) {
            throw new RegraNegocioException("A disciplina ja esta ativa.");
        }
        return repository.salvar(disciplina.ativar());
    }

    public Disciplina buscarPorId(UUID id) {
        Objects.requireNonNull(id, "O identificador da disciplina e obrigatorio.");
        return repository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Disciplina nao encontrada."));
    }

    public List<Disciplina> listarTodas() {
        return repository.listarTodas().stream()
                .sorted(Comparator.comparing(Disciplina::nome, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public List<Disciplina> listarAtivas() {
        return listarTodas().stream().filter(Disciplina::ativa).toList();
    }

    private Disciplina criarDisciplina(UUID id, String nome, String professor, boolean ativa) {
        try {
            return new Disciplina(id, nome, professor, ativa);
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }
}
