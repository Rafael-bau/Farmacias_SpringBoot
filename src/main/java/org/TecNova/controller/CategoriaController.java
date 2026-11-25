package org.TecNova.controller;

import org.TecNova.model.Categoria;
import org.TecNova.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping( "/categorias" )
public class CategoriaController extends BaseController
{

    private final CategoriaService service;

    public CategoriaController( CategoriaService service )
    {
        this.service = service;
    }

    @GetMapping
    public String listar( Model model )
    {
        model.addAttribute( "categorias", service.findAll( ) );
        return "categorias";
    }

    @PostMapping( "/guardar" )
    public String guardar( @RequestParam( required = false ) Long id, @RequestParam String nombre, RedirectAttributes ra )
    {
        try
        {
            Categoria categoria = id == null || id == 0 ? new Categoria( ) : service.findById( id );
            if( categoria == null )
            {
                throw new IllegalArgumentException( "La categoría no existe." );
            }
            categoria.setNombre( nombre );
            service.guardar( categoria );
            ok( ra, id == null || id == 0 ? "Categoría creada." : "Categoría actualizada." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/categorias";
    }

    @PostMapping( "/eliminar" )
    public String eliminar( @RequestParam Long id, RedirectAttributes ra )
    {
        try
        {
            service.eliminar( id );
            ok( ra, "Categoría eliminada." );
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/categorias";
    }
}
