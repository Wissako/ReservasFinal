# 🏫 Sistema de Reservas de Aulas

Sistema completo de gestión de reservas de aulas para centros educativos, desarrollado con Spring Boot 3.5.7 y seguridad JWT.

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Tecnologías](#️-tecnologías-utilizadas)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Arquitectura](#-arquitectura)
- [Endpoints API](#-endpoints-api-completos)
- [DTOs](#-dtos-data-transfer-objects)
- [Entidades](#-entidades-del-modelo)
- [Seguridad](#-seguridad-y-autenticación)
- [Validaciones](#-validaciones-de-negocio)
- [Excepciones](#-manejo-de-excepciones)

## 📖 Descripción

Aplicación REST API para la gestión integral de reservas de aulas en centros educativos. Permite a administradores y profesores gestionar espacios, horarios y reservas con control de capacidad, disponibilidad y conflictos.

### Funcionalidades Principales

- ✅ Autenticación y autorización con JWT
- ✅ Gestión de usuarios (Admin/Profesor)
- ✅ CRUD completo de aulas con soporte para aulas de ordenadores
- ✅ Gestión de horarios por días y sesiones
- ✅ Sistema de reservas con validación de conflictos
- ✅ Control de capacidad y asistentes
- ✅ Prevención automática de solapamientos

## 🛠️ Tecnologías Utilizadas

### Backend
- **Java:** 17
- **Spring Boot:** 3.5.7
- **Spring Security:** Autenticación JWT
- **Spring Data JPA:** Persistencia de datos
- **MySQL:** Base de datos relacional
- **Lombok:** Reducción de código boilerplate
- **Bean Validation:** Validaciones declarativas
- **JJWT:** 0.12.6 - Generación y validación de tokens JWT
- **BCrypt:** Cifrado de contraseñas

### Dependencias Maven Principales
```xml
- spring-boot-starter-data-jpa
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-oauth2-resource-server
- spring-boot-starter-validation
- mysql-connector-j
- lombok
- jjwt-api, jjwt-impl, jjwt-jackson
```

## 🚀 Instalación

### Requisitos Previos
- Java JDK 17+
- Maven 3.9+
- MySQL 8.0+
- Git

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <url-repositorio>
cd ReservasNuevoPrueba
```

2. **Crear base de datos MySQL**
```sql
CREATE DATABASE reservas_aulas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'reservas_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON reservas_aulas.* TO 'reservas_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configurar application.properties**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reservas_aulas
spring.datasource.username=reservas_user
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
```

4. **Compilar y ejecutar**
```bash
# Linux/Mac
./mvnw clean install
./mvnw spring-boot:run

# Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

5. **Verificar instalación**
```bash
curl http://localhost:8080/auth/login
```

## ⚙️ Configuración

### application.properties completo
```properties
# Nombre de la aplicación
spring.application.name=ReservasNuevoPrueba

# Configuración de Base de Datos MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/reservas_aulas?useSSL=false&serverTimezone=UTC
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# Puerto del servidor
server.port=8080

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.com.luis.reservasnuevoprueba=DEBUG
```

### Configuración CORS
La aplicación está configurada para aceptar peticiones desde múltiples orígenes (desarrollo):
- `http://localhost:3000` - React
- `http://localhost:4200` - Angular
- `http://localhost:5173` - Vite
- `http://127.0.0.1:5500` - Live Server

## 🏗 Arquitectura

### Estructura del Proyecto
```
src/main/java/com/luis/reservasnuevoprueba/
├── config/
│   ├── CorsConfig.java              # Configuración CORS
│   └── SecurityConfig.java          # Configuración Spring Security
├── controllers/
│   ├── AuthController.java          # Autenticación (login/register)
│   ├── UsuarioController.java       # Gestión de usuarios
│   ├── AulaController.java          # CRUD de aulas
│   ├── HorarioController.java       # CRUD de horarios
│   └── ReservaController.java       # CRUD de reservas
├── DTO/
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   ├── AulaDTO.java
│   ├── AulaRequest.java
│   ├── HorarioDTO.java
│   ├── HorarioRequest.java
│   ├── ReservaDTO.java
│   ├── ReservaRequest.java
│   ├── UsuarioDTO.java
│   ├── UsuarioRequest.java
│   ├── UsuarioUpdateRequest.java
│   ├── CambiarPasswordRequest.java
│   └── ErrorResponse.java
├── entities/
│   ├── Usuario.java                 # Entidad Usuario (UserDetails)
│   ├── Aula.java                    # Entidad Aula
│   ├── Horario.java                 # Entidad Horario
│   └── Reserva.java                 # Entidad Reserva
├── repository/
│   ├── UsuarioRepository.java
│   ├── AulaRepository.java
│   ├── HorarioRepository.java
│   └── ReservaRepository.java
├── service/
│   ├── JwtService.java              # Generación y validación JWT
│   ├── CustomUserDetailsService.java
│   ├── AulaService.java
│   └── ReservaService.java
├── exception/
│   ├── GlobalException.java         # Manejador global de excepciones
│   ├── AulaException.java
│   └── ReservaException.java
└── ReservasNuevoPruebaApplication.java
```

## 🔌 Endpoints API Completos

### 🔐 Autenticación (`/auth`)

#### POST `/auth/login`
**Descripción:** Autenticar usuario y obtener token JWT  
**Acceso:** Público (sin autenticación)  
**Request Body:**
```json
{
  "email": "usuario@email.com",
  "password": "contraseña123"
}
```
**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
**Response 401:**
```json
{
  "error": "Credenciales incorrectas"
}
```

#### POST `/auth/register`
**Descripción:** Registrar nuevo usuario  
**Acceso:** Público  
**Request Body:**
```json
{
  "email": "nuevo@email.com",
  "password": "password123",
  "nombre": "Nombre Completo"
}
```
**Response 201:**
```json
{
  "message": "Usuario registrado correctamente"
}
```
**Response 400:**
```json
{
  "error": "El email ya está registrado"
}
```

---

### 👤 Usuarios (`/usuario`)

#### GET `/usuario/perfil`
**Descripción:** Obtener perfil del usuario autenticado  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:**
```json
{
  "id": 1,
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "roles": "ROLE_PROFESOR"
}
```

#### PUT `/usuario/{id}`
**Descripción:** Actualizar datos de usuario  
**Roles permitidos:** `ADMIN` (todos) o usuario propio  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:**
```json
{
  "nombre": "Juan Pérez Actualizado",
  "email": "nuevo_email@email.com"
}
```
**Response 200:**
```json
{
  "mensaje": "Usuario actualizado correctamente"
}
```

#### PATCH `/usuario/cambiar-pass`
**Descripción:** Cambiar contraseña del usuario autenticado  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:**
```json
{
  "passwordActual": "contraseña_antigua",
  "nuevaPassword": "contraseña_nueva123"
}
```
**Response 200:**
```json
{
  "mensaje": "Contraseña cambiada correctamente"
}
```

#### DELETE `/usuario/{id}`
**Descripción:** Eliminar usuario  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:**
```json
{
  "mensaje": "Usuario eliminado correctamente"
}
```

---

### 🏛️ Aulas (`/aulas`)

#### GET `/aulas`
**Descripción:** Listar todas las aulas con filtros opcionales  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Query Parameters:**
- `capacidad` (Integer, opcional): Capacidad mínima
- `ordenadores` (Boolean, opcional): Solo aulas con ordenadores

**Response 200:**
```json
[
  {
    "id": 1,
    "nombre": "Aula 101",
    "capacidad": 30,
    "esAulaDeOrdenadores": true,
    "numeroOrdenadores": 25
  },
  {
    "id": 2,
    "nombre": "Aula Magna",
    "capacidad": 100,
    "esAulaDeOrdenadores": false,
    "numeroOrdenadores": null
  }
]
```

#### GET `/aulas/{id}`
**Descripción:** Obtener detalles de un aula específica  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:**
```json
{
  "id": 1,
  "nombre": "Aula 101",
  "capacidad": 30,
  "esAulaDeOrdenadores": true,
  "numeroOrdenadores": 25
}
```

#### POST `/aulas`
**Descripción:** Crear nueva aula  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:**
```json
{
  "nombre": "Aula 102",
  "capacidad": 35,
  "esAulaDeOrdenadores": true,
  "numeroOrdenadores": 30
}
```
**Validaciones:**
- `nombre`: Obligatorio, no vacío
- `capacidad`: Obligatoria, mínimo 1
- `esAulaDeOrdenadores`: Obligatorio
- `numeroOrdenadores`: Si `esAulaDeOrdenadores=true`, mínimo 1

**Response 201:**
```json
{
  "id": 3,
  "nombre": "Aula 102",
  "capacidad": 35,
  "esAulaDeOrdenadores": true,
  "numeroOrdenadores": 30
}
```

#### PUT `/aulas/{id}`
**Descripción:** Actualizar aula existente  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:** (igual que POST)  
**Response 200:** (devuelve aula actualizada)

#### DELETE `/aulas/{id}`
**Descripción:** Eliminar aula  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Response 204:** Sin contenido

#### GET `/aulas/{id}/reservas`
**Descripción:** Obtener todas las reservas de un aula  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Array de reservas

#### GET `/aulas/{id}/reservas-futuras`
**Descripción:** Obtener reservas futuras de un aula  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Array de reservas futuras

---

### ⏰ Horarios (`/horarios`)

#### GET `/horarios`
**Descripción:** Listar todos los horarios  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:**
```json
[
  {
    "id": 1,
    "diaSemana": "LUNES",
    "sesionDia": 1,
    "horaInicio": "08:00:00",
    "horaFin": "09:00:00"
  },
  {
    "id": 2,
    "diaSemana": "LUNES",
    "sesionDia": 2,
    "horaInicio": "09:00:00",
    "horaFin": "10:00:00"
  }
]
```

#### GET `/horarios/{id}`
**Descripción:** Obtener horario específico  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Objeto horario

#### POST `/horarios`
**Descripción:** Crear nuevo horario  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:**
```json
{
  "diaSemana": "MARTES",
  "sesionDia": 3,
  "horaInicio": "10:00:00",
  "horaFin": "11:00:00"
}
```
**Validaciones:**
- `diaSemana`: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES
- `sesionDia`: Obligatorio, mínimo 1
- `horaInicio`: Obligatorio
- `horaFin`: Obligatorio

**Response 201:** Horario creado

#### PUT `/horarios/{id}`
**Descripción:** Actualizar horario  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Horario actualizado

#### DELETE `/horarios/{id}`
**Descripción:** Eliminar horario  
**Roles permitidos:** `ADMIN`  
**Headers:** `Authorization: Bearer <token>`  
**Response 204:** Sin contenido

#### GET `/horarios/dia/{diaSemana}`
**Descripción:** Obtener horarios por día de la semana  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Path Parameter:** `diaSemana` (LUNES, MARTES, etc.)  
**Response 200:** Array de horarios

#### GET `/horarios/sesion/{sesionDia}`
**Descripción:** Obtener horarios por número de sesión  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Path Parameter:** `sesionDia` (Integer)  
**Response 200:** Array de horarios

---

### 📅 Reservas (`/reservas`)

#### GET `/reservas`
**Descripción:** Listar todas las reservas del sistema  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:**
```json
[
  {
    "id": 1,
    "fecha": "2025-12-01T10:00:00",
    "motivo": "Clase de programación",
    "numAsistentes": 25,
    "fechaCreacion": "2025-11-23",
    "aula": {
      "id": 1,
      "nombre": "Aula 101",
      "capacidad": 30,
      "esAulaDeOrdenadores": true,
      "numeroOrdenadores": 25
    },
    "horarios": [
      {
        "id": 1,
        "diaSemana": "LUNES",
        "sesionDia": 1,
        "horaInicio": "08:00:00",
        "horaFin": "09:00:00"
      }
    ],
    "usuarioEmail": "profesor@email.com"
  }
]
```

#### GET `/reservas/{id}`
**Descripción:** Obtener detalles de una reserva  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Objeto reserva completo

#### POST `/reservas`
**Descripción:** Crear nueva reserva  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:**
```json
{
  "fecha": "2025-12-15T10:00:00",
  "motivo": "Taller de robótica",
  "numAsistentes": 20,
  "aulaId": 1,
  "horariosIds": [1, 2, 3]
}
```
**Validaciones Automáticas:**
- ✅ Fecha debe ser futura
- ✅ Número de asistentes no puede superar capacidad del aula
- ✅ No puede haber solapamiento con otras reservas
- ✅ El aula debe existir
- ✅ Los horarios deben existir

**Response 201:**
```json
{
  "id": 2,
  "fecha": "2025-12-15T10:00:00",
  "motivo": "Taller de robótica",
  "numAsistentes": 20,
  "fechaCreacion": "2025-11-23",
  "aula": { ... },
  "horarios": [ ... ],
  "usuarioEmail": "profesor@email.com"
}
```

**Response 400:**
```json
{
  "timestamp": "2025-11-23T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Ya existe una reserva en el aula para esa fecha y horario"
}
```

#### PUT `/reservas/{id}`
**Descripción:** Actualizar reserva existente  
**Roles permitidos:** `ADMIN` (todas), `PROFESOR` (solo propias)  
**Headers:** `Authorization: Bearer <token>`  
**Request Body:** (igual que POST)  
**Response 200:** Reserva actualizada  
**Response 403:** Si profesor intenta editar reserva de otro

#### DELETE `/reservas/{id}`
**Descripción:** Eliminar reserva  
**Roles permitidos:** `ADMIN` (todas), `PROFESOR` (solo propias)  
**Headers:** `Authorization: Bearer <token>`  
**Response 204:** Sin contenido  
**Response 403:** Si profesor intenta eliminar reserva de otro

#### GET `/reservas/mis-reservas`
**Descripción:** Obtener reservas del usuario autenticado  
**Roles permitidos:** `ADMIN`, `PROFESOR`  
**Headers:** `Authorization: Bearer <token>`  
**Response 200:** Array de reservas del usuario

---

## 📦 DTOs (Data Transfer Objects)

### AuthResponse
```java
{
  "token": String,        // Token JWT
  "tipo": String,         // "Bearer"
  "email": String,        // Email del usuario
  "roles": String         // Roles del usuario
}
```

### LoginRequest
```java
{
  "email": String,        // @NotBlank @Email
  "password": String      // @NotBlank
}
```

### RegisterRequest
```java
{
  "email": String,        // @NotBlank @Email
  "password": String,     // @NotBlank @Size(min=6)
  "nombre": String        // @NotBlank
}
```

### UsuarioDTO
```java
{
  "id": Long,
  "nombre": String,
  "email": String,        // @Email
  "roles": String
}
```

### UsuarioUpdateRequest
```java
{
  "nombre": String,       // Opcional
  "email": String         // Opcional @Email
}
```

### CambiarPasswordRequest
```java
{
  "passwordActual": String,    // @NotBlank
  "nuevaPassword": String      // @NotBlank @Size(min=6)
}
```

### AulaDTO
```java
{
  "id": Long,
  "nombre": String,
  "capacidad": Integer,
  "esAulaDeOrdenadores": Boolean,
  "numeroOrdenadores": Integer   // Nullable
}
```

### AulaRequest
```java
{
  "nombre": String,              // @NotBlank
  "capacidad": Integer,          // @NotNull @Min(1)
  "esAulaDeOrdenadores": Boolean,// @NotNull
  "numeroOrdenadores": Integer   // @Min(0), requerido si esAulaDeOrdenadores=true
}
```
**Validación especial:** Si `esAulaDeOrdenadores` es `true`, `numeroOrdenadores` debe ser > 0

### HorarioDTO
```java
{
  "id": Long,
  "diaSemana": DiaSemana,        // ENUM
  "sesionDia": Integer,
  "horaInicio": LocalTime,
  "horaFin": LocalTime
}
```

### HorarioRequest
```java
{
  "diaSemana": DiaSemana,        // @NotNull (LUNES, MARTES, etc.)
  "sesionDia": Integer,          // @NotNull @Min(1)
  "horaInicio": LocalTime,       // @NotNull
  "horaFin": LocalTime           // @NotNull
}
```

### ReservaDTO
```java
{
  "id": Long,
  "fecha": LocalDateTime,
  "motivo": String,
  "numAsistentes": Integer,
  "fechaCreacion": LocalDate,
  "aula": AulaDTO,
  "horarios": List<HorarioDTO>,
  "usuarioEmail": String
}
```

### ReservaRequest
```java
{
  "fecha": LocalDateTime,        // @NotNull @Future
  "motivo": String,              // @NotBlank
  "numAsistentes": Integer,      // @NotNull @Min(1)
  "aulaId": Long,                // @NotNull
  "horariosIds": List<Long>      // Array de IDs de horarios
}
```

### ErrorResponse
```java
{
  "mensaje": String,
  "status": int,
  "timestamp": String
}
```

---

## 🗄️ Entidades del Modelo

### Usuario
```java
@Entity
public class Usuario implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    
    private String nombre;
    
    @Email @NotBlank @Column(unique = true)
    private String email;
    
    @NotBlank @Size(min = 3)
    private String password;          // Encriptado con BCrypt
    
    private String roles;             // "ROLE_ADMIN,ROLE_PROFESOR"
    
    @OneToMany
    private List<Reserva> reservas;
}
```

### Aula
```java
@Entity
@Table(name = "aulas")
public class Aula {
    @Id @GeneratedValue
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private Integer capacidad;
    
    @Column(nullable = false)
    private Boolean esAulaDeOrdenadores;
    
    private Integer numeroOrdenadores;
    
    @OneToMany(mappedBy = "aula", cascade = {PERSIST, REMOVE})
    private List<Reserva> reservas;
}
```

### Horario
```java
@Entity
@Table(name = "horario")
public class Horario {
    @Id @GeneratedValue
    private long id;
    
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;      // LUNES, MARTES, MIERCOLES, JUEVES, VIERNES
    
    private int sesionDia;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    
    @ManyToMany(mappedBy = "horarios")
    private List<Reserva> reservas;
}
```

### Reserva
```java
@Entity
@Table(name = "reserva")
public class Reserva {
    @Id @GeneratedValue
    private long id;
    
    private LocalDateTime fecha;
    private String motivo;
    private Integer numAsistentes;
    
    @CreationTimestamp
    private LocalDate fechaCreacion;
    
    @ManyToOne
    @JoinColumn(name = "aula_id")
    private Aula aula;
    
    @ManyToMany
    @JoinTable(
        name = "reserva_horario",
        joinColumns = @JoinColumn(name = "reserva_id"),
        inverseJoinColumns = @JoinColumn(name = "horario_id")
    )
    private List<Horario> horarios;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    Usuario usuario;
}
```

### Relaciones entre Entidades
```
Usuario (1) ----< (N) Reserva (N) >---- (N) Horario
                        |
                        |
                      (N,1)
                        |
                       Aula
```

---

## 🔐 Seguridad y Autenticación

### Flujo de Autenticación JWT

1. **Usuario se registra** (`POST /auth/register`)
   - Contraseña cifrada con BCrypt
   - Usuario creado con rol `ROLE_USER` por defecto

2. **Usuario inicia sesión** (`POST /auth/login`)
   - Validación de email/password
   - Generación de token JWT con:
     - Subject: email del usuario
     - Claim "roles": roles del usuario
     - Expiración: 24 horas
     - Firma: HS256

3. **Usuario hace peticiones autenticadas**
   - Header: `Authorization: Bearer <token>`
   - Spring Security valida automáticamente el token
   - Extrae roles y permisos

### Configuración de Seguridad

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    // Rutas públicas: /auth/**
    // Rutas protegidas: Todo lo demás
    // Stateless: Sin sesiones HTTP
    // JWT Resource Server configurado
}
```

### Control de Acceso por Roles

| Endpoint | ADMIN | PROFESOR |
|----------|-------|----------|
| POST /auth/register | ✅ | ✅ |
| POST /auth/login | ✅ | ✅ |
| GET /usuario/perfil | ✅ | ✅ |
| PUT /usuario/{id} | ✅ | ✅ (solo propio) |
| DELETE /usuario/{id} | ✅ | ❌ |
| GET /aulas | ✅ | ✅ |
| POST /aulas | ✅ | ❌ |
| PUT /aulas/{id} | ✅ | ❌ |
| DELETE /aulas/{id} | ✅ | ❌ |
| GET /horarios | ✅ | ✅ |
| POST /horarios | ✅ | ❌ |
| PUT /horarios/{id} | ✅ | ❌ |
| DELETE /horarios/{id} | ✅ | ❌ |
| GET /reservas | ✅ | ✅ |
| POST /reservas | ✅ | ✅ |
| PUT /reservas/{id} | ✅ | ✅ (solo propias) |
| DELETE /reservas/{id} | ✅ | ✅ (solo propias) |

### Anotaciones de Seguridad Utilizadas

```java
@PreAuthorize("hasRole('ADMIN')")
@PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
```

---

## ✅ Validaciones de Negocio

### Validaciones en Aulas
- ✅ El nombre no puede estar vacío
- ✅ La capacidad debe ser mayor a 0
- ✅ Si es aula de ordenadores, debe tener al menos 1 ordenador
- ✅ Si NO es aula de ordenadores, numeroOrdenadores debe ser null

### Validaciones en Horarios
- ✅ Día de la semana válido (LUNES-VIERNES)
- ✅ Sesión del día mínimo 1
- ✅ Horas de inicio y fin obligatorias

### Validaciones en Reservas
- ✅ **Fecha futura:** No se permiten reservas en el pasado
- ✅ **Capacidad:** El número de asistentes no puede superar la capacidad del aula
- ✅ **Solapamiento:** No puede haber dos reservas en la misma aula, fecha y horarios
- ✅ **Asistentes mínimo:** Debe haber al menos 1 asistente
- ✅ **Aula válida:** El aula debe existir en el sistema
- ✅ **Horarios válidos:** Todos los horarios deben existir
- ✅ **Al menos un horario:** Debe seleccionarse al menos un horario

### Queries de Validación

```java
// Verificar solapamiento de reservas
@Query("SELECT r
