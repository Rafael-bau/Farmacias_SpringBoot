package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.ItemCarrito;
import org.TecNova.model.Venta;
import org.TecNova.service.ReporteService;
import org.TecNova.service.VentaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Punto de venta: carrito en sesión (RF-06) y cobro transaccional (RF-07). */
@Controller
@RequestMapping( "/pos" )
public class PosController extends BaseController
{
    private final ReporteService reporteService;
    private final VentaService ventaService;

    public PosController( ReporteService reporteService, VentaService ventaService )
    {
        this.reporteService = reporteService;
        this.ventaService = ventaService;
    }

    @SuppressWarnings( "unchecked" )
    private List<ItemCarrito> carrito( HttpSession session )
    {
        List<ItemCarrito> carrito = (List<ItemCarrito>) session.getAttribute( "carrito" );
        if( carrito == null )
        {
            carrito = new ArrayList<>( );
            session.setAttribute( "carrito", carrito );
        }
        return carrito;
    }

    private ItemCarrito buscar( List<ItemCarrito> carrito, Long idLote )
    {
        for( ItemCarrito item : carrito )
        {
            if( item.getIdLote( ).equals( idLote ) )
            {
                return item;
            }
        }
        return null;
    }

    @GetMapping
    public String pos( @RequestParam( defaultValue = "" ) String q, HttpSession session, Model model )
    {
        List<ItemCarrito> carrito = carrito( session );
        BigDecimal subtotal = BigDecimal.ZERO;
        for( ItemCarrito item : carrito )
        {
            subtotal = subtotal.add( item.getSubtotal( ) );
        }
        BigDecimal tasa = ventaService.getTasaIva( );
        BigDecimal iva = subtotal.multiply( tasa ).setScale( 2, RoundingMode.HALF_UP );
        model.addAttribute( "lotes", reporteService.lotesDisponibles( q ) );
        model.addAttribute( "carrito", carrito );
        model.addAttribute( "subtotal", subtotal );
        model.addAttribute( "iva", iva );
        model.addAttribute( "total", subtotal.add( iva ) );
        model.addAttribute( "tasaPorcentaje", tasa.multiply( BigDecimal.valueOf( 100 ) ).setScale( 0, RoundingMode.HALF_UP ) );
        model.addAttribute( "q", q );
        Object ultima = session.getAttribute( "ultimaVenta" );
        if( ultima != null )
        {
            model.addAttribute( "ultimaVenta", ultima );
            session.removeAttribute( "ultimaVenta" );
        }
        return "pos";
    }

    @PostMapping
    public String accion( @RequestParam String accion, @RequestParam( defaultValue = "" ) String q,
                          @RequestParam( required = false ) Long idLote, @RequestParam( required = false ) Integer cantidad,
                          @RequestParam( required = false ) String telefonoCliente,
                          @RequestParam( required = false ) String metodoPago,
                          @AuthenticationPrincipal UsuarioPrincipal actual, HttpSession session, RedirectAttributes ra )
    {
        List<ItemCarrito> carrito = carrito( session );
        try
        {
            switch( accion )
            {
                case "agregar":
                {
                    if( idLote == null || cantidad == null || cantidad <= 0 )
                    {
                        throw new IllegalArgumentException( "La cantidad debe ser mayor a 0." );
                    }
                    Map<String, Object> lote = reporteService.lotePorId( idLote );
                    if( lote == null )
                    {
                        throw new IllegalArgumentException( "El lote ya no está disponible." );
                    }
                    int stock = ( (Number) lote.get( "stock_actual" ) ).intValue( );
                    ItemCarrito existente = buscar( carrito, idLote );
                    int enCarrito = existente == null ? 0 : existente.getCantidad( );
                    if( enCarrito + cantidad > stock )
                    {
                        throw new IllegalArgumentException( "Solo hay " + stock + " unidades disponibles en ese lote." );
                    }
                    if( existente != null )
                    {
                        existente.setCantidad( enCarrito + cantidad );
                    }
                    else
                    {
                        carrito.add( new ItemCarrito( idLote, (String) lote.get( "nombre_comercial" ),
                                (String) lote.get( "num_lote" ), (BigDecimal) lote.get( "precio_venta" ), cantidad, stock ) );
                    }
                    break;
                }
                case "cantidad":
                {
                    ItemCarrito item = idLote == null ? null : buscar( carrito, idLote );
                    if( item != null && cantidad != null )
                    {
                        if( cantidad <= 0 )
                        {
                            carrito.remove( item );
                        }
                        else if( cantidad > item.getStock( ) )
                        {
                            throw new IllegalArgumentException( "Solo hay " + item.getStock( ) + " unidades en ese lote." );
                        }
                        else
                        {
                            item.setCantidad( cantidad );
                        }
                    }
                    break;
                }
                case "quitar":
                {
                    ItemCarrito item = idLote == null ? null : buscar( carrito, idLote );
                    if( item != null )
                    {
                        carrito.remove( item );
                    }
                    break;
                }
                case "vaciar":
                    carrito.clear( );
                    break;
                case "cobrar":
                {
                    Venta venta = ventaService.registrar( actual.getUsuario( ).getId( ), telefonoCliente, metodoPago, carrito );
                    carrito.clear( );
                    session.setAttribute( "ultimaVenta", venta );
                    break;
                }
                default:
                    break;
            }
        }
        catch( RuntimeException e )
        {
            error( ra, e );
        }
        return "redirect:/pos" + ( q.isEmpty( ) ? "" : "?q=" + URLEncoder.encode( q, StandardCharsets.UTF_8 ) );
    }
}
