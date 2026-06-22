-- ============================================================
-- SGE - Sistema de Gestão Escolar
-- Script de criação do banco de dados (MySQL 8.x)
-- ============================================================

DROP DATABASE IF EXISTS sge;
CREATE DATABASE sge DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sge;

-- ============================================================
-- Tabela: usuarios (administradores com acesso ao sistema)
-- ============================================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,  -- hash SHA-256
    nome VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Tabela: turmas
-- ============================================================
CREATE TABLE turmas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    ano INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Tabela: alunos (vinculados a uma turma)
-- ============================================================
CREATE TABLE alunos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    matricula VARCHAR(20) NOT NULL UNIQUE,
    turma_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_aluno_turma FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Tabela: notas (quantidade dinâmica por aluno)
-- ============================================================
CREATE TABLE notas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_id INT NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    valor DECIMAL(5,2) NOT NULL,
    bimestre INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_nota_aluno FOREIGN KEY (aluno_id) REFERENCES alunos(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_nota_valor CHECK (valor >= 0 AND valor <= 10),
    CONSTRAINT chk_nota_bimestre CHECK (bimestre BETWEEN 1 AND 4)
) ENGINE=InnoDB;

-- ============================================================
-- Tabela: logs (Desafio Extra - Auditoria)
-- ============================================================
CREATE TABLE logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT,
    usuario_login VARCHAR(50),
    acao VARCHAR(20) NOT NULL,        -- INSERT, UPDATE, DELETE, LOGIN, LOGOUT
    tabela VARCHAR(50) NOT NULL,
    registro_id INT,
    detalhes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Usuário administrador padrão
-- login: admin | senha: admin123 (hash SHA-256)
-- ============================================================
INSERT INTO usuarios (login, senha, nome, perfil) VALUES
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Administrador', 'ADMIN');

-- ============================================================
-- Dados de exemplo (turmas + alunos + notas) - opcional
-- ============================================================
INSERT INTO turmas (nome, descricao, ano) VALUES
('Jovem Programador', 'Curso de introdução à programação', 2026),
('Técnico em Administração', 'Curso técnico em administração', 2026),
('Informática Básica', 'Curso básico de informática', 2026);

INSERT INTO alunos (nome, matricula, turma_id) VALUES
('Ana Silva', 'ALU001', 1),
('Bruno Costa', 'ALU002', 1),
('Carla Oliveira', 'ALU003', 2),
('Daniel Pereira', 'ALU004', 3);

INSERT INTO notas (aluno_id, disciplina, valor, bimestre) VALUES
(1, 'Lógica de Programação', 8.50, 1),
(1, 'Lógica de Programação', 9.00, 2),
(1, 'Banco de Dados', 7.50, 1),
(1, 'Banco de Dados', 8.00, 2),
(2, 'Lógica de Programação', 6.00, 1),
(2, 'Lógica de Programação', 7.50, 2),
(3, 'Contabilidade', 9.00, 1),
(3, 'Contabilidade', 8.50, 2),
(4, 'Informática', 10.00, 1),
(4, 'Informática', 9.50, 2);

-- ============================================================
-- TRIGGERS (Desafio Extra - Sistema de Logs/Auditoria)
-- O contexto do usuário logado é injetado pela aplicação
-- via variáveis de sessão: @app_user_id e @app_user_login
-- ============================================================

DELIMITER $$

-- USUÁRIOS
CREATE TRIGGER trg_usuarios_insert AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'INSERT', 'usuarios', NEW.id,
            CONCAT('Usuário criado: ', NEW.login));
END$$

CREATE TRIGGER trg_usuarios_update AFTER UPDATE ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'UPDATE', 'usuarios', NEW.id,
            CONCAT('Usuário atualizado: ', NEW.login));
END$$

CREATE TRIGGER trg_usuarios_delete AFTER DELETE ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'DELETE', 'usuarios', OLD.id,
            CONCAT('Usuário removido: ', OLD.login));
END$$

-- TURMAS
CREATE TRIGGER trg_turmas_insert AFTER INSERT ON turmas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'INSERT', 'turmas', NEW.id,
            CONCAT('Turma criada: ', NEW.nome));
END$$

CREATE TRIGGER trg_turmas_update AFTER UPDATE ON turmas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'UPDATE', 'turmas', NEW.id,
            CONCAT('Turma atualizada: ', NEW.nome));
END$$

CREATE TRIGGER trg_turmas_delete AFTER DELETE ON turmas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'DELETE', 'turmas', OLD.id,
            CONCAT('Turma removida: ', OLD.nome));
END$$

-- ALUNOS
CREATE TRIGGER trg_alunos_insert AFTER INSERT ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'INSERT', 'alunos', NEW.id,
            CONCAT('Aluno criado: ', NEW.nome, ' (matrícula ', NEW.matricula, ')'));
END$$

CREATE TRIGGER trg_alunos_update AFTER UPDATE ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'UPDATE', 'alunos', NEW.id,
            CONCAT('Aluno atualizado: ', NEW.nome));
END$$

CREATE TRIGGER trg_alunos_delete AFTER DELETE ON alunos
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'DELETE', 'alunos', OLD.id,
            CONCAT('Aluno removido: ', OLD.nome));
END$$

-- NOTAS
CREATE TRIGGER trg_notas_insert AFTER INSERT ON notas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'INSERT', 'notas', NEW.id,
            CONCAT('Nota lançada: aluno_id=', NEW.aluno_id, ' disciplina=', NEW.disciplina, ' valor=', NEW.valor));
END$$

CREATE TRIGGER trg_notas_update AFTER UPDATE ON notas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'UPDATE', 'notas', NEW.id,
            CONCAT('Nota atualizada: aluno_id=', NEW.aluno_id, ' valor=', NEW.valor));
END$$

CREATE TRIGGER trg_notas_delete AFTER DELETE ON notas
FOR EACH ROW
BEGIN
    INSERT INTO logs (usuario_id, usuario_login, acao, tabela, registro_id, detalhes)
    VALUES (@app_user_id, @app_user_login, 'DELETE', 'notas', OLD.id,
            CONCAT('Nota removida: aluno_id=', OLD.aluno_id, ' valor=', OLD.valor));
END$$

DELIMITER ;

-- ============================================================
-- FIM DO SCRIPT
-- ============================================================
