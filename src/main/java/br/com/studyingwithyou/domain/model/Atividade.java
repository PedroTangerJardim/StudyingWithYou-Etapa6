package br.com.studyingwithyou.domain.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record Atividade(
        UUID id,
        String titulo,
        String descricao,
        UUID disciplinaId,
        LocalDate dataEntrega,
        Prioridade prioridade,
        StatusAtividade status) {

    public Atividade {
        Objects.requireNonNull(id, "O identificador da atividade e obrigatorio.");
        Objects.requireNonNull(disciplinaId, "A disciplina e obrigatoria.");
        Objects.requireNonNull(dataEntrega, "A data de entrega e obrigatoria.");
        Objects.requireNonNull(prioridade, "A prioridade e obrigatoria.");
        Objects.requireNonNull(status, "O status e obrigatorio.");
        titulo = validarTitulo(titulo);
        descricao = descricao == null ? "" : descricao.trim();
        if (descricao.length() > 500) {
            throw new IllegalArgumentException("A descricao deve possuir no maximo 500 caracteres.");
        }
    }

    public Atividade atualizar(
            String novoTitulo,
            String novaDescricao,
            UUID novaDisciplinaId,
            LocalDate novaDataEntrega,
            Prioridade novaPrioridade) {
        return new Atividade(
                id,
                novoTitulo,
                novaDescricao,
                novaDisciplinaId,
                novaDataEntrega,
                novaPrioridade,
                status);
    }

    public Atividade concluir() {
        return new Atividade(id, titulo, descricao, disciplinaId, dataEntrega, prioridade, StatusAtividade.CONCLUIDA);
    }

    public Atividade reabrir() {
        return new Atividade(id, titulo, descricao, disciplinaId, dataEntrega, prioridade, StatusAtividade.PENDENTE);
    }

    public boolean estaAtrasadaEm(LocalDate data) {
        return status == StatusAtividade.PENDENTE && dataEntrega.isBefore(data);
    }

    private static String validarTitulo(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O titulo da atividade e obrigatorio.");
        }
        String texto = valor.trim();
        if (texto.length() > 120) {
            throw new IllegalArgumentException("O titulo deve possuir no maximo 120 caracteres.");
        }
        return texto;
    }
}
