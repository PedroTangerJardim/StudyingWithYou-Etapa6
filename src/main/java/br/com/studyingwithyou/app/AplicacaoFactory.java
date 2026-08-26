package br.com.studyingwithyou.app;

import br.com.studyingwithyou.application.service.AtividadeService;
import br.com.studyingwithyou.application.service.AutenticacaoService;
import br.com.studyingwithyou.application.service.DashboardService;
import br.com.studyingwithyou.application.service.DisciplinaService;
import br.com.studyingwithyou.domain.repository.AtividadeRepository;
import br.com.studyingwithyou.domain.repository.DisciplinaRepository;
import br.com.studyingwithyou.domain.repository.UsuarioRepository;
import br.com.studyingwithyou.domain.security.SenhaHasher;
import br.com.studyingwithyou.infrastructure.persistence.jdbc.ConnectionFactory;
import br.com.studyingwithyou.infrastructure.persistence.jdbc.JdbcAtividadeRepository;
import br.com.studyingwithyou.infrastructure.persistence.jdbc.JdbcDisciplinaRepository;
import br.com.studyingwithyou.infrastructure.persistence.jdbc.JdbcUsuarioRepository;
import br.com.studyingwithyou.infrastructure.persistence.memory.InMemoryAtividadeRepository;
import br.com.studyingwithyou.infrastructure.persistence.memory.InMemoryDisciplinaRepository;
import br.com.studyingwithyou.infrastructure.persistence.memory.InMemoryUsuarioRepository;
import br.com.studyingwithyou.infrastructure.security.Pbkdf2SenhaHasher;
import java.time.Clock;

public final class AplicacaoFactory {

    private AplicacaoFactory() {
    }

    public static ContextoAplicacao emMemoria(Clock clock) {
        return criar(
                new InMemoryUsuarioRepository(),
                new InMemoryDisciplinaRepository(),
                new InMemoryAtividadeRepository(),
                new Pbkdf2SenhaHasher(),
                clock);
    }

    public static ContextoAplicacao comJdbc(ConnectionFactory connectionFactory, Clock clock) {
        return criar(
                new JdbcUsuarioRepository(connectionFactory),
                new JdbcDisciplinaRepository(connectionFactory),
                new JdbcAtividadeRepository(connectionFactory),
                new Pbkdf2SenhaHasher(),
                clock);
    }

    public static ContextoAplicacao criar(
            UsuarioRepository usuarioRepository,
            DisciplinaRepository disciplinaRepository,
            AtividadeRepository atividadeRepository,
            SenhaHasher senhaHasher,
            Clock clock) {
        return new ContextoAplicacao(
                new AutenticacaoService(usuarioRepository, senhaHasher),
                new DisciplinaService(disciplinaRepository),
                new AtividadeService(atividadeRepository, disciplinaRepository, clock),
                new DashboardService(atividadeRepository, disciplinaRepository, clock));
    }
}
