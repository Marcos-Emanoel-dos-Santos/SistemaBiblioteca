# Registro de Uso de LLMs — Sistema de Gestão de Biblioteca

- **Disciplina:** Modelagem de Sistemas Computacionais (PUCPR — 2º semestre de 2026)
- **Equipe:** Bruna Neves Sbardeloto, Marcos Emanoel dos Santos, Pedro Ferreira Carneiro Maraski
- **Ferramentas e modelos utilizados:**
  - **Google Antigravity:** Claude Opus 5.5, Claude Sonnet 5.5 e GPT-OSS 120B
  - **Google Gemini (web):** Gemini 3.1 Pro

Este arquivo resume os principais pedidos feitos aos modelos ao longo das etapas do desenvolvimento e registra explicitamente o que a equipe **aceitou**, **corrigiu** ou **descartou**.

---

## Etapa 0 — Formulação do Prompt e Estratégia de Execução

- **Ferramenta / Modelo:** Google Gemini (Gemini 3.1 Pro)
- **Pedido principal:** elaborar um prompt estruturado para guiar o desenvolvimento no Antigravity utilizando a pasta local `helper/` (contendo os enunciados dos Trabalhos 02 e 03, a lista `Problemas_GoF_Biblioteca.pdf`, slides dos padrões, códigos-exemplo em `pp/` e o UML da equipe), impondo execução em **4 fases com checkpoints obrigatórios**, Java 17+ com Maven e JUnit 5, interface CLI e ausên cia de frameworks pesados (sem Spring/Hibernate) para manter os 10 padrões GoF explícitos.
- **Aceito pela equipe:**
  - Execução faseada (Fase 1 de análise sem código → Fases 2, 3 e 4 incrementais) para revisão contínua e domínio total do código pela equipe.
  - Exportação do arquivo binário `trabalho2_UML.asta` para `trabalho2_UML.pdf` dentro de `helper/`, permitindo leitura visual fiel das classes e diagramas.
  - Uso de JDBC puro + `Abstract Factory` em vez de ORM/frameworks.
- **Ajustes feitos pela equipe:**
  - Confirmação de interface em linha de comando (CLI), priorização de MySQL para o perfil de produção (com apoio de SQLite apenas nos testes automatizados JDBC) e preservação da nomenclatura do UML do Trabalho 02.

---

## Fase 1 — Análise do `helper/` e Plano de Arquitetura

- **Ferramenta / Modelo:** Google Antigravity (Claude Opus 5.5 / Claude Sonnet 5.5 / GPT-OSS 120B) + revisão no Google Gemini (Gemini 3.1 Pro)
- **Pedido principal:** ler todo o conteúdo de `helper/` sem escrever código ainda, inventariar os requisitos, mapear os 10 padrões GoF para a estrutura de pacotes e apontar divergências entre o UML do Trabalho 02 e os enunciados do Trabalho 03.
- **Aceito pela equipe:**
  - Estrutura de pacotes em `br.pucpr.biblioteca` separando claramente `dominio`, `nucleo`, `tratadores`, `cadastro`, `circulacao`, `financeiro`, `consulta`, `organizacao`, `persistencia`, `config` e `cli`.
  - Aproveitamento da nomenclatura de Observer presente nos slides do professor (`PublicadorDeDevolucao`, `OuvinteDeDevolucao`, `FilaDeReenvio`).
  - Reaproveitamento das interfaces de componentes do Trabalho 02 (`iMulta`, `iQuitacao`, `iCobranca`).
  - Uso de `java.time.Clock` injetável para testes determinísticos e simulação de atraso na demonstração guiada sem poluir regras de domínio.
  - Identificação de divergências técnicas do UML anterior (`LocalDate` no lugar de `String` para datas, `String` para CPF, substituição de `estado : String` por `EstadoLivro`, guarda `numRenovacoes < 2`).
- **Corrigido / Decidido pela equipe (respostas Q1 a Q5):**
  - **Q1:** confirmado `AcervoDeLivros` para preservar coerência direta com o UML da equipe.
  - **Q2:** rejeitada a transição incorreta `LIVRE → INCINERADO` do UML anterior; mantida a transição T9 do Trabalho 02 (`enviarParaReparo`: `LIVRE → REPARANDO`) e mantido `avaliarReparo(consertado)` delegando para `registrarLaudoIrrecuperavel()` (`REPARANDO → INCINERADO`).
  - **Q3:** confirmado MySQL como padrão do perfil `PRODUCAO`, mantendo SQLite exclusivamente no escopo `test` do Maven para exercitar `commit`/`rollback` JDBC em `mvn test` sem exigir Docker.
  - **Q4:** corrigido o ambiente local da máquina de desenvolvimento, pois o Maven estava usando `JAVA_HOME=/usr/lib/jvm/java-11-openjdk` (herdado de uso anterior com Hadoop) enquanto o sistema possuía OpenJDK 27. Alterado `JAVA_HOME` para `/usr/lib/jvm/java-27-openjdk` e adicionado `maven-enforcer-plugin` exigindo JDK ≥ 17.
  - **Q5:** inicializado o repositório Git com `helper/` no `.gitignore`.
- **Descartado pela equipe:**
  - Geração antecipada de guia de estudo para arguição junto com o código; a equipe optou por deixar esse material para depois da finalização e revisão do sistema.

---

## Fase 2 — Fundação, Domínio Base e Padrões R07, R08 e R10

- **Ferramenta / Modelo:** início no Google Antigravity (interrompido por limite de cota após 35 arquivos); conclusão no Google Gemini (Gemini 3.1 Pro)
- **Pedido principal:** inicializar o projeto Maven (`pom.xml` + `.gitignore`), implementar o modelo de domínio base, DTOs, interfaces, pontos internos (`AcervoDeLivros`, `Relacionamento`, `Movimentacao`), `Abstract Factory` (família em memória e família relacional JDBC + `schema.sql`), `Template Method` nos cadastros, `Composite` na hierarquia do acervo e os testes JUnit `R07`, `R08` e `R10`.
- **Aceito pela equipe:**
  - Os 35 arquivos iniciais gerados pelo Antigravity (verificados com `mvn test-compile` limpo antes da continuidade).
  - Implementação das famílias `persistencia/memoria` (com snapshot/rollback transacional) e `persistencia/jdbc` (conexão compartilhada preguiçosa na `UnidadeDeTrabalhoJdbc`), além da proteção `FamiliaIncompativelException`.
  - Implementação de `RotinaDeCadastro` (`incluir` marcado como `final` e gancho `verificarPreRequisitos`) e `GrupoDoAcervo`/`ItemDeLivro` com bloqueio de ciclos e unicidade por livro.
- **Corrigido pela equipe:**
  - Na primeira execução de `mvn test`, o teste `R10_unidadeComDuasSecoesInformaCincoLivros` falhou por expectativa de ordenação da lista (`[101, 102, 103, 201, 202]` vs. `[103, 101, 102, 201, 202]`, pois a subseção contendo `103` havia sido adicionada antes dos livros diretos da seção). O teste foi corrigido e os 12 testes da Fase 2 passaram integralmente.

---

## Fase 3 — Padrões R01–R06, R09, Tratadores e Montador do Sistema

- **Ferramenta / Modelo:** Google Gemini (Gemini 3.1 Pro)
- **Pedido principal:** completar a máquina de estados (`R01`), implementar `Strategy` (`R02`), `Adapter` para os Bancos A e B simulados (`R03`), `Facade` transacional (`R04`), `Observer` com isolamento de falhas (`R05`), `Command` idempotente e concorrente (`R06`), `Decorator` de consulta com cache e medição (`R09`), os tratadores restantes, o `MontadorDoSistema` e os testes `R01` a `R09`.
- **Aceito pela equipe:**
  - Separação estrita de papéis nos 7 padrões, sem condicionais de estado nos tratadores e sem condicionais de banco no `ServicoDeMultas`.
  - `ExecutorDeSolicitacoes` com `ConcurrentHashMap` + `CompletableFuture` garantindo execução única sob submissões simultâneas de mesmo `id`.
  - `MetadadosLivro` no cache sem atributo de estado, impedindo por projeto que dados em cache autorizem reserva.
  - Validação completa via `mvn test`: 126 classes compiladas sem warnings e 39/39 testes aprovados de primeira.

---

## Fase 4 — Interface CLI, Infraestrutura Docker/MySQL e Documentação

- **Ferramenta / Modelo:** Google Gemini (Gemini 3.1 Pro)
- **Pedido principal:** dividir a última etapa em duas entregas — (4A) CLI interativa + Demonstração Guiada + `biblioteca.properties` + `docker-compose.yml` com MySQL 8.4 + `exec-maven-plugin` no `pom.xml` + `DemonstracaoGuiadaTest`; (4B) documentação entregável (`README.md`, `RASTREABILIDADE.md`, `REGISTRO_LLM.md` e guia de atualização do UML).
- **Aceito pela equipe:**
  - Divisão da Fase 4 em 4A e 4B para validar execução real antes de fechar a documentação.
  - Uso do `docker-compose.yml` para subir MySQL 8.4 já inicializado com `schema.sql`, eliminando a necessidade de instalar MySQL diretamente no sistema operacional.
  - Criação explícita de objetos `SolicitacaoDeReserva` e `SolicitacaoDeRenovacao` na camada CLI entregues ao `TratadorDeEmprestimo`, deixando claro o papel Client do padrão Command.
  - Validação de todos os 41 testes automatizados e execução bem-sucedida da CLI e da Demonstração Guiada nos perfis `MEMORIA` e `PRODUCAO`.
- **Corrigido / Ajustado durante a entrega:**
  - A geração da documentação precisou ser entregue em arquivos `.txt` individuais para renomeação local para `.md`, devido a uma falha de entrega de anexo `.zip` na interface do chat.

