package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.config.UsuarioPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.TecNova.service.SalidaService;

@RestController
@RequestMapping( "/api/salidas" )
@Tag( name = "Salidas (merma, caducidad, ajustes)" )
public class SalidaApiController
{
    private final SalidaService service;

    public SalidaApiController( SalidaService service )
    {
        this.service = service;
    }

    @GetMapping
    @Operation( summary = "Últimas 100 salidas de inventario que no son ventas" )
    public List<SalidaRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::salida ).toList( );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra una salida. motivo: MERMA, CADUCIDAD, AJUSTE u OTRO" )
    public SalidaRes registrar( @RequestBody SalidaReq body, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        if( body.cantidad( ) == null )
        {
            throw new IllegalArgumentException( "La cantidad debe ser un número entero." );
        }
        return ApiMapper.salida( service.registrar( body.loteId( ), body.cantidad( ), body.motivo( ),
                body.observaciones( ), actual.getUsuario( ).getId( ) ) );
    }
}
