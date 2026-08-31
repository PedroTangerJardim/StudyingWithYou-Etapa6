package br.com.studyingwithyou.domain.service;

import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.Prioridade;
import br.com.studyingwithyou.domain.model.ResumoDashboard;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraResumoDashboardTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 8, 31);
    private final CalculadoraResumoDashboard calculadora = new CalculadoraResumoDashboard();

    @Test
    void calculaIndicadoresDoDashboard() {
        Disciplina ativa = disciplina(true);
        Disciplina inativa = disciplina(false);
        List<Atividade> atividades = List.of(
                atividade(ativa.id(), HOJE.minusDays(1), StatusAtividade.PENDENTE),
                atividade(ativa.id(), HOJE, StatusAtividade.PENDENTE),
                atividade(ativa.id(), HOJE.plusDays(7), StatusAtividade.PENDENTE),
                atividade(ativa.id(), HOJE.plusDays(8), StatusAtividade.PENDENTE),
                atividade(inativa.id(), HOJE.minusDays(5), StatusAtividade.CONCLUIDA));

        ResumoDashboard resumo = calculadora.calcular(
                List.of(ativa, inativa),
                atividades,
                HOJE);

        assertAll(
                () -> assertEquals(1, resumo.disciplinasAtivas()),
                () -> assertEquals(5, resumo.atividadesTotais()),
                () -> assertEquals(4, resumo.atividadesPendentes()),
                () -> assertEquals(1, resumo.atividadesConcluidas()),
                () -> assertEquals(1, resumo.atividadesAtrasadas()),
                () -> assertEquals(2, resumo.proximosSeteDias()));
    }

    @Test
    void retornaResumoZeradoParaListasVazias() {
        ResumoDashboard resumo = calculadora.calcular(List.of(), List.of(), HOJE);

        assertEquals(new ResumoDashboard(0, 0, 0, 0, 0, 0), resumo);
    }

    private Disciplina disciplina(boolean ativa) {
        return new Disciplina(UUID.randomUUID(), "Programacao Web", "Ana Silva", ativa);
    }

    private Atividade atividade(UUID disciplinaId, LocalDate entrega, StatusAtividade status) {
        return new Atividade(
                UUID.randomUUID(),
                "Atividade",
                "",
                disciplinaId,
                entrega,
                Prioridade.MEDIA,
                status);
    }
}
