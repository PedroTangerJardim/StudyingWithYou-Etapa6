CREATE DATABASE IF NOT EXISTS studyingwithyou
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE studyingwithyou;

CREATE TABLE IF NOT EXISTS usuarios (
    id CHAR(36) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(512) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS disciplinas (
    id CHAR(36) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    professor VARCHAR(100) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS atividades (
    id CHAR(36) PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descricao VARCHAR(500) NOT NULL DEFAULT '',
    disciplina_id CHAR(36) NOT NULL,
    data_entrega DATE NOT NULL,
    prioridade VARCHAR(10) NOT NULL,
    status VARCHAR(12) NOT NULL,
    CONSTRAINT fk_atividade_disciplina
        FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id),
    CONSTRAINT chk_prioridade
        CHECK (prioridade IN ('BAIXA', 'MEDIA', 'ALTA')),
    CONSTRAINT chk_status
        CHECK (status IN ('PENDENTE', 'CONCLUIDA')),
    INDEX idx_atividades_data_entrega (data_entrega),
    INDEX idx_atividades_disciplina (disciplina_id),
    INDEX idx_atividades_status (status)
);
