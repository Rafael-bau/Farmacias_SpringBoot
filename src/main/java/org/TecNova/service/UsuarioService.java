package org.TecNova.service;

import org.TecNova.model.Cliente;
import org.TecNova.model.Usuario;
import org.TecNova.repository.ClienteRepository;
import org.TecNova.repository.UsuarioRepository;
import org.TecNova.util.Validador;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService
{
    private final UsuarioRepository repository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder encoder;

    public UsuarioService( UsuarioRepository repository, ClienteRepository clienteRepository, PasswordEncoder encoder )
    {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.encoder = encoder;
    }

    public List<Usuario> findAll( )
    {
        return repository.findAllByOrderByEstatusDescNombreAsc( );
    }

    public Usuario findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }

    /** Alta o edición. En alta la contraseña es obligatoria; en edición, si viene vacía no se cambia. */
    @Transactional
    public Usuario guardar( Usuario usuario, String password, Long clienteId )
    {
        boolean nuevo = usuario.getId( ) == null;
        String error = Validador.validarPersona( usuario.getNombre( ), usuario.getCorreo( ) );
        if( error != null )
        {
            throw new IllegalArgumentException( error );
        }
        if( usuario.getRol( ) == null )
        {
            throw new IllegalArgumentException( "Selecciona un rol." );
        }
        boolean esCliente = "Cliente".equals( usuario.getRol( ).getNombre( ) );
        if( esCliente && clienteId == null )
        {
            throw new IllegalArgumentException( "Para el rol Cliente selecciona el cliente al que pertenece la cuenta." );
        }
        if( nuevo || ( password != null && !password.isEmpty( ) ) )
        {
            if( !Validador.password( password ) )
            {
                throw new IllegalArgumentException( "La contraseña debe tener entre 8 y 72 caracteres." );
            }
            usuario.setPasswordHash( encoder.encode( password ) );
        }
        usuario.setNombre( usuario.getNombre( ).trim( ) );
        usuario.setCorreo( usuario.getCorreo( ).trim( ).toLowerCase( ) );
        if( usuario.getEstatus( ) == null )
        {
            usuario.setEstatus( true );
        }
        Usuario guardado = repository.saveAndFlush( usuario );

        // Liga la cuenta con el cliente (o la desliga si el rol ya no es Cliente)
        clienteRepository.desvincular( guardado.getId( ) );
        if( esCliente )
        {
            Cliente cliente = clienteRepository.findById( clienteId )
                    .orElseThrow( ( ) -> new IllegalArgumentException( "El cliente indicado no existe." ) );
            if( cliente.getUsuario( ) != null )
            {
                throw new IllegalArgumentException( "Ese cliente ya tiene una cuenta de acceso asignada." );
            }
            cliente.setUsuario( guardado );
        }
        return guardado;
    }

    /** Baja lógica: el usuario tiene ventas históricas, no se borra. */
    @Transactional
    public void baja( Long id )
    {
        repository.findById( id ).ifPresent( u -> u.setEstatus( false ) );
    }

    @Transactional
    public void cambiarPassword( Long idUsuario, String actual, String nueva )
    {
        Usuario usuario = repository.findById( idUsuario ).orElse( null );
        if( usuario == null || actual == null || !encoder.matches( actual, usuario.getPasswordHash( ) ) )
        {
            throw new IllegalArgumentException( "La contraseña actual no es correcta." );
        }
        if( !Validador.password( nueva ) )
        {
            throw new IllegalArgumentException( "La nueva contraseña debe tener entre 8 y 72 caracteres." );
        }
        usuario.setPasswordHash( encoder.encode( nueva ) );
    }
}
