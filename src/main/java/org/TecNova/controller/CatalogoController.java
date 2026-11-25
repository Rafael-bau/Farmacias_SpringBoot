package org.TecNova.controller;

import org.TecNova.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogoController
{
    private final ReporteService reporteService;

    public CatalogoController( ReporteService reporteService )
    {
        this.reporteService = reporteService;
    }

    @GetMapping( "/catalogos" )
    public String listar( @RequestParam( defaultValue = "" ) String q, Model model )
    {
        model.addAttribute( "productos", reporteService.catalogo( q ) );
        model.addAttribute( "q", q );
        return "catalogos";
    }
}
