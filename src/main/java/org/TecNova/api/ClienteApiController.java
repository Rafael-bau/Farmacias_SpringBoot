package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.model.Cliente;
import org.TecNova.service.ClienteService;
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
@RequestMapping( "/api/clientes" )
@Tag( name = "Clientes (programa de lealtad)" )
public class ClienteApiController
{

    private final ClienteService service;

    public ClienteApiController( ClienteService service )
    {
        this.service = service;
    }

    private Cliente llenar( Cliente c, ClienteReq body )
    {
        c.setNombre( body.nombre( ) );
        c.setTelefono( body.telefono( ) );
        c.setCorreo( body.correo( ) );
        return c;
    }

    @GetMapping
    @Operation( summary = "Lista o busca clientes por nombre, teléfono o correo" )
    public List<ClienteRes> listar( @RequestParam( defaultValue = "" ) String q )
    {
        return service.buscar( q ).stream( ).map( ApiMapper::cliente ).toList( );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Obtiene un cliente por su id" )
    public ClienteRes obtener( @PathVariable Long id )
    {
        return ApiMapper.cliente( ApiMapper.requerido( service.findById( id ), "El cliente no existe." ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra un cliente (validación con expresiones regulares, RF-10)" )
    public ClienteRes crear( @RequestBody ClienteReq body )
    {
        return ApiMapper.cliente( service.guardar( llenar( new Cliente( ), body ) ) );
    }

    @PutMapping( "/{id}" )
    @Operation( summary = "Actualiza un cliente" )
    public ClienteRes actualizar( @PathVariable Long id, @RequestBody ClienteReq body )
    {
        Cliente c = ApiMapper.requerido( service.findById( id ), "El cliente no existe." );
        return ApiMapper.cliente( service.guardar( llenar( c, body ) ) );
    }

    @DeleteMapping( "/{id}" )
    @Operation( summary = "Elimina un cliente (solo administrador; sus ventas se conservan)" )
    public MensajeRes eliminar( @PathVariable Long id )
    {
        service.eliminar( id );
        return new MensajeRes( "Cliente eliminado." );
    }
}
