package br.com.studyingwithyou.application.dto;

import br.com.studyingwithyou.domain.model.Prioridade;
import java.time.LocalDate;
import java.util.UUID;

public record EdicaoAtividade(
        String titulo,
        String descricao,
        UUID disciplinaId,
        LocalDate dataEntrega,
        Prioridade prioridade) {
}
