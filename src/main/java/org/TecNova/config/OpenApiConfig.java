package org.TecNova.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Datos que muestra Swagger UI y esquema de autenticación (botón "Authorize": correo y contraseña). */
@Configuration
public class OpenApiConfig
{
    @Bean
    public OpenAPI farmaciasOpenApi( )
    {
        return new OpenAPI()
                .info( new Info( )
                        .title( "Farmacias URB - API REST" )
                        .version( "1.0" )
                        .description( "Inventario, punto de venta, clientes y programa de lealtad de Farmacias URB (TecNova). "
                                + "Pulsa Authorize e inicia sesión con tu correo y contraseña (por ejemplo admin@farmaciasurb.com / Admin123)." ) )
                .components( new Components( ).addSecuritySchemes( "basicAuth",
                        new SecurityScheme( ).type( SecurityScheme.Type.HTTP ).scheme( "basic" ) ) )
                .addSecurityItem( new SecurityRequirement( ).addList( "basicAuth" ) );
    }
}
