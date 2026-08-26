# Requisitos do sistema StudyingWithYou

## Objetivo

O StudyingWithYou organiza a rotina de estudos do usuário. O núcleo desta etapa foi desenvolvido sem dependência de interface gráfica para que possa ser utilizado posteriormente por uma aplicação web.

## Requisitos funcionais

- RF01: cadastrar usuário com nome, e-mail e senha.
- RF02: autenticar usuário por e-mail e senha.
- RF03: cadastrar, consultar, editar, ativar e desativar disciplinas.
- RF04: impedir o cadastro de disciplinas com nomes repetidos.
- RF05: cadastrar, consultar, editar e excluir atividades.
- RF06: definir data de entrega, prioridade e status para cada atividade.
- RF07: concluir e reabrir atividades.
- RF08: consultar atividades por disciplina e por intervalo de datas.
- RF09: ordenar o cronograma pela data de entrega.
- RF10: apresentar um resumo com disciplinas ativas e atividades pendentes, concluídas, atrasadas e próximas do vencimento.

## Regras de negócio

- RN01: e-mails não podem ser repetidos.
- RN02: a senha deve possuir no mínimo oito caracteres.
- RN03: disciplinas não podem possuir nomes repetidos, sem diferenciar letras maiúsculas e minúsculas.
- RN04: uma atividade deve estar ligada a uma disciplina ativa.
- RN05: a data de entrega de uma nova atividade não pode estar no passado.
- RN06: somente atividades pendentes podem ser concluídas.
- RN07: somente atividades concluídas podem ser reabertas.

## Requisitos não funcionais

- RNF01: utilizar Java 17.
- RNF02: manter as regras de negócio independentes de Swing, HTML ou frameworks web.
- RNF03: permitir a troca do mecanismo de persistência por meio de interfaces.
- RNF04: proteger senhas com PBKDF2 e salt aleatório.
- RNF05: utilizar consultas parametrizadas no acesso JDBC.
- RNF06: permitir execução dos testes principais pelo método `main()`.
