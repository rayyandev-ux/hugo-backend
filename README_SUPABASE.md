# Integración con Supabase - Proyecto de Préstamos
## Configuración de Base de Datos

### Conexión a Supabase
El proyecto está configurado para conectarse a Supabase PostgreSQL con las siguientes configuraciones en `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres
spring.datasource.username=postgres.qjqjqjqjqjqjqjqjqj
spring.datasource.password=tu_password_aqui
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data-base.sql
```

## Estructura de Base de Datos

### Tabla: users
Tabla principal para usuarios del sistema (solo administradores y trabajadores).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | Clave primaria |
| nombre | VARCHAR(255) | Nombre del usuario |
| apellido | VARCHAR(255) | Apellido del usuario |
| username | VARCHAR(255) | Nombre de usuario único |
| email | VARCHAR(255) | Email único |
| password | VARCHAR(255) | Contraseña encriptada |
| role | VARCHAR(50) | Rol del usuario (ADMIN, WORKER) |
| reset_password_token | VARCHAR(255) | Token para reseteo de contraseña |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Fecha de actualización |

### Tabla: trabajador
Extiende la tabla users para trabajadores específicos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGINT | Clave foránea a users(id) |
| needs_password_change | BOOLEAN | Indica si necesita cambiar contraseña |
| created_at | TIMESTAMP | Fecha de creación |
| updated_at | TIMESTAMP | Fecha de actualización |

### Tabla: cliente
Información de clientes que solicitan préstamos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | Clave primaria |
| create_at | TIMESTAMP | Fecha de creación |
| nombre | VARCHAR(255) | Nombre del cliente |
| apellido_paterno | VARCHAR(255) | Apellido paterno |
| apellido_materno | VARCHAR(255) | Apellido materno |
| nro_documento | VARCHAR(20) | Número de documento único |
| tipo_persona | VARCHAR(50) | Tipo de persona |
| nacionalidad | VARCHAR(100) | Nacionalidad |
| estado | VARCHAR(50) | Estado del cliente |
| condicion | VARCHAR(50) | Condición del cliente |
| direccion | TEXT | Dirección |
| distrito | VARCHAR(100) | Distrito |
| provincia | VARCHAR(100) | Provincia |
| departamento | VARCHAR(100) | Departamento |

### Tabla: prestamo
Información de préstamos otorgados.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | Clave primaria |
| cliente_id | BIGINT | Referencia a cliente(id) |
| nro_documento | VARCHAR(20) | Número de documento |
| monto | DECIMAL(15,2) | Monto del préstamo |
| interes | DECIMAL(5,2) | Tasa de interés |
| plazo | INTEGER | Plazo en meses |
| fecha_creacion | TIMESTAMP | Fecha de creación |
| estado | VARCHAR(50) | Estado del préstamo |

### Tabla: cronograma_pagos
Cronograma de pagos de cada préstamo.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | BIGSERIAL | Clave primaria |
| prestamo_id | BIGINT | Referencia a prestamo(id) |
| estado | VARCHAR(50) | Estado del pago |
| fecha_pago | DATE | Fecha de pago |
| monto_cuota | DECIMAL(15,2) | Monto de la cuota |
| pago_intereses | DECIMAL(15,2) | Pago de intereses |
| amortizacion | DECIMAL(15,2) | Amortización del capital |
| saldo_restante | DECIMAL(15,2) | Saldo restante |

## Configuración Spring Boot

### application.properties
```properties
# Configuración de base de datos Supabase
spring.datasource.url=jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres
spring.datasource.username=postgres.qjqjqjqjqjqjqjqjqj
spring.datasource.password=tu_password_aqui
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Inicialización de datos
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:data-base.sql

# Configuración de email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_password_email
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Script de Inicialización (data-base.sql)
El archivo `src/main/resources/data-base.sql` contiene todas las sentencias SQL para crear las tablas necesarias en Supabase.

## Características Implementadas

### 1. Herencia de Tablas
- La tabla `trabajador` hereda de `users` usando claves foráneas
- Permite mantener información común en `users` y específica en `trabajador`

### 2. Relaciones
- `prestamo` → `cliente` (Many-to-One)
- `cronograma_pagos` → `prestamo` (Many-to-One)
- `trabajador` → `users` (One-to-One)

### 3. Campos Únicos
- `users.username`
- `users.email`
- `cliente.nro_documento`

### 4. Validaciones
- Roles válidos: **ADMIN, WORKER** (se eliminó USER)
- Campos obligatorios definidos con NOT NULL

### 5. Índices de Rendimiento
- Índices en campos de búsqueda frecuente
- Índices en claves foráneas
- Índices en campos de filtrado

### 6. Timestamps Automáticos
- `created_at` y `updated_at` con valores por defecto
- Actualización automática de timestamps

## Sistema de Roles Simplificado

El sistema ahora maneja únicamente dos tipos de usuarios:

- **ADMIN**: Administradores del sistema con acceso completo
- **WORKER**: Trabajadores/empleados con permisos específicos

**Nota importante**: Se eliminó el rol USER. Los clientes no son usuarios del sistema, son entidades separadas que se gestionan a través de la tabla `cliente`.

## Integración con API Externa - Decolecta

### Configuración de API
El proyecto utiliza la API de Decolecta para validación de documentos de identidad:

- **Documentación**: https://decolecta.gitbook.io/docs/
- **Base URL**: https://api.decolecta.com
- **Autenticación**: Bearer Token

### Endpoints Utilizados

#### 1. Validación DNI (RENIEC)
- **URL**: `https://api.decolecta.com/v1/reniec/dni?numero={dni}`
- **Método**: GET
- **Headers**: 
  - `Authorization: Bearer sk_10864.8Ab96PjsRm4fErm3r8XCe06VYWlTrTSx`
  - `Accept: application/json`

**Respuesta de ejemplo**:
```json
{
  "first_name": "ERACLEO JUAN",
  "first_last_name": "HUAMANI", 
  "second_last_name": "MENDOZA",
  "full_name": "HUAMANI MENDOZA ERACLEO JUAN",
  "document_number": "46027897"
}
```

#### 2. Validación RUC (SUNAT)
- **URL**: `https://api.decolecta.com/v1/sunat/ruc?numero={ruc}`
- **Método**: GET
- **Headers**: 
  - `Authorization: Bearer sk_10864.8Ab96PjsRm4fErm3r8XCe06VYWlTrTSx`
  - `Accept: application/json`

**Respuesta de ejemplo**:
```json
{
  "razon_social": "HUAMANI MENDOZA ERACLEO JUAN",
  "numero_documento": "10460278975",
  "estado": "ACTIVO",
  "condicion": "HABIDO",
  "direccion": "-",
  "distrito": "",
  "provincia": "",
  "departamento": "",
  "es_agente_retencion": false,
  "es_buen_contribuyente": false
}
```

### Implementación en el Código

La integración se encuentra en `ClienteService.java`:

- **Método `validarDNI(String dni)`**: Consulta la API de RENIEC para obtener datos personales
- **Método `validarRUC(String ruc)`**: Consulta la API de SUNAT para obtener datos empresariales
- **Método `registrarCliente()`**: Utiliza las APIs para validar y obtener datos automáticamente

### Configuración del Token

El token de autenticación está configurado directamente en el servicio. Para cambiar el token:

1. Actualizar la variable `TOKEN` en `ClienteService.java`
2. Reiniciar la aplicación

### Ventajas de Decolecta API

- **Datos actualizados**: Información directa de RENIEC y SUNAT
- **Validación automática**: Verificación de documentos en tiempo real
- **Integración simple**: API REST con respuestas JSON
- **Confiabilidad**: Servicio especializado en consultas de documentos peruanos

## Próximos Pasos

1. **Ejecutar la aplicación**: `mvn spring-boot:run`
2. **Verificar creación de tablas** en Supabase Dashboard
3. **Probar funcionalidades** de registro y autenticación
4. **Probar validación de DNI/RUC** con la nueva API de Decolecta
5. **Configurar datos de prueba** si es necesario

## Notas Importantes

- Asegúrate de reemplazar `tu_password_aqui` con la contraseña real de Supabase
- El modo `spring.sql.init.mode=always` ejecutará el script en cada inicio
- Las contraseñas se almacenan encriptadas usando BCrypt
- Los tokens de reseteo de contraseña tienen expiración automática
- **Los clientes no tienen roles de sistema**: son entidades independientes sin acceso al sistema

## Script de Inicialización

El archivo `data-base.sql` contiene:
- Creación de todas las tablas necesarias
- Índices para optimizar consultas
- Comentarios explicativos
- Datos de ejemplo (usuario administrador)

## Características Importantes

1. **Herencia de Tablas**: La tabla `trabajador` hereda de `users` usando estrategia JOINED.

2. **Relaciones**:
   - Cliente → Préstamo (1:N)
   - Préstamo → Cronograma de Pagos (1:N)

3. **Índices Optimizados**:
   - Búsqueda por número de documento
   - Filtrado por estados
   - Consultas por fechas

4. **Validaciones**:
   - Roles válidos mediante CHECK constraints
   - Campos únicos para documentos y credenciales

## Ejecución

Al iniciar la aplicación Spring Boot:
1. Se conectará automáticamente a Supabase
2. Ejecutará el script `data-base.sql` si es necesario
3. Hibernate actualizará la estructura si hay cambios en las entidades

## Seguridad

- Las contraseñas se almacenan encriptadas
- Conexión segura a través de SSL
- Validaciones a nivel de base de datos y aplicación