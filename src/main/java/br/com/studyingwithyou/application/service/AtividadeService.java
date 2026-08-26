package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.application.dto.EdicaoAtividade;
import br.com.studyingwithyou.application.dto.NovaAtividade;
import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import br.com.studyingwithyou.shared.exception.EntidadeNaoEncontradaException;
import br.com.studyingwithyou.shared.exception.RegraNegocioException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final Clock clock;

    public AtividadeService(
            AtividadeRepository atividadeRepository,
            DisciplinaRepository disciplinaRepository,
            Clock clock) {
        this.atividadeRepository = Objects.requireNonNull(atividadeRepository);
        this.disciplinaRepository = Objects.requireNonNull(disciplinaRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public Atividade cadastrar(NovaAtividade entrada) {
        Objects.requireNonNull(entrada, "Os dados da atividade sao obrigatorios.");
        validarDataEntrega(entrada.dataEntrega());
        validarDisciplinaAtiva(entrada.disciplinaId());
        Atividade atividade = criarAtividade(
                UUID.randomUUID(),
                entrada.titulo(),
                entrada.descricao(),
                entrada.disciplinaId(),
                entrada.dataEntrega(),
                entrada.prioridade(),
                StatusAtividade.PENDENTE);
        return atividadeRepository.salvar(atividade);
    }

    public Atividade editar(UUID id, EdicaoAtividade entrada) {
        Objects.requireNonNull(entrada, "Os dados da atividade sao obrigatorios.");
        Atividade atual = buscarPorId(id);
        validarDataEntrega(entrada.dataEntrega());
        validarDisciplinaAtiva(entrada.disciplinaId());
        Atividade atualizada = criarAtividade(
                atual.id(),
                entrada.titulo(),
                entrada.descricao(),
                entrada.disciplinaId(),
                entrada.dataEntrega(),
                entrada.prioridade(),
                atual.status());
        return atividadeRepository.salvar(atualizada);
    }

    public Atividade concluir(UUID id) {
        Atividade atividade = buscarPorId(id);
        if (atividade.status() == StatusAtividade.CONCLUIDA) {
            throw new RegraNegocioException("A atividade ja esta concluida.");
        }
        return atividadeRepository.salvar(atividade.concluir());
    }

    public Atividade reabrir(UUID id) {
        Atividade atividade = buscarPorId(id);
        if (atividade.status() == StatusAtividade.PENDENTE) {
            throw new RegraNegocioException("A atividade ja esta pendente.");
        }
        return atividadeRepository.salvar(atividade.reabrir());
    }

    public void excluir(UUID id) {
        buscarPorId(id);
        atividadeRepository.excluir(id);
    }

    public Atividade buscarPorId(UUID id) {
        Objects.requireNonNull(id, "O identificador da atividade e obrigatorio.");
        return atividadeRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Atividade nao encontrada."));
    }

    public List<Atividade> listarTodas() {
        return atividadeRepository.listarTodas().stream().sorted(ordenacaoCronologica()).toList();
    }

    public List<Atividade> listarPendentes() {
        return listarTodas().stream()
                .filter(atividade -> atividade.status() == StatusAtividade.PENDENTE)
                .toList();
    }

    public List<Atividade> listarPorDisciplina(UUID disciplinaId) {
        Objects.requireNonNull(disciplinaId, "O identificador da disciplina e obrigatorio.");
        return listarTodas().stream()
                .filter(atividade -> atividade.disciplinaId().equals(disciplinaId))
                .toList();
    }

    public List<Atividade> listarCronograma(LocalDate inicio, LocalDate fim) {
        Objects.requireNonNull(inicio, "A data inicial e obrigatoria.");
        Objects.requireNonNull(fim, "A data final e obrigatoria.");
        if (fim.isBefore(inicio)) {
            throw new RegraNegocioException("A data final nao pode ser anterior a data inicial.");
        }
        return listarPendentes().stream()
                .filter(atividade -> !atividade.dataEntrega().isBefore(inicio))
                .filter(atividade -> !atividade.dataEntrega().isAfter(fim))
                .toList();
    }

    private void validarDataEntrega(LocalDate dataEntrega) {
        if (dataEntrega == null) {
            throw new RegraNegocioException("A data de entrega e obrigatoria.");
        }
        if (dataEntrega.isBefore(LocalDate.now(clock))) {
            throw new RegraNegocioException("A data de entrega nao pode estar no passado.");
        }
    }

    private void validarDisciplinaAtiva(UUID disciplinaId) {
        if (disciplinaId == null) {
            throw new RegraNegocioException("A disciplina e obrigatoria.");
        }
        Disciplina disciplina = disciplinaRepository.buscarPorId(disciplinaId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Disciplina nao encontrada."));
        if (!disciplina.ativa()) {
            throw new RegraNegocioException("Nao e possivel usar uma disciplina desativada.");
        }
    }

    private Atividade criarAtividade(
            UUID id,
            String titulo,
            String descricao,
            UUID disciplinaId,
            LocalDate dataEntrega,
            br.com.studyingwithyou.domain.model.Prioridade prioridade,
            StatusAtividade status) {
        try {
            return new Atividade(id, titulo, descricao, disciplinaId, dataEntrega, prioridade, status);
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }

    private Comparator<Atividade> ordenacaoCronologica() {
        return Comparator.comparing(Atividade::dataEntrega)
                .thenComparing(Atividade::titulo, String.CASE_INSENSITIVE_ORDER);
    }
}
