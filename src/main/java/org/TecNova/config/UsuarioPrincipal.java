package org.TecNova.config;

import org.TecNova.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** Usuario autenticado: envuelve la entidad Usuario (y el id del cliente si el rol es Cliente). */
public class UsuarioPrincipal implements UserDetails
{
    private static final long serialVersionUID = 1L;

    private final Usuario usuario;
    private final Long clienteId;

    public UsuarioPrincipal( Usuario usuario, Long clienteId )
    {
        this.usuario = usuario;
        this.clienteId = clienteId;
    }

    public Usuario getUsuario( )
    {
        return usuario;
    }

    public Long getClienteId( )
    {
        return clienteId;
    }

    public boolean esAdmin( )
    {
        return "Administrador".equals( usuario.getRol( ).getNombre( ) );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities( )
    {
        return List.of( new SimpleGrantedAuthority( "ROLE_" + usuario.getRol( ).getNombre( ).toUpperCase( Locale.ROOT ) ) );
    }

    @Override
    public String getPassword( )
    {
        return usuario.getPasswordHash( );
    }

    @Override
    public String getUsername( )
    {
        return usuario.getCorreo( );
    }

    @Override
    public boolean isAccountNonExpired( )
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked( )
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired( )
    {
        return true;
    }

    @Override
    public boolean isEnabled( )
    {
        return Boolean.TRUE.equals( usuario.getEstatus( ) );
    }
}
