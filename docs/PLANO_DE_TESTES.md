# Plano de Testes - StudyingWithYou

## Identificação

- Projeto: StudyingWithYou.
- Etapa: 7 do Projeto Integrador.
- Versão: 1.1.0.
- Ambiente: Java 17, Maven e Apache NetBeans.
- Framework: JUnit Jupiter 5.10.3.

## Objetivo

Verificar as principais regras de negócio do sistema atual e preparar uma base de validação para a futura aplicação web. Os testes automatizados não acessam o banco de dados; utilizam objetos de domínio e repositórios em memória.

## Escopo

O plano cobre cadastro e autenticação de usuários, disciplinas, atividades, cronograma, dashboard, persistência, segurança e os fluxos planejados para a interface web.

## Estratégia

- Testes unitários JUnit para cálculos, validações e ordenações determinísticas.
- Testes de integração para serviços com repositórios em memória.
- Testes manuais para interface web, navegação, mensagens e usabilidade.
- Testes de persistência MySQL em ambiente separado.

## Casos automatizados JUnit

| ID | Requisito | Cenário | Resultado esperado |
|---|---|---|---|
| JU01 | RF06/RF10 | Atividade pendente vencida | Marcada como atrasada |
| JU02 | RF06/RF10 | Atividade com vencimento hoje | Não marcada como atrasada |
| JU03 | RF07/RF10 | Atividade concluída e vencida | Não marcada como atrasada |
| JU04 | RF07 | Concluir e reabrir atividade | Status alterado e identificador preservado |
| JU05 | RF10 | Calcular resumo com atividades em diferentes datas | Quantidades calculadas corretamente |
| JU06 | RF10 | Calcular resumo sem dados | Todos os indicadores iguais a zero |
| JU07 | RN05 | Cadastrar atividade com data passada | Operação recusada |
| JU08 | RF08/RF09 | Consultar cronograma de sete dias | Itens filtrados e ordenados por data |
| JU09 | RF08 | Consultar intervalo invertido | Operação recusada |
| JU10 | RN04 | Cadastrar atividade em disciplina desativada | Operação recusada |

## Casos manuais principais

| ID | Área | Pré-condição | Procedimento resumido | Resultado esperado |
|---|---|---|---|---|
| TM01 | Usuário | Nenhuma conta com o e-mail | Preencher cadastro válido e confirmar | Conta criada e senha não exibida |
| TM02 | Usuário | Conta existente | Tentar cadastrar o mesmo e-mail com outra caixa | Mensagem de e-mail já utilizado |
| TM03 | Autenticação | Conta ativa | Informar credenciais corretas | Sessão iniciada e painel exibido |
| TM04 | Autenticação | Conta ativa | Informar senha incorreta | Acesso recusado sem revelar detalhes sensíveis |
| TM05 | Disciplina | Usuário autenticado | Cadastrar disciplina válida | Disciplina exibida na lista |
| TM06 | Disciplina | Disciplina existente | Cadastrar nome equivalente com outra caixa | Duplicidade recusada |
| TM07 | Disciplina | Disciplina ativa | Desativar disciplina | Disciplina identificada como inativa |
| TM08 | Atividade | Disciplina ativa | Cadastrar atividade válida | Atividade criada como pendente |
| TM09 | Atividade | Disciplina ativa | Informar data anterior à atual | Cadastro recusado com mensagem clara |
| TM10 | Atividade | Disciplina inativa | Tentar cadastrar atividade | Cadastro recusado |
| TM11 | Atividade | Atividade pendente | Editar título, data e prioridade | Dados atualizados sem trocar o identificador |
| TM12 | Atividade | Atividade pendente | Concluir atividade | Status alterado para concluída |
| TM13 | Atividade | Atividade concluída | Reabrir atividade | Status alterado para pendente |
| TM14 | Cronograma | Atividades em datas variadas | Filtrar por período | Somente itens do intervalo, em ordem cronológica |
| TM15 | Dashboard | Dados cadastrados | Abrir o painel | Indicadores compatíveis com os registros |
| TM16 | Persistência | MySQL configurado | Reiniciar aplicação após cadastrar dados | Dados permanecem disponíveis |
| TM17 | Web | Aplicação publicada | Navegar por cadastro, login, disciplinas e atividades | Fluxo completo sem páginas quebradas |
| TM18 | Web | Aplicação em navegador | Reduzir largura da janela | Conteúdo permanece legível e operável |

## Critérios de entrada

- Código compila com Java 17.
- Dependências JUnit disponíveis.
- Repositórios em memória funcionais.
- Banco configurado somente para os testes de persistência.

## Critérios de saída

- Todos os testes JUnit aprovados.
- Nenhum defeito crítico ou alto em aberto.
- Fluxos manuais principais aprovados.
- Evidências anexadas ao projeto e versionadas no GitHub.

## Registro de defeitos

Cada defeito deve informar identificador, caso relacionado, passos para reprodução, resultado esperado, resultado obtido, severidade, ambiente e evidência visual. Classificação sugerida: crítica, alta, média ou baixa.

## Evidências previstas

- Saída da execução dos testes JUnit.
- Relatório de testes gerado pelo Maven Surefire.
- Histórico Git contendo os commits da Etapa 7.
- Captura do repositório GitHub com os testes e documentos publicados.
