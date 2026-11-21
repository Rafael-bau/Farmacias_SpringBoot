package org.TecNova.controller;

import org.TecNova.model.Medicamento;
import org.TecNova.service.CategoriaService;
import org.TecNova.service.MedicamentoService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/medicamentos" )
public class MedicamentoController extends BaseController
{

    private final MedicamentoService service;
    private final CategoriaService categoriaService;

    public MedicamentoController( MedicamentoService service, CategoriaService categoriaService )
    {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar( Model model )
    {
        model.addAttribute( "medicamentos", service.findAll( ) );
        model.addAttribute( "categorias", categoriaService.findAll( ) );
        return "medicamentos";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam( required = false ) Long id, @RequestParam String codigoBarras,
                           @RequestParam String nombreComercial, @RequestParam String sustanciaActiva,
                           @RequestParam BigDecimal precioVenta, @RequestParam Integer stockMinimo,
                           @RequestParam( required = false ) Long categoriaId,
                           @RequestParam( defaultValue = "1" ) String estatus, RedirectAttributes ra )
    {
        try
        {
            boolean nuevo = id == null || id == 0;
            Medicamento m = nuevo ? new Medicamento( ) : service.findById( id );
            if( m == null )
            {
                throw new IllegalArgumentException( "El medicamento no existe." );
            }
            m.setCodigoBarras( codigoBarras );
            m.setNombreComercial( nombreComercial );
            m.setSustanciaActiva( sustanciaActiva );
            m.setPrecioVenta( precioVenta );
            m.setStockMinimo( stockMinimo );
            m.setCategoria( categoriaService.findById( categoriaId ) );
            m.setEstatus( !"0".equals( estatus ) );
            service.guardar( m );
            ok( ra, nuevo ? "Medicamento registrado correctamente." : "Medicamento actualizado correctamente." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/medicamentos";
    }

    @PostMapping( "/eliminar" )
    public String baja( @RequestParam Long id, RedirectAttributes ra )
    {
        try
        {
            service.baja( id );
            ok( ra, "Medicamento dado de baja (puedes reactivarlo editándolo)." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/medicamentos";
    }
}
