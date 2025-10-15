-- Script para crear un usuario administrador
-- Contraseña: admin123 (encriptada con BCrypt)

INSERT INTO trabajador (nombre, apellido, username, password, email, role, needs_password_change, created_at, updated_at)
VALUES 
    ('Admin', 'Sistema', 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6', 'admin@sistema.com', 0, false, NOW(), NOW())
ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    updated_at = NOW();

-- Verificar que el usuario se haya creado
SELECT id, nombre, apellido, username, email, role, needs_password_change, created_at 
FROM trabajador 
WHERE username = 'admin';