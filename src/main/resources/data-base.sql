-- Script SQL para crear las tablas necesarias en Supabase
-- Basado en las entidades del proyecto de préstamos

-- Crear tabla users (tabla padre para herencia)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'WORKER')),
    reset_password_token VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla trabajador (hereda de users)
CREATE TABLE IF NOT EXISTS trabajador (
    id BIGINT PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    needs_password_change BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla cliente
CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL PRIMARY KEY,
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla prestamo
CREATE TABLE IF NOT EXISTS prestamo (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL REFERENCES cliente(id) ON DELETE CASCADE,
    nro_documento VARCHAR(20),
    monto DECIMAL(15,2) NOT NULL,
    interes DECIMAL(5,2) NOT NULL,
    plazo INTEGER NOT NULL, -- En meses
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) DEFAULT 'Pendiente',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla cronograma_pagos
CREATE TABLE IF NOT EXISTS cronograma_pagos (
    id BIGSERIAL PRIMARY KEY,
    prestamo_id BIGINT NOT NULL REFERENCES prestamo(id) ON DELETE CASCADE,
    estado VARCHAR(50) DEFAULT 'Pendiente',
    fecha_pago DATE NOT NULL,
    monto_cuota DECIMAL(15,2) NOT NULL,
    pago_intereses DECIMAL(15,2) NOT NULL,
    amortizacion DECIMAL(15,2) NOT NULL,
    saldo_restante DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_cliente_nro_documento ON cliente(nro_documento);
CREATE INDEX IF NOT EXISTS idx_prestamo_cliente_id ON prestamo(cliente_id);
CREATE INDEX IF NOT EXISTS idx_prestamo_estado ON prestamo(estado);
CREATE INDEX IF NOT EXISTS idx_cronograma_prestamo_id ON cronograma_pagos(prestamo_id);
CREATE INDEX IF NOT EXISTS idx_cronograma_estado ON cronograma_pagos(estado);
CREATE INDEX IF NOT EXISTS idx_cronograma_fecha_pago ON cronograma_pagos(fecha_pago);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Insertar datos de ejemplo (opcional)
-- Usuario administrador por defecto
INSERT INTO users (nombre, apellido, username, email, password, role) 
VALUES ('Admin', 'Sistema', 'admin', 'admin@prestamos.com', '$2a$10$encrypted_password_here', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Comentarios sobre las tablas
COMMENT ON TABLE users IS 'Tabla principal de usuarios del sistema (solo ADMIN y WORKER)';
COMMENT ON TABLE trabajador IS 'Tabla de trabajadores que extiende users';
COMMENT ON TABLE cliente IS 'Tabla de clientes que solicitan préstamos';
COMMENT ON TABLE prestamo IS 'Tabla de préstamos otorgados';
COMMENT ON TABLE cronograma_pagos IS 'Tabla del cronograma de pagos de cada préstamo';

-- Comentarios sobre columnas importantes
COMMENT ON COLUMN cliente.nro_documento IS 'Número de documento (DNI o RUC)';
COMMENT ON COLUMN prestamo.plazo IS 'Plazo del préstamo en meses';
COMMENT ON COLUMN prestamo.interes IS 'Tasa de interés del préstamo';
COMMENT ON COLUMN cronograma_pagos.pago_intereses IS 'Monto correspondiente a intereses en la cuota';
COMMENT ON COLUMN cronograma_pagos.amortizacion IS 'Monto correspondiente a amortización del capital';
COMMENT ON COLUMN cronograma_pagos.saldo_restante IS 'Saldo pendiente después del pago';