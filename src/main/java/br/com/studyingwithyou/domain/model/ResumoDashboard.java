package br.com.studyingwithyou.domain.model;

public record ResumoDashboard(
        long disciplinasAtivas,
        long atividadesTotais,
        long atividadesPendentes,
        long atividadesConcluidas,
        long atividadesAtrasadas,
        long proximosSeteDias) {
}
