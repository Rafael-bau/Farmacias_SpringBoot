package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.Cliente;
import org.TecNova.model.Usuario;
import org.TecNova.service.ClienteService;
import org.TecNova.service.RolService;
import org.TecNova.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/usuarios" )
public class UsuarioController extends BaseController
{

    private final UsuarioService service;
    private final RolService rolService;
    private final ClienteService clienteService;

    public UsuarioController( UsuarioService service, RolService rolService, ClienteService clienteService )
    {
        this.service = service;
        this.rolService = rolService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String listar( Model model )
    {
        java.util.List<Cliente> clientes = clienteService.findAll( );
        Map<Long, Cliente> clientePorUsuario = new HashMap<>( );
        for( Cliente cliente : clientes )
        {
            if( cliente.getUsuario( ) != null )
            {
                clientePorUsuario.put( cliente.getUsuario( ).getId( ), cliente );
            }
        }
        model.addAttribute( "usuarios", service.findAll( ) );
        model.addAttribute( "roles", rolService.findAll( ) );
        model.addAttribute( "clientes", clientes );
        model.addAttribute( "clientePorUsuario", clientePorUsuario );
        return "usuarios";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam( required = false ) Long id, @RequestParam String nombre,
                           @RequestParam String correo, @RequestParam( required = false ) String password,
                           @RequestParam Long rolId, @RequestParam( required = false ) Long clienteId,
                           @RequestParam( defaultValue = "1" ) String estatus,
                           @AuthenticationPrincipal UsuarioPrincipal actual, RedirectAttributes ra )
    {
        try
        {
            boolean nuevo = id == null || id == 0;
            Usuario usuario = nuevo ? new Usuario( ) : service.findById( id );
            if( usuario == null )
            {
                throw new IllegalArgumentException( "El usuario no existe." );
            }
            boolean activo = !"0".equals( estatus );
            if( !nuevo && !activo && usuario.getId( ).equals( actual.getUsuario( ).getId( ) ) )
            {
                throw new IllegalArgumentException( "No puedes dar de baja tu propia cuenta." );
            }
            usuario.setNombre( nombre );
            usuario.setCorreo( correo );
            usuario.setRol( rolService.findById( rolId ) );
            usuario.setEstatus( activo );
            service.guardar( usuario, password, clienteId );
            ok( ra, nuevo ? "Usuario creado correctamente." : "Usuario actualizado correctamente." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/usuarios";
    }

    @PostMapping( "/eliminar" )
    public String baja( @RequestParam Long id, @AuthenticationPrincipal UsuarioPrincipal actual, RedirectAttributes ra )
    {
        try
        {
            if( id.equals( actual.getUsuario( ).getId( ) ) )
            {
                throw new IllegalArgumentException( "No puedes dar de baja tu propia cuenta." );
            }
            service.baja( id );
            ok( ra, "Usuario dado de baja (puedes reactivarlo editándolo)." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/usuarios";
    }
}
