package org.TecNova.config;

import org.TecNova.model.Usuario;
import org.TecNova.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * SOLO DESARROLLO: los usuarios del script SQL traen un hash de marcador. Al arrancar se reemplaza por una
 * contraseña real según el rol (Admin123 / Cajero123 / Cliente123). Desactívalo con app.inicializar-passwords=false.
 */
@Component
public class InicializadorPasswords implements CommandLineRunner
{
    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final boolean activo;

    public InicializadorPasswords( UsuarioRepository repository, PasswordEncoder encoder,
                                   @Value( "${app.inicializar-passwords:false}" ) boolean activo )
    {
        this.repository = repository;
        this.encoder = encoder;
        this.activo = activo;
    }

    @Override
    public void run( String... args )
    {
        if( !activo )
        {
            return;
        }
        try
        {
            int n = 0;
            for( Usuario usuario : repository.findAll( ) )
            {
                if( usuario.getPasswordHash( ) != null && usuario.getPasswordHash( ).startsWith( "$2b$12$HASH_DE_PRUEBA" ) )
                {
                    String rol = usuario.getRol( ).getNombre( );
                    String pass = "Administrador".equals( rol ) ? "Admin123" : "Cajero".equals( rol ) ? "Cajero123" : "Cliente123";
                    usuario.setPasswordHash( encoder.encode( pass ) );
                    repository.save( usuario );
                    n++;
                }
            }
            if( n > 0 )
            {
                System.out.println( "[Farmacias URB] Contraseñas de prueba asignadas a " + n + " usuario(s)." );
            }
        }
        catch( RuntimeException e )
        {
            System.err.println( "[Farmacias URB] No se pudieron inicializar las contraseñas: " + e.getMessage( ) );
        }
    }
}
