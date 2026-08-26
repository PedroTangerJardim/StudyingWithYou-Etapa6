package br.com.studyingwithyou.domain.model;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public record Usuario(UUID id, String nome, String email, String senhaHash, boolean ativo) {

    public Usuario {
        Objects.requireNonNull(id, "O identificador do usuario e obrigatorio.");
        nome = validarTexto(nome, "O nome do usuario e obrigatorio.", 100);
        email = validarEmail(email);
        senhaHash = validarTexto(senhaHash, "A senha protegida e obrigatoria.", 512);
    }

    public Usuario desativar() {
        return new Usuario(id, nome, email, senhaHash, false);
    }

    private static String validarEmail(String valor) {
        String emailNormalizado = validarTexto(valor, "O e-mail e obrigatorio.", 150).toLowerCase(Locale.ROOT);
        if (!emailNormalizado.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("O e-mail informado e invalido.");
        }
        return emailNormalizado;
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
