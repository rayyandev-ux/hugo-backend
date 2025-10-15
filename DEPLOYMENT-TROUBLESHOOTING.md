# Deployment Troubleshooting Guide - Render

Esta guía te ayudará a resolver problemas comunes durante el deployment en Render.

## Error: Unable to determine Dialect without JDBC metadata

### Descripción del Error
```
Failed to initialize JPA EntityManagerFactory: Unable to create requested service [org.hibernate.engine.jdbc.env.spi.JdbcEnvironment] due to: Unable to determine Dialect without JDBC metadata (please set 'jakarta.persistence.jdbc.url' for common cases or 'hibernate.dialect' when a custom Dialect implementation must be provided)
```

### Causas Posibles
1. **Variables de entorno no configuradas** en Render
2. **Configuración de perfil de Spring** no activada correctamente
3. **Configuración de Hibernate** faltante para producción

### Soluciones Implementadas

#### 1. Configuración de Variables de Entorno en Render
Asegúrate de que las siguientes variables estén configuradas en tu dashboard de Render:

**Variables Requeridas:**
- `SUPABASE_DATABASE_URL`: `jdbc:postgresql://db.dtalvtuvoquygqzkpgqa.supabase.co:5432/postgres?sslmode=require`
- `SUPABASE_DATABASE_USERNAME`: `postgres`
- `SUPABASE_DATABASE_PASSWORD`: [Tu contraseña de Supabase]
- `SPRING_PROFILES_ACTIVE`: `production`

**Variables Opcionales:**
- `SUPABASE_URL`: `https://dtalvtuvoquygqzkpgqa.supabase.co`
- `SUPABASE_ANON_KEY`: [Tu clave anónima]
- `SUPABASE_SERVICE_ROLE_KEY`: [Tu clave de servicio]
- `SPRING_MAIL_USERNAME`: [Tu email para SMTP]
- `SPRING_MAIL_PASSWORD`: [Tu contraseña de email]
- `CORS_ALLOWED_ORIGINS`: [URLs permitidas para CORS]

#### 2. Configuración de application-production.properties
El archivo ya incluye la configuración necesaria:

```properties
# Configuración de base de datos Supabase
spring.datasource.url=${SUPABASE_DATABASE_URL}
spring.datasource.username=${SUPABASE_DATABASE_USERNAME}
spring.datasource.password=${SUPABASE_DATABASE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuración de JPA/Hibernate para producción
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

# Configuración adicional para conexión de base de datos
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1
```

#### 3. Configuración de render.yaml
El archivo `render.yaml` debe incluir:

```yaml
services:
  - type: web
    name: prestamo-inso-backend
    env: java
    buildCommand: ./mvnw clean package -DskipTests
    startCommand: SPRING_PROFILES_ACTIVE=production java -Dserver.port=$PORT -jar target/prestamo-inso-0.0.1-SNAPSHOT.jar
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: production
      - key: SUPABASE_DATABASE_URL
        value: jdbc:postgresql://db.dtalvtuvoquygqzkpgqa.supabase.co:5432/postgres?sslmode=require
      - key: SUPABASE_DATABASE_USERNAME
        value: postgres
      - key: SUPABASE_DATABASE_PASSWORD
        sync: false  # Configurar manualmente en Render
```

#### 4. Configuración de Dockerfile
El Dockerfile incluye la activación del perfil de producción:

```dockerfile
CMD ["java", "-jar", "-Dspring.profiles.active=production", "target/prestamo-inso-0.0.1-SNAPSHOT.jar"]
```

## Pasos para Resolver el Error

### 1. Verificar Variables de Entorno en Render
1. Ve a tu dashboard de Render
2. Selecciona tu servicio
3. Ve a "Environment"
4. Verifica que todas las variables estén configuradas correctamente
5. **Importante**: Asegúrate de que `SUPABASE_DATABASE_PASSWORD` tenga el valor correcto

### 2. Verificar Conexión a Supabase
1. Ve a tu proyecto en Supabase
2. Ve a "Settings" > "Database"
3. Copia la cadena de conexión correcta
4. Asegúrate de que coincida con `SUPABASE_DATABASE_URL`

### 3. Redeploy del Servicio
Después de configurar las variables:
1. Ve a "Deploys" en tu dashboard de Render
2. Haz clic en "Manual Deploy" > "Deploy latest commit"
3. Monitorea los logs durante el deployment

### 4. Verificar Logs de Deployment
Busca en los logs:
- ✅ `Started PrestamoInsoApplication` - Aplicación iniciada correctamente
- ✅ `Tomcat started on port` - Servidor web iniciado
- ❌ `BeanCreationException` - Error de configuración
- ❌ `Unable to determine Dialect` - Error de base de datos

## Otros Problemas Comunes

### Error de CORS
**Síntoma**: Frontend no puede conectarse al backend
**Solución**: Configurar `CORS_ALLOWED_ORIGINS` con la URL de tu frontend

### Error de SSL/TLS
**Síntoma**: Error de conexión SSL a la base de datos
**Solución**: Asegúrate de que la URL incluya `?sslmode=require`

### Error de Timeout
**Síntoma**: La aplicación se inicia pero no responde
**Solución**: Verificar configuración de Hikari connection pool

### Error de Memoria
**Síntoma**: OutOfMemoryError durante el build
**Solución**: Usar `-DskipTests` en el buildCommand

## Comandos Útiles para Debugging

### Verificar Variables de Entorno Localmente
```bash
# Activar perfil de producción localmente
export SPRING_PROFILES_ACTIVE=production
export SUPABASE_DATABASE_URL="jdbc:postgresql://..."
export SUPABASE_DATABASE_USERNAME="postgres"
export SUPABASE_DATABASE_PASSWORD="tu_password"

# Ejecutar aplicación
./mvnw spring-boot:run
```

### Verificar Conexión a Base de Datos
```bash
# Probar conexión con psql
psql "postgresql://postgres:password@db.dtalvtuvoquygqzkpgqa.supabase.co:5432/postgres?sslmode=require"
```

## Checklist de Deployment

- [ ] Variables de entorno configuradas en Render
- [ ] `SUPABASE_DATABASE_PASSWORD` configurada correctamente
- [ ] `render.yaml` actualizado con configuración correcta
- [ ] `application-production.properties` incluye configuración de Hibernate
- [ ] Dockerfile incluye activación de perfil de producción
- [ ] Build exitoso sin errores de compilación
- [ ] Logs muestran inicio exitoso de la aplicación
- [ ] Endpoint de health check responde correctamente

## Contacto y Soporte

Si el problema persiste después de seguir esta guía:
1. Revisa los logs completos de deployment en Render
2. Verifica la configuración de Supabase
3. Asegúrate de que todas las dependencias estén actualizadas
4. Considera usar el perfil de desarrollo localmente para debugging