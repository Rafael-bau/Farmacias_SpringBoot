package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.model.Categoria;
import org.TecNova.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/categorias" )
@Tag( name = "Categorías" )
public class CategoriaApiController
{

    private final CategoriaService service;

    public CategoriaApiController( CategoriaService service )
    {
        this.service = service;
    }

    @GetMapping
    @Operation( summary = "Lista las categorías de medicamentos" )
    public List<CategoriaRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::categoria ).toList( );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Obtiene una categoría por su id" )
    public CategoriaRes obtener( @PathVariable Long id )
    {
        return ApiMapper.categoria( ApiMapper.requerido( service.findById( id ), "La categoría no existe." ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Crea una categoría" )
    public CategoriaRes crear( @RequestBody CategoriaReq body )
    {
        Categoria categoria = new Categoria( );
        categoria.setNombre( body.nombre( ) );
        return ApiMapper.categoria( service.guardar( categoria ) );
    }

    @PutMapping( "/{id}" )
    @Operation( summary = "Actualiza una categoría" )
    public CategoriaRes actualizar( @PathVariable Long id, @RequestBody CategoriaReq body )
    {
        Categoria categoria = ApiMapper.requerido( service.findById( id ), "La categoría no existe." );
        categoria.setNombre( body.nombre( ) );
        return ApiMapper.categoria( service.guardar( categoria ) );
    }

    @DeleteMapping( "/{id}" )
    @Operation( summary = "Elimina una categoría (falla si tiene medicamentos)" )
    public MensajeRes eliminar( @PathVariable Long id )
    {
        service.eliminar( id );
        return new MensajeRes( "Categoría eliminada." );
    }
}
