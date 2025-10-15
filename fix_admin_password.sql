-- Script para corregir la contraseña del usuario admin
-- Contraseña: admin123 (encriptada con BCrypt)
-- Hash BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6

-- Actualizar la contraseña del usuario admin en la tabla users
UPDATE users 
SET password = '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd6dMlVBPbZpQ5I6',
    updated_at = NOW()
WHERE username = 'admin';

-- Verificar que la actualización se haya realizado correctamente
SELECT id, nombre, apellido, username, email, role, 
       CASE 
           WHEN password IS NULL OR password = '' THEN 'CONTRASEÑA VACÍA' 
           ELSE 'CONTRASEÑA CONFIGURADA' 
       END as password_status,
       created_at, updated_at
FROM users 
WHERE username = 'admin';