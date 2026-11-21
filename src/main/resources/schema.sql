-- Esquema relacional portavel (compativel com MySQL 8 e SQLite para testes JDBC).
-- Sem AUTO_INCREMENT: identificadores sao definidos pelo dominio/aplicacao.
-- Sem FOREIGN KEY rigida, conforme simplificacao de integridade referencial do Trabalho 02 (regras 1.3 a 4.3).

CREATE TABLE IF NOT EXISTS livros (
    id_livro INTEGER PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    estado VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS pessoas (
    cpf VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    sobrenome VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS estudantes (
    matricula INTEGER PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS atendentes (
    id_atendente INTEGER PRIMARY KEY,
    cpf VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservas (
    id VARCHAR(64) PRIMARY KEY,
    id_livro INTEGER NOT NULL,
    matricula INTEGER NOT NULL,
    data VARCHAR(20) NOT NULL,
    data_vencimento VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS emprestimos (
    id VARCHAR(64) PRIMARY KEY,
    id_livro INTEGER NOT NULL,
    matricula INTEGER NOT NULL,
    data VARCHAR(20) NOT NULL,
    data_vencimento VARCHAR(20) NOT NULL,
    num_renovacoes INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS devolucoes (
    id VARCHAR(64) PRIMARY KEY,
    id_livro INTEGER NOT NULL,
    matricula INTEGER NOT NULL,
    data VARCHAR(20) NOT NULL,
    data_vencimento VARCHAR(20) NOT NULL,
    ordem INTEGER NOT NULL
);
