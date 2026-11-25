package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.Usuario;
import org.TecNova.service.RolService;
import org.TecNova.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/usuarios" )
@Tag( name = "Usuarios" )
public class UsuarioApiController
{

    private final UsuarioService service;
    private final RolService rolService;

    public UsuarioApiController( UsuarioService service, RolService rolService )
    {
        this.service = service;
        this.rolService = rolService;
    }

    private Usuario llenar( Usuario u, UsuarioReq body )
    {
        u.setNombre( body.nombre( ) );
        u.setCorreo( body.correo( ) );
        u.setRol( rolService.findById( body.rolId( ) ) );
        u.setEstatus( body.estatus( ) == null ? Boolean.TRUE : body.estatus( ) );
        return u;
    }

    @GetMapping
    @Operation( summary = "Lista los usuarios (sin contraseñas)" )
    public List<UsuarioRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::usuario ).toList( );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Obtiene un usuario por su id" )
    public UsuarioRes obtener( @PathVariable Long id )
    {
        return ApiMapper.usuario( ApiMapper.requerido( service.findById( id ), "El usuario no existe." ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Crea un usuario (la contraseña se guarda cifrada con bcrypt). Rol Cliente requiere clienteId" )
    public UsuarioRes crear( @RequestBody UsuarioReq body )
    {
        return ApiMapper.usuario( service.guardar( llenar( new Usuario( ), body ), body.password( ), body.clienteId( ) ) );
    }

    @PutMapping( "/{id}" )
    @Operation( summary = "Actualiza un usuario (si password viene vacío no se cambia)" )
    public UsuarioRes actualizar( @PathVariable Long id, @RequestBody UsuarioReq body,
                                  @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        Usuario u = ApiMapper.requerido( service.findById( id ), "El usuario no existe." );
        if( Boolean.FALSE.equals( body.estatus( ) ) && id.equals( actual.getUsuario( ).getId( ) ) )
        {
            throw new IllegalArgumentException( "No puedes dar de baja tu propia cuenta." );
        }
        return ApiMapper.usuario( service.guardar( llenar( u, body ), body.password( ), body.clienteId( ) ) );
    }

    @DeleteMapping( "/{id}" )
    @Operation( summary = "Da de baja un usuario (baja lógica)" )
    public MensajeRes baja( @PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        if( id.equals( actual.getUsuario( ).getId( ) ) )
        {
            throw new IllegalArgumentException( "No puedes dar de baja tu propia cuenta." );
        }
        service.baja( id );
        return new MensajeRes( "Usuario dado de baja." );
    }
}
