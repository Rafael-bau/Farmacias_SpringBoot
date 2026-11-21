package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.TecNova.api.ApiDtos.AlertasRes;
import org.TecNova.api.ApiDtos.PerfilRes;
import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.ReporteService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Consultas de solo lectura: inventario por lote, almacén, catálogo, alertas y perfil de la sesión. */
@RestController
@RequestMapping( "/api" )
@Tag( name = "Inventario y consultas" )
public class ConsultaApiController
{
    private final ReporteService service;

    public ConsultaApiController( ReporteService service )
    {
        this.service = service;
    }

    @GetMapping( "/auth/me" )
    @Operation( summary = "Datos del usuario autenticado (sirve para probar el botón Authorize)" )
    public PerfilRes yo( @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        return new PerfilRes( actual.getUsuario( ).getId( ), actual.getUsuario( ).getNombre( ),
                actual.getUsuario( ).getCorreo( ), actual.getUsuario( ).getRol( ).getNombre( ), actual.getClienteId( ) );
    }

    @GetMapping( "/alertas" )
    @Operation( summary = "Cantidad de alertas: por caducar (3 meses o menos, RF-04) y stock bajo (RF-05)" )
    public AlertasRes alertas( )
    {
        return new AlertasRes( service.totalCaducidad( ), service.totalStockBajo( ) );
    }

    @GetMapping( "/inventario" )
    @Operation( summary = "Inventario por lote con su estado (CADUCADO, POR CADUCAR, STOCK BAJO, VIGENTE)" )
    public List<Map<String, Object>> inventario(
            @RequestParam( defaultValue = "" ) String filtro,
            @RequestParam( defaultValue = "" ) String q )
    {
        return service.inventario( filtro, q );
    }

    @GetMapping( "/almacen" )
    @Operation( summary = "Existencias consolidadas por medicamento. filtro=stock muestra agotados y stock bajo" )
    public List<Map<String, Object>> almacen(
            @RequestParam( defaultValue = "" ) String filtro,
            @RequestParam( defaultValue = "" ) String q )
    {
        return service.almacen( filtro, q );
    }

    @GetMapping( "/catalogo" )
    @Operation( summary = "Catálogo de medicamentos activos con precio y disponibilidad" )
    public List<Map<String, Object>> catalogo( @RequestParam( defaultValue = "" ) String q )
    {
        return service.catalogo( q );
    }
}
