-- MySQL schema for prestamo-inso
-- Optional: not auto-executed; use manually if needed

-- Users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(255) NOT NULL,
  apellido VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  role ENUM('ADMIN','WORKER') NOT NULL,
  reset_password_token VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Trabajador
CREATE TABLE IF NOT EXISTS trabajador (
  id BIGINT PRIMARY KEY,
  needs_password_change TINYINT(1) DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_trabajador_user FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cliente
CREATE TABLE IF NOT EXISTS cliente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  nombre VARCHAR(255),
  apellido_paterno VARCHAR(255),
  apellido_materno VARCHAR(255),
  nro_documento VARCHAR(20) UNIQUE,
  tipo_persona VARCHAR(50),
  nacionalidad VARCHAR(100),
  estado VARCHAR(50),
  condicion VARCHAR(50),
  direccion TEXT,
  distrito VARCHAR(100),
  provincia VARCHAR(100),
  departamento VARCHAR(100),
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Prestamo
CREATE TABLE IF NOT EXISTS prestamo (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  cliente_id BIGINT NOT NULL,
  nro_documento VARCHAR(20),
  monto DECIMAL(15,2) NOT NULL,
  interes DECIMAL(5,2) NOT NULL,
  plazo INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  estado VARCHAR(50) DEFAULT 'Pendiente',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_prestamo_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cronograma de pagos
CREATE TABLE IF NOT EXISTS cronograma_pagos (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  prestamo_id BIGINT NOT NULL,
  estado VARCHAR(50) DEFAULT 'Pendiente',
  fecha_pago DATE NOT NULL,
  monto_cuota DECIMAL(15,2) NOT NULL,
  pago_intereses DECIMAL(15,2) NOT NULL,
  amortizacion DECIMAL(15,2) NOT NULL,
  saldo_restante DECIMAL(15,2) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_cronograma_prestamo FOREIGN KEY (prestamo_id) REFERENCES prestamo(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes
CREATE INDEX idx_cliente_nro_documento ON cliente(nro_documento);
CREATE INDEX idx_prestamo_cliente_id ON prestamo(cliente_id);
CREATE INDEX idx_prestamo_estado ON prestamo(estado);
CREATE INDEX idx_cronograma_estado ON cronograma_pagos(estado);
CREATE INDEX idx_cronograma_fecha_pago ON cronograma_pagos(fecha_pago);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Seed admin user (ignored on duplicate username)
INSERT IGNORE INTO users (nombre, apellido, username, email, password, role)
VALUES ('Admin', 'Sistema', 'admin', 'admin@prestamos.com', '$2a$10$encrypted_password_here', 'ADMIN');