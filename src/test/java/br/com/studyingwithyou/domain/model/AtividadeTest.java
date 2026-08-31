package br.com.studyingwithyou.domain.model;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtividadeTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 8, 31);

    @Test
    void consideraPendenteVencidaComoAtrasada() {
        Atividade atividade = atividade(HOJE.minusDays(1), StatusAtividade.PENDENTE);

        assertTrue(atividade.estaAtrasadaEm(HOJE));
    }

    @Test
    void naoConsideraVencimentoHojeComoAtraso() {
        Atividade atividade = atividade(HOJE, StatusAtividade.PENDENTE);

        assertFalse(atividade.estaAtrasadaEm(HOJE));
    }

    @Test
    void naoConsideraConcluidaComoAtrasada() {
        Atividade atividade = atividade(HOJE.minusDays(3), StatusAtividade.CONCLUIDA);

        assertFalse(atividade.estaAtrasadaEm(HOJE));
    }

    @Test
    void concluiEReabreSemAlterarIdentidade() {
        Atividade original = atividade(HOJE.plusDays(2), StatusAtividade.PENDENTE);

        Atividade concluida = original.concluir();
        Atividade reaberta = concluida.reabrir();

        assertEquals(StatusAtividade.CONCLUIDA, concluida.status());
        assertEquals(StatusAtividade.PENDENTE, reaberta.status());
        assertEquals(original.id(), reaberta.id());
    }

    private Atividade atividade(LocalDate entrega, StatusAtividade status) {
        return new Atividade(
                UUID.randomUUID(),
                "Revisar conteudo",
                "",
                UUID.randomUUID(),
                entrega,
                Prioridade.MEDIA,
                status);
    }
}
