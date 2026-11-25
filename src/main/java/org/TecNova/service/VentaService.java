package org.TecNova.service;

import org.TecNova.model.Cliente;
import org.TecNova.model.DetalleVenta;
import org.TecNova.model.ItemCarrito;
import org.TecNova.model.Lote;
import org.TecNova.model.Medicamento;
import org.TecNova.model.Venta;
import org.TecNova.repository.ClienteRepository;
import org.TecNova.repository.DetalleVentaRepository;
import org.TecNova.repository.LoteRepository;
import org.TecNova.repository.UsuarioRepository;
import org.TecNova.repository.VentaRepository;
import org.TecNova.util.TicketPdf;
import org.TecNova.util.Validador;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class VentaService
{
    private static final Set<String> METODOS = Set.of( "EFECTIVO", "TARJETA", "TRANSFERENCIA" );

    private final VentaRepository ventas;
    private final DetalleVentaRepository detalles;
    private final LoteRepository lotes;
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final BigDecimal tasaIva;

    public VentaService( VentaRepository ventas, DetalleVentaRepository detalles, LoteRepository lotes,
                         ClienteRepository clientes, UsuarioRepository usuarios,
                         @Value( "${app.iva-tasa:0.00}" ) BigDecimal tasaIva )
    {
        this.ventas = ventas;
        this.detalles = detalles;
        this.lotes = lotes;
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.tasaIva = tasaIva;
    }

    public BigDecimal getTasaIva( )
    {
        return tasaIva;
    }

    public Venta findById( Long id )
    {
        return id == null ? null : ventas.findById( id ).orElse( null );
    }

    /** El administrador ve todas las ventas; el cajero solo las suyas. */
    public List<Venta> historial( Long idUsuario, boolean admin )
    {
        return admin ? ventas.findTop200ByOrderByFechaVentaDescIdDesc( )
                     : ventas.findTop200ByUsuarioIdOrderByFechaVentaDescIdDesc( idUsuario );
    }

    public List<DetalleVenta> detalle( Long idVenta )
    {
        return detalles.findByVentaIdOrderByIdAsc( idVenta );
    }

    /**
     * RF-06 / RF-07: cobra el carrito en UNA transacción (@Transactional): commit si todo sale bien, rollback
     * si algo falla. El descuento de stock y la suma de puntos los hacen los triggers de la base de datos.
     */
    @Transactional
    public Venta registrar( Long idCajero, String telefonoCliente, String metodoPago, List<ItemCarrito> items )
    {
        if( items == null || items.isEmpty( ) )
        {
            throw new IllegalArgumentException( "El carrito está vacío." );
        }
        if( !METODOS.contains( metodoPago ) )
        {
            throw new IllegalArgumentException( "Selecciona un método de pago válido." );
        }
        Cliente cliente = null;
        if( telefonoCliente != null && !telefonoCliente.isBlank( ) )
        {
            if( !Validador.telefono( telefonoCliente.trim( ) ) )
            {
                throw new IllegalArgumentException( "Teléfono del cliente inválido: deben ser 10 dígitos." );
            }
            cliente = clientes.findByTelefono( telefonoCliente.trim( ) ).orElseThrow( ( ) ->
                    new IllegalArgumentException( "No existe un cliente con ese teléfono. Regístralo en Catálogos › Clientes." ) );
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        List<DetalleVenta> partidas = new ArrayList<>( );
        for( ItemCarrito item : items )
        {
            Lote lote = lotes.findByIdForUpdate( item.getIdLote( ) )
                    .orElseThrow( ( ) -> new IllegalArgumentException( "Uno de los lotes ya no existe." ) );
            Medicamento medicamento = lote.getMedicamento( );
            if( !Boolean.TRUE.equals( medicamento.getEstatus( ) ) )
            {
                throw new IllegalArgumentException( medicamento.getNombreComercial( ) + " está dado de baja." );
            }
            if( lote.getFechaCaducidad( ).isBefore( LocalDate.now( ) ) )
            {
                throw new IllegalArgumentException( "Lote caducado: " + medicamento.getNombreComercial( ) + "." );
            }
            if( lote.getStockActual( ) < item.getCantidad( ) )
            {
                throw new IllegalArgumentException( "Stock insuficiente de " + medicamento.getNombreComercial( )
                        + " (disponibles: " + lote.getStockActual( ) + ")." );
            }
            DetalleVenta d = new DetalleVenta( );
            d.setLote( lote );
            d.setCantidad( item.getCantidad( ) );
            d.setPrecioUnitario( medicamento.getPrecioVenta( ) );
            d.setSubtotalLinea( medicamento.getPrecioVenta( ).multiply( BigDecimal.valueOf( item.getCantidad( ) ) ) );
            subtotal = subtotal.add( d.getSubtotalLinea( ) );
            partidas.add( d );
        }
        subtotal = subtotal.setScale( 2, RoundingMode.HALF_UP );
        BigDecimal iva = subtotal.multiply( tasaIva ).setScale( 2, RoundingMode.HALF_UP );
        BigDecimal total = subtotal.add( iva );

        Venta venta = new Venta( );
        venta.setFolioTicket( UUID.randomUUID( ).toString( ).replace( "-", "" ).substring( 0, 20 ) );
        venta.setSubtotal( subtotal );
        venta.setIva( iva );
        venta.setTotal( total );
        venta.setMetodoPago( metodoPago );
        venta.setPuntosGenerados( cliente == null ? 0 : total.divideToIntegralValue( BigDecimal.TEN ).intValue( ) );
        venta.setEstatusVenta( "COMPLETADA" );
        venta.setUsuario( usuarios.getReferenceById( idCajero ) );
        venta.setCliente( cliente );
        venta = ventas.saveAndFlush( venta );
        venta.setFolioTicket( "V" + LocalDate.now( ).format( DateTimeFormatter.ofPattern( "yyMMdd" ) )
                + String.format( "%06d", venta.getId( ) ) );

        for( DetalleVenta d : partidas )
        {
            d.setVenta( venta );
            detalles.save( d );
        }
        detalles.flush( );
        return venta;
    }

    /** Cancela la venta: devuelve el stock, revierte los puntos y conserva el historial. */
    @Transactional
    public void cancelar( Long idVenta )
    {
        Venta venta = ventas.findByIdForUpdate( idVenta ).orElseThrow( ( ) -> new IllegalArgumentException( "La venta no existe." ) );
        if( "CANCELADA".equals( venta.getEstatusVenta( ) ) )
        {
            throw new IllegalArgumentException( "La venta ya estaba cancelada." );
        }
        for( DetalleVenta d : detalles.findByVentaIdOrderByIdAsc( idVenta ) )
        {
            Lote lote = lotes.findByIdForUpdate( d.getLote( ).getId( ) ).orElseThrow( );
            lote.setStockActual( lote.getStockActual( ) + d.getCantidad( ) );
        }
        if( venta.getCliente( ) != null )
        {
            Cliente cliente = clientes.findById( venta.getCliente( ).getId( ) ).orElseThrow( );
            cliente.setPuntosAcumulados( Math.max( 0, cliente.getPuntosAcumulados( ) - venta.getPuntosGenerados( ) ) );
        }
        venta.setEstatusVenta( "CANCELADA" );
    }

    /** Compras de un cliente con un resumen de artículos (portal del cliente). */
    public List<Map<String, Object>> comprasDeCliente( Long idCliente )
    {
        List<Map<String, Object>> resultado = new ArrayList<>( );
        for( Venta venta : ventas.findTop60ByClienteIdOrderByFechaVentaDescIdDesc( idCliente ) )
        {
            StringBuilder articulos = new StringBuilder( );
            for( DetalleVenta d : detalles.findByVentaIdOrderByIdAsc( venta.getId( ) ) )
            {
                if( articulos.length( ) > 0 )
                {
                    articulos.append( ", " );
                }
                articulos.append( d.getCantidad( ) ).append( " x " ).append( d.getLote( ).getMedicamento( ).getNombreComercial( ) );
            }
            Map<String, Object> fila = new LinkedHashMap<>( );
            fila.put( "venta", venta );
            fila.put( "articulos", articulos.toString( ) );
            resultado.add( fila );
        }
        return resultado;
    }

    /** RF-08: ticket digital en PDF. */
    public byte[] ticketPdf( Long idVenta )
    {
        Venta venta = findById( idVenta );
        if( venta == null )
        {
            throw new IllegalArgumentException( "La venta no existe." );
        }
        Map<String, Object> v = new LinkedHashMap<>( );
        v.put( "folio_ticket", venta.getFolioTicket( ) );
        v.put( "fecha_venta", venta.getFechaVenta( ) == null ? null : Timestamp.valueOf( venta.getFechaVenta( ) ) );
        v.put( "cajero", venta.getUsuario( ).getNombre( ) );
        v.put( "cliente", venta.getCliente( ) == null ? null : venta.getCliente( ).getNombre( ) );
        v.put( "id_cliente", venta.getCliente( ) == null ? null : venta.getCliente( ).getId( ) );
        v.put( "subtotal", venta.getSubtotal( ) );
        v.put( "iva", venta.getIva( ) );
        v.put( "total", venta.getTotal( ) );
        v.put( "metodo_pago", venta.getMetodoPago( ) );
        v.put( "puntos_generados", venta.getPuntosGenerados( ) );
        v.put( "estatus_venta", venta.getEstatusVenta( ) );
        List<Map<String, Object>> partidas = new ArrayList<>( );
        for( DetalleVenta d : detalles.findByVentaIdOrderByIdAsc( idVenta ) )
        {
            Map<String, Object> fila = new LinkedHashMap<>( );
            fila.put( "nombre_comercial", d.getLote( ).getMedicamento( ).getNombreComercial( ) );
            fila.put( "cantidad", d.getCantidad( ) );
            fila.put( "precio_unitario", d.getPrecioUnitario( ) );
            fila.put( "subtotal_linea", d.getSubtotalLinea( ) );
            partidas.add( fila );
        }
        return TicketPdf.generar( v, partidas );
    }
}
