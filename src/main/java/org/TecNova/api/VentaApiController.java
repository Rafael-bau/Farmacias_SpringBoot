package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.ItemCarrito;
import org.TecNova.model.Venta;
import org.TecNova.service.ReporteService;
import org.TecNova.service.VentaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Punto de venta: registrar ventas (transacción con commit/rollback), cancelarlas y descargar el ticket PDF. */
@RestController
@RequestMapping( "/api/ventas" )
@Tag( name = "Ventas (punto de venta)" )
public class VentaApiController
{
    private final VentaService service;
    private final ReporteService reporteService;

    public VentaApiController( VentaService service, ReporteService reporteService )
    {
        this.service = service;
        this.reporteService = reporteService;
    }

    /** Solo el administrador, el cajero dueño de la venta o el cliente de la venta pueden consultarla. */
    private Venta autorizada( Long id, UsuarioPrincipal actual )
    {
        Venta venta = ApiMapper.requerido( service.findById( id ), "La venta no existe." );
        String rol = actual.getUsuario( ).getRol( ).getNombre( );
        boolean permitido = "Administrador".equals( rol )
                || ( "Cajero".equals( rol ) && venta.getUsuario( ).getId( ).equals( actual.getUsuario( ).getId( ) ) )
                || ( "Cliente".equals( rol ) && actual.getClienteId( ) != null && venta.getCliente( ) != null
                     && venta.getCliente( ).getId( ).equals( actual.getClienteId( ) ) );
        if( !permitido )
        {
            throw new ResponseStatusException( HttpStatus.FORBIDDEN, "No tienes permiso para ver esta venta." );
        }
        return venta;
    }

    @GetMapping
    @Operation( summary = "Historial de ventas: el administrador ve todas, el cajero solo las suyas" )
    public List<VentaRes> historial( @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        return service.historial( actual.getUsuario( ).getId( ), actual.esAdmin( ) ).stream( )
                .map( v -> ApiMapper.venta( v, null ) ).toList( );
    }

    @GetMapping( "/mis-compras" )
    @Operation( summary = "Compras del cliente autenticado (portal del cliente)" )
    public List<VentaRes> misCompras( @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        if( actual.getClienteId( ) == null )
        {
            throw new IllegalArgumentException( "Tu cuenta aún no está vinculada a un cliente." );
        }
        List<VentaRes> compras = new ArrayList<>( );
        for( Map<String, Object> fila : service.comprasDeCliente( actual.getClienteId( ) ) )
        {
            Venta venta = (Venta) fila.get( "venta" );
            compras.add( ApiMapper.venta( venta, service.detalle( venta.getId( ) ) ) );
        }
        return compras;
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Detalle de una venta con sus partidas" )
    public VentaRes obtener( @PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        Venta venta = autorizada( id, actual );
        return ApiMapper.venta( venta, service.detalle( id ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra una venta. Descuenta stock y suma puntos al cliente (si se indica su teléfono). "
            + "Si algo falla se revierte todo (RF-07)" )
    public VentaRes registrar( @RequestBody VentaReq body, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        if( body.items( ) == null || body.items( ).isEmpty( ) )
        {
            throw new IllegalArgumentException( "La venta no tiene productos." );
        }
        List<ItemCarrito> items = new ArrayList<>( );
        for( ItemVentaReq i : body.items( ) )
        {
            if( i.idLote( ) == null || i.cantidad( ) == null || i.cantidad( ) <= 0 )
            {
                throw new IllegalArgumentException( "Cada producto necesita idLote y una cantidad mayor a 0." );
            }
            Map<String, Object> lote = reporteService.lotePorId( i.idLote( ) );
            if( lote == null )
            {
                throw new IllegalArgumentException( "El lote " + i.idLote( ) + " no está disponible." );
            }
            items.add( new ItemCarrito( i.idLote( ), (String) lote.get( "nombre_comercial" ), (String) lote.get( "num_lote" ),
                    (BigDecimal) lote.get( "precio_venta" ), i.cantidad( ), ( (Number) lote.get( "stock_actual" ) ).intValue( ) ) );
        }
        Venta venta = service.registrar( actual.getUsuario( ).getId( ), body.telefonoCliente( ), body.metodoPago( ), items );
        return ApiMapper.venta( venta, service.detalle( venta.getId( ) ) );
    }

    @PostMapping( "/{id}/cancelar" )
    @Operation( summary = "Cancela una venta (solo administrador): devuelve el stock y revierte los puntos" )
    public MensajeRes cancelar( @PathVariable Long id )
    {
        service.cancelar( id );
        return new MensajeRes( "Venta cancelada: el stock y los puntos fueron revertidos." );
    }

    @GetMapping( value = "/{id}/ticket", produces = MediaType.APPLICATION_PDF_VALUE )
    @Operation( summary = "Descarga el ticket de la venta en PDF (RF-08)" )
    public ResponseEntity<byte[]> ticket( @PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        Venta venta = autorizada( id, actual );
        HttpHeaders headers = new HttpHeaders( );
        headers.setContentType( MediaType.APPLICATION_PDF );
        headers.set( HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ticket-" + venta.getFolioTicket( ) + ".pdf\"" );
        return new ResponseEntity<>( service.ticketPdf( id ), headers, HttpStatus.OK );
    }
}
