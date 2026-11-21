package org.TecNova.controller;

import org.TecNova.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InventarioController
{
    private final ReporteService reporteService;

    public InventarioController( ReporteService reporteService )
    {
        this.reporteService = reporteService;
    }

    @GetMapping( "/inventario" )
    public String listar( @RequestParam( defaultValue = "" ) String filtro, @RequestParam( defaultValue = "" ) String q, Model model )
    {
        model.addAttribute( "lotes", reporteService.inventario( filtro, q ) );
        model.addAttribute( "filtro", filtro );
        model.addAttribute( "q", q );
        return "inventario";
    }
}
