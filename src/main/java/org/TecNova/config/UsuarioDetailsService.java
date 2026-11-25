package org.TecNova.config;

import org.TecNova.model.Cliente;
import org.TecNova.model.Usuario;
import org.TecNova.repository.ClienteRepository;
import org.TecNova.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** RF-01: Spring Security busca al usuario por correo en la base de datos. */
@Service
public class UsuarioDetailsService implements UserDetailsService
{
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public UsuarioDetailsService( UsuarioRepository usuarioRepository, ClienteRepository clienteRepository )
    {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername( String correo ) throws UsernameNotFoundException
    {
        Usuario usuario = usuarioRepository.findByCorreo( correo.trim( ).toLowerCase( ) )
                .orElseThrow( ( ) -> new UsernameNotFoundException( "Usuario no encontrado" ) );
        Long clienteId = clienteRepository.findByUsuarioId( usuario.getId( ) ).map( Cliente::getId ).orElse( null );
        return new UsuarioPrincipal( usuario, clienteId );
    }
}
