package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.ClienteService;
import org.TecNova.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Mi cuenta: perfil y cambio de contraseña (todos los roles). */
@Controller
@RequestMapping( "/configuracion" )
public class ConfiguracionController extends BaseController
{
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    public ConfiguracionController( UsuarioService usuarioService, ClienteService clienteService )
    {
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    @GetMapping
    public String configuracion( @AuthenticationPrincipal UsuarioPrincipal actual, Model model )
    {
        if( actual.getClienteId( ) != null )
        {
            model.addAttribute( "perfilCliente", clienteService.findById( actual.getClienteId( ) ) );
        }
        return "configuracion";
    }

    @PostMapping( "/password" )
    public String cambiarPassword( @RequestParam String actual, @RequestParam String nueva, @RequestParam String confirmar,
                                   @AuthenticationPrincipal UsuarioPrincipal principal, RedirectAttributes ra )
    {
        try
        {
            if( !nueva.equals( confirmar ) )
            {
                throw new IllegalArgumentException( "La confirmación no coincide con la nueva contraseña." );
            }
            usuarioService.cambiarPassword( principal.getUsuario( ).getId( ), actual, nueva );
            ok( ra, "Contraseña actualizada correctamente." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/configuracion";
    }
}
