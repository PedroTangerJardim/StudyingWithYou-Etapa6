package br.com.studyingwithyou.application.service;

import br.com.studyingwithyou.application.dto.NovaAtividade;
import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.Prioridade;
import br.com.studyingwithyou.infrastructure.persistence.memory.InMemoryAtividadeRepository;
import br.com.studyingwithyou.infrastructure.persistence.memory.InMemoryDisciplinaRepository;
import br.com.studyingwithyou.shared.exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AtividadeServiceTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 8, 31);
    private InMemoryDisciplinaRepository disciplinaRepository;
    private AtividadeService service;
    private Disciplina disciplina;

    @BeforeEach
    void prepararCenario() {
        disciplinaRepository = new InMemoryDisciplinaRepository();
        service = new AtividadeService(
                new InMemoryAtividadeRepository(),
                disciplinaRepository,
                Clock.fixed(
                        Instant.parse("2026-08-31T15:00:00Z"),
                        ZoneId.of("America/Sao_Paulo")));
        disciplina = disciplinaRepository.salvar(
                new Disciplina(UUID.randomUUID(), "Programacao Web", "Ana Silva", true));
    }

    @Test
    void recusaDataDeEntregaNoPassado() {
        NovaAtividade entrada = entrada("Atividade antiga", HOJE.minusDays(1));

        RegraNegocioException erro = assertThrows(
                RegraNegocioException.class,
                () -> service.cadastrar(entrada));

        assertEquals("A data de entrega nao pode estar no passado.", erro.getMessage());
    }

    @Test
    void filtraEOrdenaCronograma() {
        service.cadastrar(entrada("Terceira", HOJE.plusDays(10)));
        service.cadastrar(entrada("Segunda", HOJE.plusDays(4)));
        service.cadastrar(entrada("Primeira", HOJE.plusDays(1)));

        List<Atividade> cronograma = service.listarCronograma(HOJE, HOJE.plusDays(7));

        assertEquals(List.of("Primeira", "Segunda"),
                cronograma.stream().map(Atividade::titulo).toList());
    }

    @Test
    void recusaIntervaloInvertidoNoCronograma() {
        assertThrows(
                RegraNegocioException.class,
                () -> service.listarCronograma(HOJE.plusDays(1), HOJE));
    }

    @Test
    void recusaAtividadeParaDisciplinaDesativada() {
        disciplinaRepository.salvar(disciplina.desativar());

        assertThrows(
                RegraNegocioException.class,
                () -> service.cadastrar(entrada("Teste", HOJE.plusDays(2))));
    }

    private NovaAtividade entrada(String titulo, LocalDate dataEntrega) {
        return new NovaAtividade(
                titulo,
                "Cenario de teste",
                disciplina.id(),
                dataEntrega,
                Prioridade.MEDIA);
    }
}
