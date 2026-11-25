package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.Cliente;
import org.TecNova.service.ClienteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/clientes" )
public class ClienteController extends BaseController
{

    private final ClienteService service;

    public ClienteController( ClienteService service )
    {
        this.service = service;
    }

    @GetMapping
    public String listar( @RequestParam( defaultValue = "" ) String q, @AuthenticationPrincipal UsuarioPrincipal actual, Model model )
    {
        model.addAttribute( "clientes", service.buscar( q ) );
        model.addAttribute( "q", q );
        model.addAttribute( "activo", actual.esAdmin( ) ? "clientes" : "catalogos" );
        return "clientes";
    }

    /** RF-09 / RF-10: alta y edición; la validación con expresiones regulares está en ClienteService. */
    @PostMapping( "/guardar" )
    public String guardar( @RequestParam( required = false ) Long id, @RequestParam String nombre,
                           @RequestParam String telefono, @RequestParam String correo, RedirectAttributes ra )
    {
        try
        {
            boolean nuevo = id == null || id == 0;
            Cliente cliente = nuevo ? new Cliente( ) : service.findById( id );
            if( cliente == null )
            {
                throw new IllegalArgumentException( "El cliente no existe." );
            }
            cliente.setNombre( nombre );
            cliente.setTelefono( telefono );
            cliente.setCorreo( correo );
            service.guardar( cliente );
            ok( ra, nuevo ? "Cliente registrado correctamente." : "Cliente actualizado correctamente." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/clientes";
    }

    @PostMapping( "/eliminar" )
    public String eliminar( @RequestParam Long id, RedirectAttributes ra )
    {
        try
        {
            service.eliminar( id );
            ok( ra, "Cliente eliminado. Sus ventas se conservan en el historial." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/clientes";
    }
}
