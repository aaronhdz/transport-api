# Transport API

REST API desarrollada con Java y Spring Boot para la gestión de órdenes de transporte, conductores, asignaciones y archivos asociados.

El proyecto fue desarrollado como parte de una evaluación técnica Backend y aplica una arquitectura modular, persistencia relacional, autenticación JWT, validaciones, manejo centralizado de excepciones, documentación OpenAPI/Swagger, pruebas unitarias y ejecución mediante Docker.

## Tecnologías

- Java 17
- Spring Boot 3.5.16
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL 17
- Flyway
- Lombok
- Springdoc OpenAPI / Swagger UI
- JUnit 5
- Mockito
- Docker
- Docker Compose

## Arquitectura

El proyecto utiliza una estructura modular por funcionalidad:

```text
com.transport.api
├── assignment
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── common
│   └── exception
├── driver
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── order
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
└── security
```

Esta organización permite mantener separadas las responsabilidades de cada dominio y facilita el mantenimiento y crecimiento de la aplicación.

## Funcionalidades

La API permite:

- Crear órdenes de transporte.
- Consultar una orden por UUID.
- Consultar órdenes mediante filtros y paginación.
- Cambiar el estado de una orden validando las transiciones permitidas.
- Crear conductores.
- Consultar conductores activos.
- Asignar un conductor activo a una orden en estado `CREATED`.
- Adjuntar un documento PDF a una asignación.
- Adjuntar imágenes PNG o JPG a una asignación.
- Autenticarse mediante JWT.
- Consultar y probar los servicios mediante Swagger UI.

## Estados de una orden

Los estados disponibles son:

```text
CREATED
IN_TRANSIT
DELIVERED
CANCELLED
```

Las transiciones válidas implementadas son:

```text
CREATED ──────> IN_TRANSIT ──────> DELIVERED
   │
   └──────────> CANCELLED
```

`DELIVERED` y `CANCELLED` son estados finales.

## Requisitos para ejecución

Para ejecutar el proyecto mediante Docker se requiere:

- Docker
- Docker Compose

Para ejecutar y compilar el proyecto directamente también puede utilizarse:

- Java 17
- Maven Wrapper incluido en el repositorio

## Configuración de variables de entorno

El repositorio incluye el archivo:

```text
.env.example
```

con valores preparados exclusivamente para desarrollo y evaluación técnica.

Crear el archivo `.env` a partir de la plantilla.

### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

### Linux / macOS

```bash
cp .env.example .env
```

El archivo `.env` está excluido del repositorio mediante `.gitignore`.

> Los valores incluidos en `.env.example` son únicamente credenciales de demostración y no deben utilizarse en ambientes productivos.

## Ejecución con Docker

### 1. Construir la imagen de la aplicación

Desde la raíz del proyecto:

```bash
docker build -t transport-api:1.0.0 .
```

### 2. Levantar la aplicación y PostgreSQL

```bash
docker compose up -d
```

### 3. Verificar los contenedores

```bash
docker compose ps
```

Los servicios esperados son:

```text
transport-api
transport-postgres
```

PostgreSQL cuenta con un `healthcheck` y la aplicación inicia después de que la base de datos se encuentra disponible.

## Puertos

| Servicio | Puerto |
|---|---:|
| Transport API | `8080` |
| PostgreSQL (host) | `5433` |

Dentro de la red de Docker, la API se conecta a PostgreSQL mediante el puerto estándar `5432`.

## Swagger / OpenAPI

Una vez iniciada la aplicación, la documentación interactiva está disponible en:

```text
http://localhost:8080/swagger-ui/index.html
```

La especificación OpenAPI está disponible en:

```text
http://localhost:8080/v3/api-docs
```

## Autenticación

Los endpoints protegidos utilizan autenticación JWT mediante:

```text
Authorization: Bearer <token>
```

Para obtener un token utilizar:

```text
POST /api/v1/auth/login
```

Credenciales de demostración:

```text
Username: transport-admin
Password: Transport2026!
```

Ejemplo de solicitud:

```json
{
  "username": "transport-admin",
  "password": "Transport2026!"
}
```

La respuesta contiene un token JWT de tipo `Bearer`.

En Swagger UI también puede utilizarse el botón **Authorize** para registrar el token y consumir los endpoints protegidos.

## Endpoints principales

### Autenticación

```text
POST /api/v1/auth/login
```

### Órdenes

```text
POST  /api/v1/orders
GET   /api/v1/orders/{id}
GET   /api/v1/orders
PATCH /api/v1/orders/{id}/status
```

La consulta de órdenes permite utilizar filtros por:

- Estado
- Fecha
- Origen
- Destino

y soporta paginación y ordenamiento.

### Conductores

```text
POST /api/v1/drivers
GET  /api/v1/drivers/active
```

### Asignaciones

```text
POST /api/v1/orders/{orderId}/assignments
```

La asignación únicamente es permitida cuando:

- La orden existe.
- La orden se encuentra en estado `CREATED`.
- El conductor existe.
- El conductor está activo.
- La orden no cuenta con una asignación previa.

### Archivos

```text
POST /api/v1/orders/{orderId}/attachments/document
POST /api/v1/orders/{orderId}/attachments/image
```

Formatos soportados:

```text
Documento: PDF
Imagen:    PNG / JPG
```

El tamaño máximo configurado por archivo es de **10 MB**.

Además del tipo MIME, el servicio valida el contenido básico del archivo para evitar aceptar archivos cuya extensión o `Content-Type` no corresponda con su contenido.

Los archivos son almacenados fuera de la base de datos y la ruta correspondiente queda asociada a la asignación.

## Persistencia

La aplicación utiliza PostgreSQL como base de datos relacional.

La creación y evolución del esquema se administra mediante **Flyway**.

La migración inicial se encuentra en:

```text
src/main/resources/db/migration/V1__create_initial_schema.sql
```

Las principales tablas son:

```text
orders
drivers
order_assignments
```

## Manejo de errores

La aplicación utiliza manejo centralizado de excepciones mediante `@ControllerAdvice`.

Las respuestas de error incluyen información estructurada como:

```text
timestamp
status
error
message
path
validationErrors
```

Se contemplan, entre otros:

- Recursos inexistentes.
- Errores de validación.
- Transiciones de estado inválidas.
- Licencias de conductor duplicadas.
- Conductores inactivos.
- Órdenes que no pueden ser asignadas.
- Asignaciones duplicadas.
- Archivos inválidos.
- Archivos que exceden el tamaño permitido.
- JSON mal formado o valores inválidos.
- Credenciales incorrectas.

## Logging

Los principales eventos de negocio y errores se registran mediante logging estructurado.

Ejemplos de eventos:

```text
order_created
order_status_changed
driver_created
driver_assigned
document_attached
image_attached
authentication_success
authentication_failed
file_stored
file_rejected
```

No se registran contraseñas, tokens JWT ni otros datos sensibles de autenticación.

## Pruebas

El proyecto incluye pruebas unitarias desarrolladas con **JUnit 5** y **Mockito**.

Las suites principales son:

```text
AuthServiceTest
OrderServiceTest
DriverServiceTest
AssignmentServiceTest
FileStorageServiceTest
```

Ejecutar todas las pruebas:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

Resultado de referencia:

```text
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Las pruebas unitarias no requieren una instancia externa de PostgreSQL.

## Compilación

### Windows

```powershell
.\mvnw.cmd clean package
```

### Linux / macOS

```bash
./mvnw clean package
```

## Detener los contenedores

```bash
docker compose down
```

Para eliminar también los volúmenes persistentes:

```bash
docker compose down -v
```

> El uso de `-v` elimina los datos almacenados en los volúmenes de PostgreSQL y de archivos de la aplicación.

## Consideraciones de seguridad

- Los endpoints de negocio se encuentran protegidos mediante JWT.
- Las contraseñas se validan utilizando BCrypt.
- La aplicación utiliza sesiones `STATELESS`.
- Los secretos y credenciales pueden suministrarse mediante variables de entorno.
- `.env` está excluido del control de versiones.
- Las credenciales incluidas en `.env.example` son exclusivamente para demostración y evaluación local.
- La carga de archivos valida tipo, contenido y tamaño.

## Decisiones técnicas

- **UUID** para los identificadores de las entidades.
- **PostgreSQL** como base de datos relacional.
- **Flyway** para versionamiento del esquema.
- **DTOs** para separar el contrato REST de las entidades de persistencia.
- **Mapeo manual** entre entidades y DTOs.
- **Specifications** de Spring Data JPA para los filtros dinámicos de órdenes.
- **JWT** para autenticación stateless.
- **BCrypt** para validación segura de credenciales.
- **Docker Compose** para proporcionar un entorno reproducible de API + PostgreSQL.
- **Almacenamiento mediante volumen** para documentos e imágenes.
- **JUnit 5 y Mockito** para pruebas unitarias aisladas de infraestructura.