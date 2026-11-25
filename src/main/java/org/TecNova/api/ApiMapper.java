package org.TecNova.api;

import org.TecNova.api.ApiDtos.*;
import org.TecNova.model.Categoria;
import org.TecNova.model.Cliente;
import org.TecNova.model.DetalleVenta;
import org.TecNova.model.Entrada;
import org.TecNova.model.Medicamento;
import org.TecNova.model.Proveedor;
import org.TecNova.model.Salida;
import org.TecNova.model.Usuario;
import org.TecNova.model.Venta;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/** Convierte entidades en respuestas de la API. */
final class ApiMapper
{
    private ApiMapper( )
    {
    }

    /** Devuelve el objeto o responde 404 si no existe. */
    static <T> T requerido( T objeto, String mensaje )
    {
        if( objeto == null )
        {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, mensaje );
        }
        return objeto;
    }

    static CategoriaRes categoria( Categoria c )
    {
        return new CategoriaRes( c.getId( ), c.getNombre( ) );
    }

    static MedicamentoRes medicamento( Medicamento m )
    {
        return new MedicamentoRes( m.getId( ), m.getCodigoBarras( ), m.getNombreComercial( ), m.getSustanciaActiva( ),
                m.getPrecioVenta( ), m.getStockMinimo( ), m.getCategoria( ).getId( ), m.getCategoria( ).getNombre( ),
                m.getEstatus( ) );
    }

    static ProveedorRes proveedor( Proveedor p )
    {
        return new ProveedorRes( p.getId( ), p.getNombre( ), p.getTelefono( ), p.getCorreo( ), p.getDireccion( ), p.getEstatus( ) );
    }

    static ClienteRes cliente( Cliente c )
    {
        return new ClienteRes( c.getId( ), c.getNombre( ), c.getTelefono( ), c.getCorreo( ), c.getPuntosAcumulados( ),
                c.getUsuario( ) == null ? null : c.getUsuario( ).getId( ) );
    }

    static UsuarioRes usuario( Usuario u )
    {
        return new UsuarioRes( u.getId( ), u.getNombre( ), u.getCorreo( ), u.getRol( ).getId( ), u.getRol( ).getNombre( ),
                u.getEstatus( ) );
    }

    static EntradaRes entrada( Entrada e )
    {
        return new EntradaRes( e.getId( ), e.getLote( ).getMedicamento( ).getNombreComercial( ), e.getLote( ).getNumLote( ),
                e.getLote( ).getFechaCaducidad( ), e.getCantidad( ), e.getCostoUnitario( ), e.getProveedor( ).getNombre( ),
                e.getUsuario( ).getNombre( ), e.getFecha( ) );
    }

    static SalidaRes salida( Salida s )
    {
        return new SalidaRes( s.getId( ), s.getLote( ).getMedicamento( ).getNombreComercial( ), s.getLote( ).getNumLote( ),
                s.getCantidad( ), s.getMotivo( ), s.getObservaciones( ), s.getUsuario( ).getNombre( ), s.getFecha( ) );
    }

    static VentaRes venta( Venta v, List<DetalleVenta> detalles )
    {
        List<DetalleRes> partidas = detalles == null ? null : detalles.stream( )
                .map( d -> new DetalleRes( d.getLote( ).getMedicamento( ).getNombreComercial( ), d.getCantidad( ),
                        d.getPrecioUnitario( ), d.getSubtotalLinea( ) ) ).toList( );
        return new VentaRes( v.getId( ), v.getFolioTicket( ), v.getFechaVenta( ),
                v.getCliente( ) == null ? null : v.getCliente( ).getNombre( ), v.getUsuario( ).getNombre( ),
                v.getMetodoPago( ), v.getSubtotal( ), v.getIva( ), v.getTotal( ), v.getPuntosGenerados( ),
                v.getEstatusVenta( ), partidas );
    }
}
