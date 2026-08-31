package br.com.studyingwithyou.domain.service;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.ResumoDashboard;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class CalculadoraResumoDashboard {

    public ResumoDashboard calcular(
            List<Disciplina> disciplinas,
            List<Atividade> atividades,
            LocalDate hoje) {
        Objects.requireNonNull(disciplinas, "As disciplinas sao obrigatorias.");
        Objects.requireNonNull(atividades, "As atividades sao obrigatorias.");
        Objects.requireNonNull(hoje, "A data de referencia e obrigatoria.");
        LocalDate limite = hoje.plusDays(7);
        long ativas = disciplinas.stream().filter(Disciplina::ativa).count();
        long pendentes = atividades.stream()
                .filter(atividade -> atividade.status() == StatusAtividade.PENDENTE)
                .count();
        long concluidas = atividades.stream()
                .filter(atividade -> atividade.status() == StatusAtividade.CONCLUIDA)
                .count();
        long atrasadas = atividades.stream()
                .filter(atividade -> atividade.estaAtrasadaEm(hoje))
                .count();
        long proximas = atividades.stream()
                .filter(atividade -> atividade.status() == StatusAtividade.PENDENTE)
                .filter(atividade -> !atividade.dataEntrega().isBefore(hoje))
                .filter(atividade -> !atividade.dataEntrega().isAfter(limite))
                .count();
        return new ResumoDashboard(
                ativas,
                atividades.size(),
                pendentes,
                concluidas,
                atrasadas,
                proximas);
    }
}
