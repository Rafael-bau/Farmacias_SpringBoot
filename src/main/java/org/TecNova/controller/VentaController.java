package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.VentaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Historial de ventas: el cajero ve las suyas; el administrador ve todas y puede cancelar. */
@Controller
@RequestMapping( "/ventas" )
public class VentaController extends BaseController
{
    private final VentaService service;

    public VentaController( VentaService service )
    {
        this.service = service;
    }

    @GetMapping
    public String listar( @AuthenticationPrincipal UsuarioPrincipal actual, Model model )
    {
        model.addAttribute( "ventas", service.historial( actual.getUsuario( ).getId( ), actual.esAdmin( ) ) );
        return "ventas";
    }

    @PostMapping( "/cancelar" )
    public String cancelar( @RequestParam Long id, RedirectAttributes ra )
    {
        try
        {
            service.cancelar( id );
            ok( ra, "Venta cancelada: el stock y los puntos fueron revertidos." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/ventas";
    }
}
