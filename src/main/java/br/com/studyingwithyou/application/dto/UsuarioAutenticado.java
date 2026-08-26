package br.com.studyingwithyou.application.dto;

import java.util.UUID;

public record UsuarioAutenticado(UUID id, String nome, String email) {
}
