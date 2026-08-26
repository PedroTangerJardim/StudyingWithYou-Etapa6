package br.com.studyingwithyou.tests;

import br.com.studyingwithyou.app.AplicacaoFactory;
import br.com.studyingwithyou.app.ContextoAplicacao;
import br.com.studyingwithyou.application.dto.EdicaoAtividade;
import br.com.studyingwithyou.application.dto.NovaAtividade;
import br.com.studyingwithyou.application.dto.NovaDisciplina;
import br.com.studyingwithyou.application.dto.NovoUsuario;
import br.com.studyingwithyou.application.dto.UsuarioAutenticado;
import br.com.studyingwithyou.domain.model.Atividade;
import br.com.studyingwithyou.domain.model.Disciplina;
import br.com.studyingwithyou.domain.model.Prioridade;
import br.com.studyingwithyou.domain.model.ResumoDashboard;
import br.com.studyingwithyou.domain.model.StatusAtividade;
import br.com.studyingwithyou.shared.exception.EntidadeNaoEncontradaException;
import br.com.studyingwithyou.shared.exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public final class TestesAplicacao {

    private static final LocalDate HOJE = LocalDate.of(2026, 8, 11);
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-08-11T15:00:00Z"),
            ZoneId.of("America/Sao_Paulo"));
    private int executados;
    private int aprovados;

    public void executar() {
        testar("cadastro e autenticacao de usuario", this::testarAutenticacao);
        testar("senha incorreta e recusada", this::testarSenhaIncorreta);
        testar("e-mail duplicado e recusado", this::testarUsuarioDuplicado);
        testar("disciplinas sao cadastradas e ordenadas", this::testarDisciplinas);
        testar("nome de disciplina duplicado e recusado", this::testarDisciplinaDuplicada);
        testar("atividade e cadastrada", this::testarCadastroAtividade);
        testar("data de entrega no passado e recusada", this::testarDataPassada);
        testar("cronograma filtra e ordena atividades", this::testarCronograma);
        testar("atividade e editada", this::testarEdicaoAtividade);
        testar("atividade pode ser concluida e reaberta", this::testarConclusao);
        testar("dashboard resume os dados", this::testarDashboard);
        testar("disciplina desativada nao recebe atividade", this::testarDisciplinaDesativada);
        testar("atividade e excluida", this::testarExclusao);
        System.out.println();
        System.out.println("Resultado: " + aprovados + "/" + executados + " testes aprovados.");
        if (aprovados != executados) {
            throw new AssertionError("Existem testes com falha.");
        }
    }

    private void testarAutenticacao() {
        ContextoAplicacao contexto = novoContexto();
        UsuarioAutenticado cadastrado = contexto.autenticacaoService().cadastrar(
                new NovoUsuario("Pedro Jardim", "pedro@estudos.com", "Senha123"));
        UsuarioAutenticado autenticado = contexto.autenticacaoService().autenticar(
                "PEDRO@ESTUDOS.COM", "Senha123");
        igual(cadastrado.id(), autenticado.id());
        igual("pedro@estudos.com", autenticado.email());
    }

    private void testarSenhaIncorreta() {
        ContextoAplicacao contexto = novoContexto();
        contexto.autenticacaoService().cadastrar(
                new NovoUsuario("Pedro Jardim", "pedro@estudos.com", "Senha123"));
        lanca(RegraNegocioException.class,
                () -> contexto.autenticacaoService().autenticar("pedro@estudos.com", "OutraSenha"));
    }

    private void testarUsuarioDuplicado() {
        ContextoAplicacao contexto = novoContexto();
        contexto.autenticacaoService().cadastrar(
                new NovoUsuario("Pedro Jardim", "pedro@estudos.com", "Senha123"));
        lanca(RegraNegocioException.class,
                () -> contexto.autenticacaoService().cadastrar(
                        new NovoUsuario("Outro Nome", "PEDRO@ESTUDOS.COM", "Senha456")));
    }

    private void testarDisciplinas() {
        ContextoAplicacao contexto = novoContexto();
        contexto.disciplinaService().cadastrar(new NovaDisciplina("Programacao Web", "Ana Silva"));
        contexto.disciplinaService().cadastrar(new NovaDisciplina("Banco de Dados", "Carlos Lima"));
        List<Disciplina> disciplinas = contexto.disciplinaService().listarTodas();
        igual(2, disciplinas.size());
        igual("Banco de Dados", disciplinas.get(0).nome());
        verdadeiro(disciplinas.stream().allMatch(Disciplina::ativa));
    }

    private void testarDisciplinaDuplicada() {
        ContextoAplicacao contexto = novoContexto();
        contexto.disciplinaService().cadastrar(new NovaDisciplina("Banco de Dados", "Carlos Lima"));
        lanca(RegraNegocioException.class,
                () -> contexto.disciplinaService().cadastrar(
                        new NovaDisciplina(" banco de dados ", "Outro Professor")));
    }

    private void testarCadastroAtividade() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        Atividade atividade = contexto.atividadeService().cadastrar(new NovaAtividade(
                "Modelar banco",
                "Criar o diagrama entidade-relacionamento",
                disciplina.id(),
                HOJE.plusDays(5),
                Prioridade.ALTA));
        igual(StatusAtividade.PENDENTE, atividade.status());
        igual(disciplina.id(), atividade.disciplinaId());
        igual(1, contexto.atividadeService().listarTodas().size());
    }

    private void testarDataPassada() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        lanca(RegraNegocioException.class,
                () -> contexto.atividadeService().cadastrar(new NovaAtividade(
                        "Atividade antiga",
                        "",
                        disciplina.id(),
                        HOJE.minusDays(1),
                        Prioridade.BAIXA)));
    }

    private void testarCronograma() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        criarAtividade(contexto, disciplina, "Terceira", 10, Prioridade.BAIXA);
        criarAtividade(contexto, disciplina, "Primeira", 1, Prioridade.ALTA);
        criarAtividade(contexto, disciplina, "Segunda", 4, Prioridade.MEDIA);
        List<Atividade> cronograma = contexto.atividadeService()
                .listarCronograma(HOJE, HOJE.plusDays(7));
        igual(2, cronograma.size());
        igual("Primeira", cronograma.get(0).titulo());
        igual("Segunda", cronograma.get(1).titulo());
    }

    private void testarEdicaoAtividade() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        Atividade atividade = criarAtividade(contexto, disciplina, "Texto inicial", 3, Prioridade.BAIXA);
        Atividade editada = contexto.atividadeService().editar(atividade.id(), new EdicaoAtividade(
                "Texto revisado",
                "Versao final",
                disciplina.id(),
                HOJE.plusDays(6),
                Prioridade.ALTA));
        igual(atividade.id(), editada.id());
        igual("Texto revisado", editada.titulo());
        igual(Prioridade.ALTA, editada.prioridade());
    }

    private void testarConclusao() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        Atividade atividade = criarAtividade(contexto, disciplina, "Concluir trabalho", 2, Prioridade.ALTA);
        Atividade concluida = contexto.atividadeService().concluir(atividade.id());
        igual(StatusAtividade.CONCLUIDA, concluida.status());
        Atividade reaberta = contexto.atividadeService().reabrir(atividade.id());
        igual(StatusAtividade.PENDENTE, reaberta.status());
    }

    private void testarDashboard() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        Atividade proxima = criarAtividade(contexto, disciplina, "Proxima", 2, Prioridade.ALTA);
        criarAtividade(contexto, disciplina, "Distante", 20, Prioridade.BAIXA);
        contexto.atividadeService().concluir(proxima.id());
        ResumoDashboard resumo = contexto.dashboardService().gerarResumo();
        igual(1L, resumo.disciplinasAtivas());
        igual(2L, resumo.atividadesTotais());
        igual(1L, resumo.atividadesPendentes());
        igual(1L, resumo.atividadesConcluidas());
        igual(0L, resumo.atividadesAtrasadas());
        igual(0L, resumo.proximosSeteDias());
    }

    private void testarDisciplinaDesativada() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        contexto.disciplinaService().desativar(disciplina.id());
        lanca(RegraNegocioException.class,
                () -> criarAtividade(contexto, disciplina, "Nao permitida", 2, Prioridade.MEDIA));
    }

    private void testarExclusao() {
        ContextoAplicacao contexto = novoContexto();
        Disciplina disciplina = criarDisciplina(contexto);
        Atividade atividade = criarAtividade(contexto, disciplina, "Excluir", 2, Prioridade.MEDIA);
        contexto.atividadeService().excluir(atividade.id());
        igual(0, contexto.atividadeService().listarTodas().size());
        lanca(EntidadeNaoEncontradaException.class,
                () -> contexto.atividadeService().buscarPorId(atividade.id()));
    }

    private ContextoAplicacao novoContexto() {
        return AplicacaoFactory.emMemoria(CLOCK);
    }

    private Disciplina criarDisciplina(ContextoAplicacao contexto) {
        return contexto.disciplinaService().cadastrar(
                new NovaDisciplina("Programacao Web", "Ana Silva"));
    }

    private Atividade criarAtividade(
            ContextoAplicacao contexto,
            Disciplina disciplina,
            String titulo,
            int dias,
            Prioridade prioridade) {
        return contexto.atividadeService().cadastrar(new NovaAtividade(
                titulo,
                "Atividade usada na verificacao do sistema",
                disciplina.id(),
                HOJE.plusDays(dias),
                prioridade));
    }

    private void testar(String nome, Runnable teste) {
        executados++;
        try {
            teste.run();
            aprovados++;
            System.out.println("[APROVADO] " + nome);
        } catch (Throwable erro) {
            System.out.println("[FALHOU] " + nome + ": " + erro.getMessage());
            throw erro;
        }
    }

    private void verdadeiro(boolean condicao) {
        if (!condicao) {
            throw new AssertionError("A condicao deveria ser verdadeira.");
        }
    }

    private void igual(Object esperado, Object obtido) {
        if (!java.util.Objects.equals(esperado, obtido)) {
            throw new AssertionError("Esperado: " + esperado + "; obtido: " + obtido);
        }
    }

    private void lanca(Class<? extends Throwable> tipo, Runnable acao) {
        try {
            acao.run();
        } catch (Throwable erro) {
            if (tipo.isInstance(erro)) {
                return;
            }
            throw new AssertionError("Excecao inesperada: " + erro.getClass().getSimpleName(), erro);
        }
        throw new AssertionError("Era esperada a excecao " + tipo.getSimpleName() + ".");
    }
}
