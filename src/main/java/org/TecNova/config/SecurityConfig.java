package org.TecNova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Seguridad de la aplicación:
 *  - Pantallas web: inicio de sesión con formulario (RF-01), cierre de sesión (RF-02), CSRF activo.
 *  - API REST (/api/**, documentada en Swagger): autenticación HTTP Basic (correo y contraseña) o la sesión
 *    iniciada en la web; sin token CSRF para poder probarla desde Swagger UI.
 */
@Configuration
public class SecurityConfig
{
    @Bean
    public PasswordEncoder passwordEncoder( )
    {
        return new BCryptPasswordEncoder( 10 );
    }

    @Bean
    public SecurityFilterChain filterChain( HttpSecurity http ) throws Exception
    {
        http
            .csrf( csrf -> csrf.ignoringAntMatchers( "/api/**" ) )
            .exceptionHandling( ex -> ex.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint( HttpStatus.UNAUTHORIZED ), new AntPathRequestMatcher( "/api/**" ) ) )
            .authorizeRequests( auth -> auth
                // ---- públicos: estilos, login y documentación Swagger ----
                .antMatchers( "/css/**", "/login", "/error",
                        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**" ).permitAll( )

                // ---- API REST ----
                .antMatchers( HttpMethod.POST, "/api/ventas/*/cancelar" ).hasRole( "ADMINISTRADOR" )
                .antMatchers( HttpMethod.GET, "/api/ventas/mis-compras" ).hasRole( "CLIENTE" )
                .antMatchers( HttpMethod.GET, "/api/ventas/*", "/api/ventas/*/ticket" ).authenticated( )
                .antMatchers( HttpMethod.DELETE, "/api/clientes/**" ).hasRole( "ADMINISTRADOR" )
                .antMatchers( "/api/ventas/**", "/api/clientes/**", "/api/inventario/**", "/api/almacen/**", "/api/alertas" )
                    .hasAnyRole( "ADMINISTRADOR", "CAJERO" )
                .antMatchers( "/api/categorias/**", "/api/medicamentos/**", "/api/proveedores/**",
                        "/api/usuarios/**", "/api/entradas/**", "/api/salidas/**" ).hasRole( "ADMINISTRADOR" )
                .antMatchers( "/api/**" ).authenticated( )

                // ---- pantallas web ----
                .antMatchers( HttpMethod.POST, "/ventas/cancelar", "/clientes/eliminar" ).hasRole( "ADMINISTRADOR" )
                .antMatchers( "/medicamentos/**", "/categorias/**", "/proveedores/**", "/usuarios/**",
                        "/entradas/**", "/salidas/**" ).hasRole( "ADMINISTRADOR" )
                .antMatchers( "/inventario/**", "/almacen/**", "/ventas/**", "/pos/**", "/clientes/**" )
                    .hasAnyRole( "ADMINISTRADOR", "CAJERO" )
                .antMatchers( "/mis-compras/**" ).hasRole( "CLIENTE" )
                .anyRequest( ).authenticated( ) )
            .httpBasic( Customizer.withDefaults( ) )
            .formLogin( form -> form
                .loginPage( "/login" )
                .usernameParameter( "correo" )
                .passwordParameter( "password" )
                .defaultSuccessUrl( "/", true )
                .permitAll( ) )
            .logout( logout -> logout
                .logoutSuccessUrl( "/login?logout" )
                .permitAll( ) );
        return http.build( );
    }
}
