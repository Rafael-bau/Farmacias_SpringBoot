package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.LoteService;
import org.TecNova.service.SalidaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/salidas" )
public class SalidaController extends BaseController
{
    private final SalidaService service;
    private final LoteService loteService;

    public SalidaController( SalidaService service, LoteService loteService )
    {
        this.service = service;
        this.loteService = loteService;
    }

    @GetMapping
    public String listar( Model model )
    {
        model.addAttribute( "salidas", service.findAll( ) );
        model.addAttribute( "lotes", loteService.findConStock( ) );
        return "salidas";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam Long loteId, @RequestParam Integer cantidad, @RequestParam String motivo,
                           @RequestParam( required = false ) String observaciones,
                           @AuthenticationPrincipal UsuarioPrincipal actual, RedirectAttributes ra )
    {
        try
        {
            service.registrar( loteId, cantidad, motivo, observaciones, actual.getUsuario( ).getId( ) );
            ok( ra, "Salida registrada: el inventario se actualizó." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/salidas";
    }
}
