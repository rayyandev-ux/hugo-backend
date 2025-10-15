-- Script para crear un usuario administrador
-- Contraseña: admin123 (encriptada con BCrypt)

-- Primero insertar en la tabla users
INSERT INTO users (nombre, apellido, username, email, password, role, created_at, updated_at)
VALUES 
    ('Admin', 'Sistema', 'admin', 'admin@sistema.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6', 'ADMIN', NOW(), NOW())
ON CONFLICT (username) DO UPDATE SET
    password = EXCLUDED.password,
    email = EXCLUDED.email,
    updated_at = NOW();

-- Luego insertar en la tabla trabajador usando el ID del usuario creado
INSERT INTO trabajador (id, needs_password_change, created_at, updated_at)
SELECT u.id, false, NOW(), NOW()
FROM users u 
WHERE u.username = 'admin'
ON CONFLICT (id) DO UPDATE SET
    needs_password_change = EXCLUDED.needs_password_change,
    updated_at = NOW();

-- Verificar que el usuario se haya creado correctamente
SELECT u.id, u.nombre, u.apellido, u.username, u.email, u.role, t.needs_password_change, u.created_at 
FROM users u
JOIN trabajador t ON u.id = t.id
WHERE u.username = 'admin';