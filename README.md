# Farmacias URB — Spring Boot 2.7 (Spring MVC + Thymeleaf + Spring Data JPA + Spring Security)

Misma base de datos y mismas pantallas que el proyecto Maven de servlets (`Farmacias_URB`), pero con Spring Boot.
Paquete base `org.TecNova`: `model` (mismas entidades), `repository` (equivalente a los DAO), `service`,
`controller`, `config` (seguridad), `util`.

## Puesta en marcha
1. **Base de datos:** ejecuta `sql/Farmacias_BD.sql` completo en MySQL Workbench.
2. **Conexión:** edita `src/main/resources/application.properties` (`spring.datasource.username` / `password`)
   o define las variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD`.
3. **Ejecutar:** `mvn spring-boot:run` (o ejecuta `FarmaciasUrbApplication` desde el IDE). No necesitas instalar Tomcat.
4. Abre **http://localhost:8080/** y entra con `admin@farmaciasurb.com` / `Admin123`
   (`cajero1@…` / `Cajero123`, `cliente1@…` / `Cliente123`). Las contraseñas de ejemplo se asignan al arrancar
   (`app.inicializar-passwords=true`; ponlo en `false` en producción).

## Swagger (API REST)
Con la aplicación en marcha abre **http://localhost:8080/swagger-ui.html** (documento OpenAPI en `/v3/api-docs`).
1. Pulsa **Authorize** e ingresa tu correo y contraseña (p. ej. `admin@farmaciasurb.com` / `Admin123`). Se usa HTTP Basic.
2. Prueba cualquier endpoint con **Try it out**. Empieza por `GET /api/auth/me`.
3. Los permisos son los mismos que en la web: el administrador usa todo, el cajero solo inventario, clientes y ventas,
   y el cliente solo su historial (`/api/ventas/mis-compras`) y el catálogo.

Ejemplo para registrar una venta (`POST /api/ventas`):
```json
{ "telefonoCliente": "5512345603", "metodoPago": "EFECTIVO", "items": [ { "idLote": 1, "cantidad": 2 } ] }
```
Los `idLote` disponibles salen de `GET /api/inventario`.

## Rutas principales
`/login` · `/` · `/inventario` · `/almacen` · `/catalogos` · `/entradas` · `/salidas` · `/ventas` · `/pos` ·
`/medicamentos` · `/categorias` · `/proveedores` · `/usuarios` · `/clientes` · `/mis-compras` · `/configuracion` · `/ticket/{id}`
