# DUNAB — Gestión de Dinero UNAB

Sistema completo de gestión financiera para estudiantes de la Universidad Autónoma de Bucaramanga.

**Autores:** Gonzalo Mejía · Valeria Acosta · María Alejandra Suarez  
**Materia:** Estructuras de Datos y Análisis de Algoritmos — UNAB

---

## Cómo ejecutar localmente

### Requisitos
- Java 17+
- MySQL 8+
- Maven 3.8+

### 1. Configurar la base de datos
```sql
CREATE DATABASE dunab_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar credenciales
Edita `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dunab_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA
```

### 3. Compilar y ejecutar
```bash
mvn clean package -DskipTests
java -jar target/dunab-1.0.0.jar
```

O con Maven directamente:
```bash
mvn spring-boot:run
```

### 4. Acceder al sistema
- Abrir: http://localhost:8080
- Admin: `admin@unab.edu.co` / `Admin2024$`

---

## Deploy en Railway (GRATIS)

Railway ofrece $5/mes de créditos gratuitos. Suficiente para este proyecto.

### Pasos:

1. **Crear cuenta** en [railway.app](https://railway.app) (con GitHub)

2. **New Project** → **Deploy from GitHub repo** → Selecciona `lxcruz404/DUNAB`

3. **Agregar MySQL**: En el proyecto → `+ New` → `Database` → `MySQL`

4. **Variables de entorno** en tu servicio Java:
   ```
   DATABASE_URL = jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQLDATABASE}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota
   DATABASE_USERNAME = ${MYSQLUSER}
   DATABASE_PASSWORD = ${MYSQLPASSWORD}
   JWT_SECRET = DunabSecretKey2024UnabBucaramangaFinance
   SPRING_PROFILES_ACTIVE = prod
   ```
   
   > Railway inyecta `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, `MYSQLPASSWORD` automáticamente desde la DB conectada.

5. **Deploy** — Railway detecta el `pom.xml` y compila automáticamente.

6. **¡Listo!** Tu URL será algo como `https://dunab-production.up.railway.app`

---

## API Endpoints

### Autenticación (público)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/auth/register` | Registro de nuevo estudiante |
| POST | `/api/auth/login` | Inicio de sesión (retorna JWT) |
| GET | `/api/auth/check` | Health check |

### Usuario (requiere JWT)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/users/me` | Perfil del usuario |
| PUT | `/api/users/me` | Actualizar perfil |
| POST | `/api/users/me/photo` | Subir foto de perfil |
| GET | `/api/users/me/achievements` | Logros del usuario |
| POST | `/api/users/calculator` | Calculadora de notas |

### Transacciones
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/transactions` | Registrar movimiento |
| GET | `/api/transactions` | Historial paginado |
| GET | `/api/transactions/recent` | Últimas 10 |
| GET | `/api/transactions/stats` | Estadísticas completas |

### Encuentros
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/encounters` | Todos los encuentros |
| GET | `/api/encounters/upcoming` | Solo próximos |
| POST | `/api/encounters/{id}/register` | Inscribirse |
| POST | `/api/encounters/{id}/collect` | Cobrar DUNAB |
| POST | `/api/encounters` | Crear (solo ADMIN) |

### Metas de Ahorro
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/goals` | Mis metas |
| POST | `/api/goals` | Crear meta |
| PUT | `/api/goals/{id}/sync` | Sincronizar progreso |
| DELETE | `/api/goals/{id}` | Eliminar meta |

### Ranking
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/ranking/top12` | Top 12 estudiantes |
| GET | `/api/ranking/my-position` | Mi posición |

---

## Arquitectura

```
Frontend (HTML/CSS/JS)
    ↓ fetch() con JWT Bearer token
REST Controllers (@RestController)
    ↓
Services (lógica de negocio)
    ↓
Repositories (Spring Data JPA)
    ↓
MySQL (persistencia)
```

### Estructuras de datos implementadas
- **Lista enlazada**: Historial de transacciones (JPA + ORDER BY fecha DESC)
- **Cola de prioridad**: Ranking Top 12 (ORDER BY dunab_balance DESC LIMIT 12)  
- **Árbol de búsqueda**: Índices MySQL en campos de búsqueda frecuente
- **Hash**: Mapa de categorías para estadísticas (GROUP BY)

### Clases modelo (OOP)
- `User` — Estudiante/Admin con balance DUNAB
- `Transaction` — Movimiento financiero (ingreso/gasto)
- `Encounter` — Actividad universitaria con recompensa
- `EncounterRegistration` — Inscripción de estudiante a encuentro
- `Goal` — Meta de ahorro personal
- `Achievement` — Logro/insignia desbloqueada

---

## Seguridad
- Contraseñas cifradas con BCrypt (factor 12)
- Autenticación JWT (expiración 24h)
- Spring Security con roles STUDENT / ADMIN
- CORS configurado para producción
- Validación de entradas con Bean Validation

---

## 📄 Licencia
Proyecto académico — UNAB 2024
