package org.TecNova.controller;

import org.TecNova.model.Proveedor;
import org.TecNova.service.ProveedorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/proveedores" )
public class ProveedorController extends BaseController
{

    private final ProveedorService service;

    public ProveedorController( ProveedorService service )
    {
        this.service = service;
    }

    @GetMapping
    public String listar( Model model )
    {
        model.addAttribute( "proveedores", service.findAll( ) );
        return "proveedores";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam( required = false ) Long id, @RequestParam String nombre,
                           @RequestParam( required = false ) String telefono, @RequestParam( required = false ) String correo,
                           @RequestParam( required = false ) String direccion,
                           @RequestParam( defaultValue = "1" ) String estatus, RedirectAttributes ra )
    {
        try
        {
            boolean nuevo = id == null || id == 0;
            Proveedor p = nuevo ? new Proveedor( ) : service.findById( id );
            if( p == null )
            {
                throw new IllegalArgumentException( "El proveedor no existe." );
            }
            p.setNombre( nombre );
            p.setTelefono( telefono );
            p.setCorreo( correo );
            p.setDireccion( direccion );
            p.setEstatus( !"0".equals( estatus ) );
            service.guardar( p );
            ok( ra, nuevo ? "Proveedor registrado." : "Proveedor actualizado." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/proveedores";
    }

    @PostMapping( "/eliminar" )
    public String baja( @RequestParam Long id, RedirectAttributes ra )
    {
        try
        {
            service.baja( id );
            ok( ra, "Proveedor dado de baja (puedes reactivarlo editándolo)." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/proveedores";
    }
}
