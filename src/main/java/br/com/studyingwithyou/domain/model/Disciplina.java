package br.com.studyingwithyou.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Disciplina(UUID id, String nome, String professor, boolean ativa) {

    public Disciplina {
        Objects.requireNonNull(id, "O identificador da disciplina e obrigatorio.");
        nome = validarTexto(nome, "O nome da disciplina e obrigatorio.", 100);
        professor = validarTexto(professor, "O nome do professor e obrigatorio.", 100);
    }

    public Disciplina atualizar(String novoNome, String novoProfessor) {
        return new Disciplina(id, novoNome, novoProfessor, ativa);
    }

    public Disciplina desativar() {
        return new Disciplina(id, nome, professor, false);
    }

    public Disciplina ativar() {
        return new Disciplina(id, nome, professor, true);
    }

    private static String validarTexto(String valor, String mensagem, int limite) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(mensagem);
        }
        String texto = valor.trim();
        if (texto.length() > limite) {
            throw new IllegalArgumentException("O texto deve possuir no maximo " + limite + " caracteres.");
        }
        return texto;
    }
}
