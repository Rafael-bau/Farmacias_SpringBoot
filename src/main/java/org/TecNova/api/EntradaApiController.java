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
import org.TecNova.service.EntradaService;

@RestController
@RequestMapping( "/api/entradas" )
@Tag( name = "Entradas (abastecimiento)" )
public class EntradaApiController
{
    private final EntradaService service;

    public EntradaApiController( EntradaService service )
    {
        this.service = service;
    }

    @GetMapping
    @Operation( summary = "Últimas 100 entradas de mercancía" )
    public List<EntradaRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::entrada ).toList( );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra la recepción de un lote: crea el lote o suma stock si ya existe" )
    public EntradaRes registrar( @RequestBody EntradaReq body, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        if( body.cantidad( ) == null )
        {
            throw new IllegalArgumentException( "La cantidad debe ser un número entero." );
        }
        return ApiMapper.entrada( service.registrar( body.proveedorId( ), body.medicamentoId( ), body.numLote( ),
                body.fechaCaducidad( ), body.cantidad( ), body.costoUnitario( ), actual.getUsuario( ).getId( ) ) );
    }
}
