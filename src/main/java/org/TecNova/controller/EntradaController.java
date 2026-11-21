package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.EntradaService;
import org.TecNova.service.MedicamentoService;
import org.TecNova.service.ProveedorService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping( "/entradas" )
public class EntradaController extends BaseController
{
    private final EntradaService service;
    private final ProveedorService proveedorService;
    private final MedicamentoService medicamentoService;

    public EntradaController( EntradaService service, ProveedorService proveedorService, MedicamentoService medicamentoService )
    {
        this.service = service;
        this.proveedorService = proveedorService;
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public String listar( Model model )
    {
        model.addAttribute( "entradas", service.findAll( ) );
        model.addAttribute( "proveedores", proveedorService.findActivos( ) );
        model.addAttribute( "medicamentos", medicamentoService.findActivos( ) );
        return "entradas";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam Long proveedorId, @RequestParam Long medicamentoId, @RequestParam String numLote,
                           @RequestParam String fechaCaducidad, @RequestParam Integer cantidad,
                           @RequestParam( defaultValue = "0" ) BigDecimal costoUnitario,
                           @AuthenticationPrincipal UsuarioPrincipal actual, RedirectAttributes ra )
    {
        try
        {
            LocalDate caducidad;
            try
            {
                caducidad = LocalDate.parse( fechaCaducidad );
            }
            catch( DateTimeParseException e )
            {
                throw new IllegalArgumentException( "Indica una fecha de caducidad válida." );
            }
            service.registrar( proveedorId, medicamentoId, numLote, caducidad, cantidad, costoUnitario,
                    actual.getUsuario( ).getId( ) );
            ok( ra, "Entrada registrada: el inventario se actualizó." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/entradas";
    }
}
