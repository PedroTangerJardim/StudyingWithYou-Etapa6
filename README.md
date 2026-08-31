# StudyingWithYou - Etapa 7

Projeto Java atualizado para a Etapa 7 do Projeto Integrador. O sistema organiza usuários, disciplinas, atividades, cronograma e informações do painel, com testes unitários automatizados em JUnit 5.

## Como abrir no NetBeans

1. Abra o Apache NetBeans.
2. Selecione `File > Open Project`.
3. Escolha a pasta extraída do projeto.
4. Aguarde o reconhecimento do projeto Maven.
5. Execute a classe `br.com.studyingwithyou.app.Main`.

## Testes JUnit

No NetBeans, clique com o botão direito no projeto e selecione `Test`. Também é possível executar:

```text
mvn test
```

O projeto contém 10 testes unitários distribuídos entre:

- `AtividadeTest`: atraso e alteração de status.
- `CalculadoraResumoDashboardTest`: cálculo dos indicadores do painel.
- `AtividadeServiceTest`: validações, filtro e ordenação do cronograma.

Os testes utilizam dados em memória e relógio fixo, sem dependência do MySQL.

## Verificação pelo main

O `main()` executa 13 verificações. Ao final, deve aparecer:

```text
Resultado: 13/13 testes aprovados.
```

## Banco MySQL

O arquivo `database/schema.sql` cria o banco e as tabelas. Os testes não dependem do MySQL, pois usam repositórios em memória.

Para utilizar os repositórios JDBC, adicione o MySQL Connector/J às bibliotecas do projeto e crie a aplicação com:

```java
ConnectionFactory conexao = new MySqlConnectionFactory(
        "jdbc:mysql://localhost:3306/studyingwithyou",
        "root",
        "sua_senha");
ContextoAplicacao contexto = AplicacaoFactory.comJdbc(conexao, Clock.systemDefaultZone());
```

## Estrutura da entrega

- `src/main/java`: código-fonte da aplicação.
- `src/test/java`: projeto de testes JUnit.
- `database`: script MySQL.
- `docs`: requisitos, arquitetura e plano de testes.
- `evidencias`: registros usados no relatório.
- `pom.xml`: configuração do projeto.

## Versionamento

O projeto contém histórico Git organizado e está publicado em:

[github.com/PedroTangerJardim/StudyingWithYou-Etapa6](https://github.com/PedroTangerJardim/StudyingWithYou-Etapa6)
