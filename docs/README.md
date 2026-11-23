# Sistema de Gestão de Biblioteca — Padrões de Projeto GoF

Implementação executável em Java do sistema de cadastro, reserva, empréstimo, renovação, devolução e cobrança de multas de livros da **iB2B**, aplicando integralmente os **10 padrões de projeto GoF** exigidos na disciplina de **Modelagem de Sistemas Computacionais** da **PUCPR**.

- **Instituição:** Pontifícia Universidade Católica do Paraná (PUCPR) — Bacharelado em Ciência da Computação (4º período, Turma U — 2º semestre de 2026)
- **Professor:** Prof. Edson Emílio Scalabrin
- **Equipe:**
  - Bruna Neves Sbardeloto
  - Marcos Emanoel dos Santos
  - Pedro Ferreira Carneiro Maraski

---

## Sumário

1. [Documentação Complementar](#documentação-complementar)
2. [Pré-requisitos](#pré-requisitos)
3. [Compilação e Testes Automatizados (Comando Único)](#compilação-e-testes-automatizados-comando-único)
4. [Execução da Interface CLI](#execução-da-interface-cli)
5. [Execução com Banco Relacional MySQL (Perfil `PRODUCAO`)](#execução-com-banco-relacional-mysql-perfil-producao)
6. [Configuração (`biblioteca.properties`)](#configuração-bibliotecaproperties)
7. [Estrutura do Projeto](#estrutura-do-projeto)
8. [Uso de Ferramentas e Modelos de LLM](#uso-de-ferramentas-e-modelos-de-llm)

---

## Documentação Complementar

- **[RASTREABILIDADE.md](RASTREABILIDADE.md):** tabela completa ligando os 10 requisitos (`R01` a `R10`) aos padrões GoF, classes/papéis, testes JUnit e lista de ajustes em relação ao UML do Trabalho 02.
- **[REGISTRO_LLM.md](REGISTRO_LLM.md):** registro dos principais pedidos feitos às LLMs durante o desenvolvimento e o que a equipe aceitou, corrigiu ou descartou.

---

## Pré-requisitos

- **Java Development Kit (JDK) 17 ou superior** (compilado com `--release 17`, validado no OpenJDK 27).
  - O `pom.xml` usa o `maven-enforcer-plugin` para exigir JDK ≥ 17. Caso o Maven aponte para um JDK antigo, configure `JAVA_HOME`:
    ```bash
    export JAVA_HOME=/usr/lib/jvm/java-27-openjdk
    ```
- **Apache Maven 3.8+**
- **Docker e Docker Compose** *(opcional — necessário apenas para executar a CLI no perfil `PRODUCAO` com MySQL sem instalar o MySQL localmente)*.

---

## Compilação e Testes Automatizados (Comando Único)

Na raiz do repositório, execute:

```bash
mvn test
```

Esse único comando:
1. Verifica a versão mínima do JDK (`maven-enforcer-plugin`).
2. Compila todo o projeto em Java puro + JDBC (sem frameworks pesados).
3. Executa toda a suíte **JUnit 5** (**41 testes**):
   - **39 testes de verificação** distribuídos de `R01_StateTest` a `R10_CompositeTest` (cobrindo todos os 10 requisitos e cenários de verificação, incluindo persistência JDBC real com transação/rollback via SQLite em escopo de teste, sem depender do Docker).
   - **2 testes integrados ponta a ponta** (`DemonstracaoGuiadaTest`), executando o fluxo completo tanto no perfil `MEMORIA` (Banco A + política original) quanto em `PRODUCAO` JDBC (Banco B + política com tolerância).

*(Após a primeira execução com as dependências já em cache, é possível rodar offline de forma instantânea com `mvn -o test`.)*

---

## Execução da Interface CLI

A interface de linha de comando chama **exclusivamente os 6 tratadores** (`TratadorDeAcervo`, `TratadorDePessoa`, `TratadorDeEstudante`, `TratadorDeAtendente`, `TratadorDeEmprestimo`, `TratadorDeDevolucao`) e não contém regras de domínio nem acesso direto a coleções internas.

### 1. Demonstração Guiada

Executa de forma automatizada e narrada via tratadores: cadastros (R07) → hierarquia do acervo (R10) → consulta pública com cache e medição (R09) → reserva e renovação idempotente como objetos Command (R06/R04/R01) → empréstimo transacional (R04/R01) → devolução atrasada com disparo de ouvintes (R05) → emissão de multa e boleto (R02/R03) → pagamento simulado pelo Banco externo com verificação de idempotência em notificação repetida (R03) → inspeção do exemplar devolvido (R01).

```bash
mvn compile exec:java -Dexec.args="--demo"
```

### 2. Menu Interativo (perfil padrão `MEMORIA`)

```bash
mvn compile exec:java
```

---

## Execução com Banco Relacional MySQL (Perfil `PRODUCAO`)

O projeto inclui um `docker-compose.yml` com **MySQL 8.4** que cria automaticamente o banco `biblioteca` e aplica `src/main/resources/schema.sql` na inicialização.

1. Suba o container do MySQL:
   ```bash
   docker compose up -d
   ```
2. Aguarde alguns segundos pela inicialização do banco e execute a aplicação no perfil `PRODUCAO`:
   - **Demonstração Guiada com MySQL:**
     ```bash
     mvn compile exec:java -Dbiblioteca.perfil=PRODUCAO -Dexec.args="--demo"
     ```
   - **Menu Interativo com MySQL:**
     ```bash
     mvn compile exec:java -Dbiblioteca.perfil=PRODUCAO
     ```
3. Ao terminar, encerre o container:
   ```bash
   docker compose down
   ```
   *(Para limpar também o volume de dados do MySQL e recomeçar zerado, use `docker compose down -v`.)*

> **Nota arquitetural sobre o Oracle (Requisito 8):** conforme especificado no Trabalho 02 e na lista de problemas GoF, o banco Oracle atua exclusivamente como réplica de leitura/espelho do MySQL. Não existe fábrica de escrita para Oracle no sistema — todas as operações de escrita no perfil `PRODUCAO` são direcionadas ao MySQL pela `FabricaRelacionalMySQL`.

---

## Configuração (`biblioteca.properties`)

As opções padrão ficam em `src/main/resources/biblioteca.properties` e podem ser sobrescritas na linha de comando via `-D<chave>=<valor>`:

| Propriedade | Valores aceitos | Padrão | Finalidade |
|---|---|---|---|
| `biblioteca.perfil` | `MEMORIA`, `PRODUCAO` | `MEMORIA` | Seleciona a `FabricaDeAcessoADados` (R08) uma única vez na inicialização |
| `biblioteca.multa.politica` | `ORIGINAL`, `COM_TETO_20`, `COM_TOLERANCIA_2_DIAS` | `ORIGINAL` | Define a `PoliticaDeMulta` vigente (R02 — Strategy) |
| `biblioteca.banco` | `BANCO_A`, `BANCO_B` | `BANCO_A` | Define o adaptador bancário `iCobranca` ativo (R03 — Adapter) |
| `biblioteca.consulta.cache` | `true`, `false` | `true` | Ativa/desativa o decorador `ConsultaComCache` (R09 — Decorator) |
| `biblioteca.consulta.medicao` | `true`, `false` | `true` | Ativa/desativa o decorador `ConsultaComMedicao` (R09 — Decorator) |
| `biblioteca.prazo.reservaDias` | inteiro > 0 | `3` | Validade registrada da reserva |
| `biblioteca.prazo.emprestimoDias` | inteiro > 0 | `14` | Prazo inicial de empréstimo |
| `biblioteca.prazo.renovacaoDias` | inteiro > 0 | `30` | Acréscimo de dias por renovação |
| `biblioteca.jdbc.url` | URL JDBC | `jdbc:mysql://localhost:3306/biblioteca?...` | Conexão do perfil `PRODUCAO` |
| `biblioteca.jdbc.usuario` | texto | `biblioteca` | Usuário JDBC |
| `biblioteca.jdbc.senha` | texto | `biblioteca` | Senha JDBC |

Exemplo executando a demonstração com Banco B e política de multa com tolerância de 2 dias:

```bash
mvn compile exec:java -Dbiblioteca.banco=BANCO_B -Dbiblioteca.multa.politica=COM_TOLERANCIA_2_DIAS -Dexec.args="--demo"
```

---

## Estrutura do Projeto

```text
SistemaBiblioteca/
├── pom.xml
├── docker-compose.yml
├── README.md
├── RASTREABILIDADE.md
├── REGISTRO_LLM.md
├── docs/uml/
│   └── DiagramaDeClasses_padroesGoF.pdf
│   └── DiagramaDeSequencia_casosTeste.pdf
│   └── MaquinaDeEstados_Livro.pdf
│   └── SistemaBiblioteca_UML.asta
├── src/main/resources/
│   ├── biblioteca.properties
│   └── schema.sql
├── src/main/java/br/pucpr/biblioteca/
│   ├── Aplicacao.java
│   ├── cli/                       # Interface CLI (MenuPrincipal, DemonstracaoGuiada, Console)
│   ├── config/                    # Configuracao, PerfilDeExecucao, RelogioAjustavel, MontadorDoSistema
│   ├── interfaces/                # iLivro, iPessoa, iEstudante, iAtendente, iEmprestimo, iDevolucao, iCirculacao, iMulta, iQuitacao, iCobranca
│   ├── dto/                       # Records imutáveis (Dados*, ComprovanteDe*, ResultadoDoCadastro)
│   ├── dominio/                   # Livro (R01 Context), estado/ (R01 State), pessoa/, transacao/, excecao/
│   ├── nucleo/                    # Pontos internos: AcervoDeLivros, Relacionamento, Movimentacao
│   ├── tratadores/                # 6 Tratadores delegando para domínio/padrões (zero lógica de negócio)
│   ├── cadastro/                  # R07 — Template Method (RotinaDeCadastro + 4 subclasses)
│   ├── circulacao/                # R04 — Facade (FachadaDeCirculacao)
│   │   ├── solicitacao/           # R06 — Command (SolicitacaoDe*, ExecutorDeSolicitacoes)
│   │   └── devolucao/             # R05 — Observer (PublicadorDeDevolucao + 4 ouvintes + FilaDeReenvio)
│   ├── financeiro/                # ServicoDeMultas, Multa, ConfiguracaoAdministrativa
│   │   ├── politica/              # R02 — Strategy (PoliticaDeMulta + 3 estratégias)
│   │   └── banco/                 # R03 — Adapter (BancoA/B simulados + AdaptadorBancoA/B)
│   ├── consulta/                  # R09 — Decorator (ConsultaAoAcervo + Cache + Medicao)
│   ├── organizacao/               # R10 — Composite (ComponenteDoAcervo, GrupoDoAcervo, ItemDeLivro)
│   └── persistencia/              # R08 — Abstract Factory (memoria/ e jdbc/)
└── src/test/java/br/pucpr/biblioteca/
    ├── R01_StateTest.java ... R10_CompositeTest.java
    ├── DemonstracaoGuiadaTest.java
    └── apoio/CenarioDeTeste.java
```

---

## Uso de Ferramentas e Modelos de LLM

Conforme as diretrizes da disciplina, o desenvolvimento utilizou LLMs sob supervisão, validação técnica e responsabilidade integral da equipe. O histórico detalhado de pedidos, aceites, correções e descartes encontra-se em **[REGISTRO_LLM.md](REGISTRO_LLM.md)**.

| Ferramenta | Modelos utilizados | Partes do sistema em que foram utilizados |
|---|---|---|
| **Google Antigravity** | **Claude Opus 5.5**, **Claude Sonnet 5.5** e **GPT-OSS 120B** | **Fase 1:** leitura dos enunciados, slides, exemplos em `pp/` e análise visual do `trabalho2_UML.pdf`; elaboração do plano arquitetural e levantamento de divergências.<br>**Início da Fase 2:** criação do `pom.xml` inicial, `.gitignore`, entidades base de `dominio/` (`Livro`, `pessoa/`, `transacao/`, `excecao/`, esqueleto inicial de `estado/`), `dto/` e contratos abstratos de `persistencia/`. |
| **Google Gemini** (interface web) | **Gemini 3.1 Pro** | **Planejamento e Revisão:** estruturação do prompt em fases, validação arquitetural da Fase 1 e apoio na resolução de ambiente (`JAVA_HOME` / Maven).<br>**Conclusão da Fase 2:** `persistencia/memoria/`, `persistencia/jdbc/`, `schema.sql`, `interfaces/`, `nucleo/`, `cadastro/` (R07), `organizacao/` (R10), tratadores de cadastro e testes `R07`, `R08` e `R10`.<br>**Fase 3:** `R01` (State completo com 7 estados), `R02` (Strategy), `R03` (Adapter), `R04` (Facade), `R05` (Observer), `R06` (Command), `R09` (Decorator), tratadores restantes, `config/` (`MontadorDoSistema`) e testes `R01`–`R06` e `R09`.<br>**Fase 4:** interface CLI (`Aplicacao`, `MenuPrincipal`, `DemonstracaoGuiada`, `Console`), `biblioteca.properties`, `docker-compose.yml`, `DemonstracaoGuiadaTest`, `README.md`, `RASTREABILIDADE.md`, `REGISTRO_LLM.md` e `docs/uml/GUIA_ATUALIZACAO_UML.md`. |