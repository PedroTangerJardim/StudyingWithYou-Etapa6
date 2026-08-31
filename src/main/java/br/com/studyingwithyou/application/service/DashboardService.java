package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.ResumoDashboard;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import br.com.studyingwithyou.domain.service.CalculadoraResumoDashboard;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class DashboardService {

    private final AtividadeRepository atividadeRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final Clock clock;
    private final CalculadoraResumoDashboard calculadora;

    public DashboardService(
            AtividadeRepository atividadeRepository,
            DisciplinaRepository disciplinaRepository,
            Clock clock) {
        this.atividadeRepository = Objects.requireNonNull(atividadeRepository);
        this.disciplinaRepository = Objects.requireNonNull(disciplinaRepository);
        this.clock = Objects.requireNonNull(clock);
        this.calculadora = new CalculadoraResumoDashboard();
    }

    public ResumoDashboard gerarResumo() {
        List<Atividade> atividades = atividadeRepository.listarTodas();
        List<Disciplina> disciplinas = disciplinaRepository.listarTodas();
        return calculadora.calcular(disciplinas, atividades, LocalDate.now(clock));
    }
}
