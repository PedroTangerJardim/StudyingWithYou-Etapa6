package br.com.studyingwithyou.app;

import br.com.studyingwithyou.application.service.AtividadeService;
import br.com.studyingwithyou.application.service.AutenticacaoService;
import br.com.studyingwithyou.application.service.DashboardService;
import br.com.studyingwithyou.application.service.DisciplinaService;

public record ContextoAplicacao(
        AutenticacaoService autenticacaoService,
        DisciplinaService disciplinaService,
        AtividadeService atividadeService,
        DashboardService dashboardService) {
}
