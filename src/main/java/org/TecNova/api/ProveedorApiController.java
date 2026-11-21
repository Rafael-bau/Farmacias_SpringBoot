package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.model.Proveedor;
import org.TecNova.service.ProveedorService;
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
@RequestMapping( "/api/proveedores" )
@Tag( name = "Proveedores" )
public class ProveedorApiController
{

    private final ProveedorService service;

    public ProveedorApiController( ProveedorService service )
    {
        this.service = service;
    }

    private Proveedor llenar( Proveedor p, ProveedorReq body )
    {
        p.setNombre( body.nombre( ) );
        p.setTelefono( body.telefono( ) );
        p.setCorreo( body.correo( ) );
        p.setDireccion( body.direccion( ) );
        p.setEstatus( body.estatus( ) == null ? Boolean.TRUE : body.estatus( ) );
        return p;
    }

    @GetMapping
    @Operation( summary = "Lista los proveedores" )
    public List<ProveedorRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::proveedor ).toList( );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Obtiene un proveedor por su id" )
    public ProveedorRes obtener( @PathVariable Long id )
    {
        return ApiMapper.proveedor( ApiMapper.requerido( service.findById( id ), "El proveedor no existe." ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra un proveedor" )
    public ProveedorRes crear( @RequestBody ProveedorReq body )
    {
        return ApiMapper.proveedor( service.guardar( llenar( new Proveedor( ), body ) ) );
    }

    @PutMapping( "/{id}" )
    @Operation( summary = "Actualiza un proveedor" )
    public ProveedorRes actualizar( @PathVariable Long id, @RequestBody ProveedorReq body )
    {
        Proveedor p = ApiMapper.requerido( service.findById( id ), "El proveedor no existe." );
        return ApiMapper.proveedor( service.guardar( llenar( p, body ) ) );
    }

    @DeleteMapping( "/{id}" )
    @Operation( summary = "Da de baja un proveedor (baja lógica)" )
    public MensajeRes baja( @PathVariable Long id )
    {
        service.baja( id );
        return new MensajeRes( "Proveedor dado de baja." );
    }
}
