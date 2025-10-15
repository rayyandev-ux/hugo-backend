-- Script para crear usuario administrador de prueba
-- Ejecutar este script directamente en Supabase

INSERT INTO users (nombre, apellido, username, email, password, role, created_at, updated_at)
VALUES (
    'Test',
    'Admin', 
    'testadmin',
    'testadmin@sistema.com',
    '$2a$10$4ejBm0ekZKHanemiggJzNOU5cGUC77wZrReWF.wqUC45svNt41HW.',  -- test123
    'ADMIN',
    NOW(),
    NOW()
);

-- Verificar que el usuario fue creado
SELECT id, nombre, apellido, username, email, role, created_at 
FROM users 
WHERE username = 'testadmin';