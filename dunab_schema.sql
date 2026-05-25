-- =============================================
-- DUNAB - Esquema de Base de Datos MySQL
-- Gestión de Dinero UNAB
-- =============================================

CREATE DATABASE IF NOT EXISTS dunab_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE dunab_db;

-- Nota: Spring Boot con ddl-auto=update crea las tablas automáticamente.
-- Este script sirve para inicialización manual y referencia del esquema.

-- =============================================
-- TABLA: users
-- =============================================
CREATE TABLE IF NOT EXISTS users (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_code      VARCHAR(20)  UNIQUE NOT NULL COMMENT 'Código estudiantil UNAB',
    email             VARCHAR(150) UNIQUE NOT NULL,
    password          VARCHAR(255) NOT NULL COMMENT 'BCrypt hash',
    full_name         VARCHAR(100) NOT NULL,
    career            VARCHAR(100),
    semester          TINYINT CHECK (semester BETWEEN 1 AND 10),
    dunab_balance     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    profile_photo_url VARCHAR(500),
    role              ENUM('STUDENT','ADMIN') NOT NULL DEFAULT 'STUDENT',
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login        DATETIME,
    INDEX idx_dunab_balance (dunab_balance DESC),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- TABLA: transactions
-- =============================================
CREATE TABLE IF NOT EXISTS transactions (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    amount           DECIMAL(12,2) NOT NULL COMMENT 'Positivo=ingreso, Negativo=gasto',
    balance_after    DECIMAL(12,2) NOT NULL COMMENT 'Saldo resultante',
    category         ENUM('ALIMENTACION','TRANSPORTE','ACTIVIDADES','ENCUENTROS',
                          'MATERIALES','ENTRETENIMIENTO','SALUD','OTROS',
                          'BONO_INICIAL','TRANSFERENCIA') NOT NULL,
    type             ENUM('INGRESO','GASTO') NOT NULL,
    description      VARCHAR(255) NOT NULL,
    transaction_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    encounter_id     BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, transaction_date DESC),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- TABLA: encounters (encuentros universitarios)
-- =============================================
CREATE TABLE IF NOT EXISTS encounters (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(150) NOT NULL,
    description       TEXT,
    type              ENUM('DEPORTIVO','ACADEMICO','CULTURAL','SOCIAL','TECNOLOGICO') DEFAULT 'DEPORTIVO',
    location          VARCHAR(100),
    encounter_date    DATETIME NOT NULL,
    dunab_reward      DECIMAL(10,2) NOT NULL DEFAULT 100.00,
    max_participants  INT,
    status            ENUM('ACTIVO','CERRADO','CANCELADO','FINALIZADO') DEFAULT 'ACTIVO',
    created_at        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_date_status (encounter_date, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- TABLA: encounter_registrations
-- =============================================
CREATE TABLE IF NOT EXISTS encounter_registrations (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    encounter_id   BIGINT NOT NULL,
    registered_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dunab_collected BOOLEAN NOT NULL DEFAULT FALSE,
    collected_at   DATETIME,
    UNIQUE KEY unique_registration (user_id, encounter_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (encounter_id) REFERENCES encounters(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- TABLA: goals (metas de ahorro)
-- =============================================
CREATE TABLE IF NOT EXISTS goals (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT NOT NULL,
    name           VARCHAR(100) NOT NULL,
    description    VARCHAR(300),
    target_amount  DECIMAL(12,2) NOT NULL,
    current_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    target_date    DATE,
    status         ENUM('ACTIVA','COMPLETADA','CANCELADA') DEFAULT 'ACTIVA',
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at   DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- TABLA: achievements (logros e insignias)
-- =============================================
CREATE TABLE IF NOT EXISTS achievements (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    achievement_type ENUM('PRIMER_ENCUENTRO','MIL_DUNAB','CINCO_MIL_DUNAB',
                          'DIEZ_MIL_DUNAB','PRIMERA_META','CINCO_ENCUENTROS',
                          'PRIMER_MES','TOP_12','PRIMERA_TRANSACCION') NOT NULL,
    unlocked_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_achievement (user_id, achievement_type),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- DATOS INICIALES: Admin y encuentros de ejemplo
-- =============================================

-- Admin del sistema
INSERT IGNORE INTO users (student_code, email, password, full_name, career, semester, dunab_balance, role)
VALUES ('ADMIN001', 'admin@unab.edu.co',
        '$2a$12$LHlzLm/4Eo88qBj6aPSmX.oelSz8rEJ6fvLQ9/eDG7fU6aBaT5hUS',
        'Administrador DUNAB', 'Administración', 1, 10000.00, 'ADMIN');
-- Contraseña del admin: Admin2024$

-- Encuentros de ejemplo
INSERT IGNORE INTO encounters (title, description, type, location, encounter_date, dunab_reward, max_participants, created_by)
SELECT 'Torneo de Fútbol UNAB', 'Torneo interfacultades de fútbol sala. Gana DUNAB participando.', 
       'DEPORTIVO', 'Cancha principal UNAB', 
       DATE_ADD(NOW(), INTERVAL 7 DAY), 200.00, 50, id
FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT IGNORE INTO encounters (title, description, type, location, encounter_date, dunab_reward, max_participants, created_by)
SELECT 'Hackathon de IA', 'Competencia de desarrollo con Inteligencia Artificial. Equipos de 3 personas.', 
       'TECNOLOGICO', 'Laboratorio de Cómputo', 
       DATE_ADD(NOW(), INTERVAL 14 DAY), 500.00, 30, id
FROM users WHERE role = 'ADMIN' LIMIT 1;

INSERT IGNORE INTO encounters (title, description, type, location, encounter_date, dunab_reward, max_participants, created_by)
SELECT 'Festival Cultural UNAB', 'Festival de música, danza y arte. Presentaciones estudiantiles.', 
       'CULTURAL', 'Auditorio Principal', 
       DATE_ADD(NOW(), INTERVAL 21 DAY), 150.00, 200, id
FROM users WHERE role = 'ADMIN' LIMIT 1;

SELECT 'Esquema DUNAB creado exitosamente' as status;
