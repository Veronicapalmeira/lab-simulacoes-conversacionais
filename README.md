# lab-simulacoes-conversacionais

POC 2.3: Laboratório de Simulações Conversacionais. O professor conduz, na tela projetada, uma conversa da turma com um personagem simulado por IA.

Aplicação web única em Java, organizada como monólito modular com Spring Modulith.

## Como rodar localmente

Pré-requisito: **JDK 21** com `JAVA_HOME` configurado. Não é preciso instalar o Maven, porque o projeto traz o Maven Wrapper.

| Ação | Linux/macOS/Git Bash | Windows (PowerShell) |
| --- | --- | --- |
| Compilar e rodar os testes | `./mvnw verify` | `.\mvnw.cmd verify` |
| Subir a aplicação | `./mvnw spring-boot:run` | `.\mvnw.cmd spring-boot:run` |

A aplicação sobe em <http://localhost:8080>. Por enquanto ela não tem telas, então a resposta 404 em `/` é esperada.

## Módulos

Cada módulo é um subpacote direto de `br.ufg.inf.labsimulacoes`:

| Pacote | Módulo | Responsabilidade |
| --- | --- | --- |
| `acesso` | Acesso | Usuários, perfis, turmas, parâmetros e autenticação |
| `cenarios` | Cenários | Cenários, versões, BNCC, quiz, curadoria e biblioteca |
| `sessoes` | Sessões | Sessões em sala, mensagens, controles, aplicação do quiz e engajamento |
| `auditoria` | Auditoria | Conteúdo bloqueado, sinalizações, triagem, suspensão e log de ações críticas |
| `metricas` | Métricas | Indicadores, comparação com sessões de controle, painel e exportação |
| `ia` | Adaptador de IA | Porta única para o provedor de IA |

O pacote `eventos`, no mesmo nível, guarda só os tipos dos eventos de domínio. Ele não é um módulo de negócio, mas existe para que dois módulos possam publicar e escutar eventos um do outro sem formar um ciclo.

Dentro de cada módulo, o código fica em quatro camadas, e a dependência vai sempre de cima para baixo:

| Subpacote | Camada | Conteúdo |
| --- | --- | --- |
| `apresentacao` | Apresentação | Telas, fragmentos HTML, formulários e streaming |
| `aplicacao` | Aplicação | Casos de uso, transações e verificação de perfil |
| `dominio` | Domínio | Entidades, estados e regras de negócio, sem dependência de framework |
| `infraestrutura` | Infraestrutura | Repositórios, cache, chamadas externas e tarefas agendadas |

## Regras de fronteira

- A API de um módulo é só o seu pacote raiz. Os subpacotes são internos e inacessíveis aos outros módulos.
- Os módulos não podem formar ciclos de dependência.
- Um módulo só pode depender dos módulos declarados em `allowedDependencies`, no `package-info.java` dele:

| Módulo | Pode depender de |
| --- | --- |
| `acesso` | `eventos` |
| `cenarios` | `acesso`, `ia`, `eventos` |
| `sessoes` | `acesso`, `cenarios`, `ia`, `eventos` |
| `auditoria` | `acesso`, `eventos` |
| `metricas` | `acesso`, `eventos` |
| `ia` | nenhum |
| `eventos` | nenhum |

- Dentro de um módulo, as camadas só dependem das camadas de baixo, e a apresentação não usa a infraestrutura diretamente. A infraestrutura pode usar o domínio, e o inverso não vale.
- O domínio só pode depender do Java e do próprio sistema. Por isso, anotações de JPA e Spring ficam fora dele.

O `ArquiteturaModulosTest` roda `ApplicationModules.verify()` e o `ArquiteturaCamadasTest` confere as camadas a cada build. Se uma dessas regras for violada, o teste e o build falham, e a mensagem indica a classe ou o ciclo responsável.

A maioria das classes em `src/test/java/br/ufg/inf/fixturesmodulith` viola as regras de propósito. Elas garantem que a verificação continua detectando os problemas e não fazem parte do sistema.

## Documentação

- Requisitos POC 2.3 v2: requisitos funcionais, regras de negócio (RN) e requisitos não funcionais (RNF).
- Stack Tecnológica POC 2.3: tecnologias, arquitetura e regras de gerência de configuração.
