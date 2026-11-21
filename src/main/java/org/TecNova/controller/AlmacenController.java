package org.TecNova.controller;

import org.TecNova.service.ReporteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AlmacenController
{
    private final ReporteService reporteService;

    public AlmacenController( ReporteService reporteService )
    {
        this.reporteService = reporteService;
    }

    @GetMapping( "/almacen" )
    public String listar( @RequestParam( defaultValue = "" ) String filtro, @RequestParam( defaultValue = "" ) String q, Model model )
    {
        model.addAttribute( "items", reporteService.almacen( filtro, q ) );
        model.addAttribute( "filtro", filtro );
        model.addAttribute( "q", q );
        return "almacen";
    }
}
