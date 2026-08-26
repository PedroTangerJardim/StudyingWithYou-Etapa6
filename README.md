# StudyingWithYou - Etapa 6

Projeto Java criado para a Etapa 6 do Projeto Integrador. O sistema organiza usuários, disciplinas, atividades, cronograma e informações do painel.

## Como abrir no NetBeans

1. Abra o Apache NetBeans.
2. Selecione `File > Open Project`.
3. Escolha a pasta `StudyingWithYou_Etapa6`.
4. Aguarde o reconhecimento do projeto Maven.
5. Execute a classe `br.com.studyingwithyou.app.Main`.

## Resultado esperado

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

- `src/main/java`: código-fonte.
- `database`: script MySQL.
- `docs`: requisitos e arquitetura.
- `evidencias`: registros usados no relatório.
- `pom.xml`: configuração do projeto.

## Versionamento

O projeto contém histórico Git organizado e está publicado em:

[github.com/PedroTangerJardim/StudyingWithYou-Etapa6](https://github.com/PedroTangerJardim/StudyingWithYou-Etapa6)
