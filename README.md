# Zendr — Backend

API REST para **Zendr**, una aplicación móvil que conecta a monitores de Yoga, Pilates y Calistenia con alumnos mediante clases geolocalizadas al aire libre, sin cuotas fijas ni locales físicos.

Este repositorio contiene el **servidor**: API REST, modelo de datos, seguridad y las integraciones con servicios externos.

> Proyecto final del CFGS de Desarrollo de Aplicaciones Multiplataforma (Upgrade Hub, 2026), desarrollado por un equipo de tres personas. Mi responsabilidad fue el **backend completo y el diseño de la base de datos**. El cliente móvil (React Native + Expo) fue desarrollado por mis compañeros.

---

## Stack

| Capa | Tecnologías |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot · Spring MVC · Spring WebFlux |
| Persistencia | MongoDB Atlas · Spring Data MongoDB |
| Seguridad | Spring Security · JWT (jjwt) |
| Almacenamiento | Bucket compatible con S3 (AWS SDK) |
| Servicios externos | OpenWeather · TomTom Search & Geocoding · SMTP · ZXing (QR) |
| Build y despliegue | Maven · Docker · Railway |

---

## Arquitectura

Arquitectura **orientada a dominios** combinada con el patrón clásico en capas. Cada dominio agrupa su modelo y su repositorio; los servicios contienen la lógica de negocio y los controladores actúan únicamente como punto de entrada HTTP.

```
com.zendr.backend
├── api/controllers      → Controladores REST
├── config               → Seguridad, beans, clientes de APIs externas
├── internal             → Un paquete por dominio (model + repository)
│   ├── user, event, booking, waitList, device, discipline,
│   ├── token, emailAuthCode, notification, notifyContent,
│   └── weather, log, system, version
├── services             → Lógica de negocio (interfaz + implementación)
└── utils                → Utilidades transversales
```

Los servicios siguen el patrón **interfaz + implementación** (`EventService` / `EventServiceImpl`), lo que mantiene los controladores desacoplados de la implementación concreta.

---

## Decisiones técnicas

**Modelo de datos desnormalizado.** Se optó por MongoDB y por desnormalizar de forma controlada para eliminar los *joins* en las rutas críticas de lectura (listado y búsqueda de eventos), a costa de asumir cierta duplicación de datos.

**Autenticación por JWT con revocación.** El token de acceso se valida en cada petición mediante un filtro que se ejecuta antes del `UsernamePasswordAuthenticationFilter`. La sesión es *stateless*, pero los tokens se persisten para poder **revocarlos en el logout**, lo que resuelve la limitación habitual de JWT de no poder invalidar un token antes de su expiración.

**Autorización granular.** Además del control por roles, se usa `@PreAuthorize` con expresiones SpEL para verificar la **propiedad del recurso**: un monitor solo puede modificar los eventos que ha creado él.

```java
@PreAuthorize("hasRole('ADMIN') || " +
              "@eventRepository.findById(#eventId)...monitorId... == authentication.name")
```

**Cálculo de distancias con Haversine.** Las coordenadas se almacenan como GeoJSON y la distancia entre usuario y evento se calcula con la fórmula de Haversine, que resuelve la trigonometría esférica sobre el radio terrestre y devuelve metros con precisión suficiente para ordenar resultados por proximidad.

**Borrado lógico en reservas.** Cancelar una reserva y crear otra generaba conflictos de duplicidad. Se resolvió con *soft delete* y estados diferenciados de cancelación, preservando el histórico para auditoría sin interferir en la consulta de la reserva vigente.

**Validación meteorológica.** Las sesiones al aire libre se contrastan contra la API de OpenWeather mediante `WebClient` reactivo, bloqueando la creación de eventos en condiciones desfavorables.

---

## Funcionalidades

- Registro y login con verificación de cuenta por código enviado por correo
- Perfiles diferenciados: usuario, monitor y administrador
- Creación, edición y cancelación de eventos con imagen adjunta (multipart)
- Búsqueda de eventos por disciplina, nivel, precio y distancia
- Reservas, lista de espera y control de aforo
- Check-in mediante código QR
- Recuperación y cambio de contraseña
- Baja de cuenta con cancelación en cascada de los eventos del monitor

---

## Puesta en marcha

Requisitos: **JDK 21**, **Maven** y una instancia de **MongoDB** (local o Atlas).

```bash
git clone https://github.com/adrian-mtnzz/Zendr-Backend.git
cd Zendr-Backend/backend
./mvnw spring-boot:run
```

### Variables de entorno

La aplicación no contiene ninguna credencial en el código. Todas se inyectan por entorno:

| Variable | Descripción |
|---|---|
| `ZENDR_DB_URI` | Cadena de conexión de MongoDB |
| `ZENDR_JWT_SECRET` | Clave de firma de los tokens |
| `ZENDR_SMTP_HOST` / `ZENDR_SMTP_PORT` | Servidor de correo saliente |
| `ZENDR_SMTP_USERNAME` / `ZENDR_SMTP_PASSWORD` | Credenciales SMTP |
| `ZENDR_OPENWEATHER_API_KEY` | Clave de OpenWeather |
| `ZENDR_TOMTOM_API_KEY` / `ZENDR_TOMTOM_SEARCH_KEY` | Claves de TomTom |
| `ZENDR_S3_*` | Endpoint, bucket y credenciales del almacenamiento |
| `PORT` | Puerto del servidor |

> Ajusta los nombres a los que uses realmente en tus `application*.yaml`.

Perfiles disponibles: `dev` y `prod`.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Estado del proyecto

Versión funcional entregada y desplegada en Railway. Líneas de trabajo pendientes:

- [ ] Recuperar la batería de tests de integración con Spring Boot Test y Testcontainers
- [ ] Dividir `EventServiceImpl` y `UserServiceImpl`, que han crecido en exceso
- [ ] Índices geoespaciales `2dsphere` para delegar la búsqueda por proximidad en MongoDB
- [ ] Pasarela de pagos con Stripe
- [ ] Caché con Redis para las consultas repetidas de geocodificación
- [ ] Pipeline de CI con GitHub Actions

---

## Licencia

GPL-3.0. Ver [LICENSE](LICENSE).
