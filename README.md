# Farmacias URB — Spring Boot

Proyecto Maven con **Spring Boot 2.7.18**, **Java 17 (Amazon Corretto 17)**, Spring MVC, Thymeleaf, Spring Data JPA, Spring Security, MySQL y Swagger/OpenAPI.

La clase que inicializa Spring Boot es:

`src/main/java/org/TecNova/FarmaciasUrbApplication.java`

Contiene `@SpringBootApplication` y el método `main()` que arranca el servidor web integrado. No necesitas instalar Tomcat por separado.

## 1. Requisitos

- Amazon Corretto JDK 17 (no basta con instalar solo el JRE). Guía oficial para Windows: https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/windows-install.html
- IntelliJ IDEA Community Edition.
- MySQL Server 8 y MySQL Workbench (o un cliente MySQL equivalente).
- Conexión a Internet la primera vez que IntelliJ descargue las dependencias Maven.

## 2. Abrir en IntelliJ IDEA Community

1. Descomprime el ZIP en una carpeta local.
2. Abre IntelliJ IDEA Community y selecciona **File > Open**.
3. Selecciona la carpeta `Farmacias_SpringBoot` (la que contiene `pom.xml`). No abras únicamente `src`.
4. Cuando IntelliJ detecte Maven, selecciona **Load Maven Project** o **Trust Project** si aparece.
5. En **File > Project Structure > Project**, selecciona el SDK **corretto-17**. Si no aparece, pulsa **Add SDK > JDK** y selecciona la carpeta donde instalaste Amazon Corretto 17. Configura también el nivel del lenguaje en **17**.
6. En **Settings > Build, Execution, Deployment > Build Tools > Maven**, utiliza el Maven incluido en IntelliJ (**Bundled Maven**) y selecciona **corretto-17** para el JDK del importador y del Runner.
7. En **Settings > Plugins**, instala o habilita **Lombok** si el IDE muestra errores en getters, setters o constructores generados. En **Settings > Build, Execution, Deployment > Compiler > Annotation Processors**, activa **Enable annotation processing**.
8. Abre la ventana **Maven** y pulsa **Reload All Maven Projects** para descargar y reconocer las dependencias.

El ZIP incluye una configuración compartida de ejecución llamada **Farmacias URB - Spring Boot**. Después de que IntelliJ termine de importar Maven, selecciónala junto al botón verde **Run**.

## 3. Preparar la base de datos MySQL

1. Inicia el servicio de MySQL Server.
2. Abre MySQL Workbench y ejecuta el script completo `sql/Farmacias_BD.sql`.
3. El script crea la base de datos `farmacias_urb` y sus tablas.
4. Abre `src/main/resources/application.properties` y comprueba que los datos de conexión coincidan con tu instalación de MySQL:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://127.0.0.1:3306/farmacias_urb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Mexico_City&characterEncoding=UTF-8}
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:n0m3l0}
```

Los valores situados después de `:` son los valores locales predeterminados. Si tu usuario de MySQL o su contraseña son distintos, modifica esos valores o define las variables de entorno `DB_URL`, `DB_USER` y `DB_PASSWORD`. La contraseña de MySQL no es la contraseña de inicio de sesión de la aplicación.

## 4. Inicializar y ejecutar Spring Boot

### Desde IntelliJ (recomendado)

1. Espera a que finalice la importación Maven.
2. Selecciona la configuración **Farmacias URB - Spring Boot**.
3. Pulsa **Run** (triángulo verde). También puedes abrir `FarmaciasUrbApplication.java` y pulsar el triángulo que aparece junto al método `main()`.
4. Espera a que la consola muestre el mensaje `Started FarmaciasUrbApplication` y que Tomcat indique que está escuchando en el puerto `8080`.

### Desde la terminal de IntelliJ

Con Corretto 17 seleccionado como JDK y Maven disponible, ejecuta:

```bash
mvn spring-boot:run
```

Para generar el JAR ejecutable:

```bash
mvn clean package
```

El resultado se guarda en `target/Farmacias_URB_SpringBoot-1.0-SNAPSHOT.jar` y se ejecuta con:

```bash
java -jar target/Farmacias_URB_SpringBoot-1.0-SNAPSHOT.jar
```

### Inicio directo en Windows

El ZIP también incluye `INICIAR_SPRING_BOOT.cmd` y un JAR ejecutable precompilado con Java 17. Después de configurar MySQL y `application.properties`, puedes hacer doble clic en ese archivo. El script verifica que `java`/`JAVA_HOME` apunte a Java 17 antes de iniciar Spring Boot.

Para trabajar con el código fuente y ejecutarlo desde IntelliJ, sigue los pasos anteriores y usa la configuración **Farmacias URB - Spring Boot**.

## 5. Acceso

- Aplicación web: http://localhost:8080/
- Swagger UI: http://localhost:8080/swagger-ui.html
- Documento OpenAPI: http://localhost:8080/v3/api-docs

Cuentas de demostración indicadas por el proyecto:

- Administrador: `admin@farmaciasurb.com` / `Admin123`
- Cajero: `cajero1@farmaciasurb.com` / `Cajero123`
- Cliente: `cliente1@farmaciasurb.com` / `Cliente123`

En desarrollo, `app.inicializar-passwords=true` permite inicializar las contraseñas de demostración al arrancar. No lo dejes activado en un entorno real.

## 6. Errores frecuentes

- **`Access denied for user`**: el usuario o la contraseña configurados en `application.properties` no coinciden con MySQL.
- **`Communications link failure` o `Connection refused`**: inicia MySQL y confirma que escucha en el puerto `3306`.
- **`Port 8080 was already in use`**: cierra la otra aplicación que utiliza el puerto 8080 o cambia `server.port` en `application.properties`.
- **Errores de métodos `get...()` / `set...()` en clases del modelo**: habilita el procesamiento de anotaciones y comprueba que la dependencia/plugin de Lombok esté cargada.
- **El proyecto aparece como Java normal y no como Maven**: abre la carpeta raíz que contiene `pom.xml` y usa **Add as Maven Project** o **Reload All Maven Projects**.

## 7. Dependencias principales

Las versiones se administran con el parent de Spring Boot 2.7.18. Se conserva esta línea porque el código existente utiliza `javax.persistence` y `javax.servlet`; migrar a Spring Boot 3 requeriría cambiar esos imports y validar la compatibilidad de las vistas y dependencias.
