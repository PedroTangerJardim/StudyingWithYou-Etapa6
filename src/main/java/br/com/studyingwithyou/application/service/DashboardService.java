package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.ResumoDashboard;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class DashboardService {

    private final AtividadeRepository atividadeRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final Clock clock;

    public DashboardService(
            AtividadeRepository atividadeRepository,
            DisciplinaRepository disciplinaRepository,
            Clock clock) {
        this.atividadeRepository = Objects.requireNonNull(atividadeRepository);
        this.disciplinaRepository = Objects.requireNonNull(disciplinaRepository);
        this.clock = Objects.requireNonNull(clock);
    }

    public ResumoDashboard gerarResumo() {
        LocalDate hoje = LocalDate.now(clock);
        LocalDate limite = hoje.plusDays(7);
        List<Atividade> atividades = atividadeRepository.listarTodas();
        long ativas = disciplinaRepository.listarTodas().stream().filter(Disciplina::ativa).count();
        long pendentes = atividades.stream().filter(a -> a.status() == StatusAtividade.PENDENTE).count();
        long concluidas = atividades.stream().filter(a -> a.status() == StatusAtividade.CONCLUIDA).count();
        long atrasadas = atividades.stream().filter(a -> a.estaAtrasadaEm(hoje)).count();
        long proximas = atividades.stream()
                .filter(a -> a.status() == StatusAtividade.PENDENTE)
                .filter(a -> !a.dataEntrega().isBefore(hoje))
                .filter(a -> !a.dataEntrega().isAfter(limite))
                .count();
        return new ResumoDashboard(ativas, atividades.size(), pendentes, concluidas, atrasadas, proximas);
    }
}
