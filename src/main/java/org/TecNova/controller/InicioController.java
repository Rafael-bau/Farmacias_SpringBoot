package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.ReporteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Inicio: alertas de caducidad y stock bajo (personal) o novedades (cliente). */
@Controller
public class InicioController
{
    private final ReporteService reporteService;

    public InicioController( ReporteService reporteService )
    {
        this.reporteService = reporteService;
    }

    @GetMapping( "/" )
    public String inicio( @AuthenticationPrincipal UsuarioPrincipal principal, Model model )
    {
        boolean cliente = "Cliente".equals( principal.getUsuario( ).getRol( ).getNombre( ) );
        model.addAttribute( "esCliente", cliente );
        if( cliente )
        {
            model.addAttribute( "novedades", reporteService.novedades( 6 ) );
        }
        else
        {
            model.addAttribute( "alertasCaducidad", reporteService.totalCaducidad( ) );
            model.addAttribute( "alertasStock", reporteService.totalStockBajo( ) );
            model.addAttribute( "medicamentos", reporteService.resumenStock( 10 ) );
        }
        return "inicio";
    }
}
