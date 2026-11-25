package org.TecNova.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Cuerpos de entrada (Req) y de salida (Res) de la API REST. Evitan exponer entidades ni contraseñas. */
public final class ApiDtos
{
    private ApiDtos( )
    {
    }

    public record CategoriaReq( String nombre ) { }
    public record CategoriaRes( Long id, String nombre ) { }

    public record MedicamentoReq( String codigoBarras, String nombreComercial, String sustanciaActiva,
                                  BigDecimal precioVenta, Integer stockMinimo, Long categoriaId, Boolean estatus ) { }
    public record MedicamentoRes( Long id, String codigoBarras, String nombreComercial, String sustanciaActiva,
                                  BigDecimal precioVenta, Integer stockMinimo, Long categoriaId, String categoria,
                                  Boolean estatus ) { }

    public record ProveedorReq( String nombre, String telefono, String correo, String direccion, Boolean estatus ) { }
    public record ProveedorRes( Long id, String nombre, String telefono, String correo, String direccion, Boolean estatus ) { }

    public record ClienteReq( String nombre, String telefono, String correo ) { }
    public record ClienteRes( Long id, String nombre, String telefono, String correo, Integer puntosAcumulados, Long usuarioId ) { }

    public record UsuarioReq( String nombre, String correo, String password, Long rolId, Long clienteId, Boolean estatus ) { }
    public record UsuarioRes( Long id, String nombre, String correo, Long rolId, String rol, Boolean estatus ) { }

    public record EntradaReq( Long proveedorId, Long medicamentoId, String numLote, LocalDate fechaCaducidad,
                              Integer cantidad, BigDecimal costoUnitario ) { }
    public record EntradaRes( Long id, String medicamento, String numLote, LocalDate fechaCaducidad, Integer cantidad,
                              BigDecimal costoUnitario, String proveedor, String registro, LocalDateTime fecha ) { }

    public record SalidaReq( Long loteId, Integer cantidad, String motivo, String observaciones ) { }
    public record SalidaRes( Long id, String medicamento, String numLote, Integer cantidad, String motivo,
                             String observaciones, String registro, LocalDateTime fecha ) { }

    public record ItemVentaReq( Long idLote, Integer cantidad ) { }
    public record VentaReq( String telefonoCliente, String metodoPago, List<ItemVentaReq> items ) { }
    public record DetalleRes( String medicamento, Integer cantidad, BigDecimal precioUnitario, BigDecimal subtotalLinea ) { }
    public record VentaRes( Long id, String folioTicket, LocalDateTime fecha, String cliente, String cajero, String metodoPago,
                            BigDecimal subtotal, BigDecimal iva, BigDecimal total, Integer puntosGenerados,
                            String estatus, List<DetalleRes> detalle ) { }

    public record AlertasRes( long porCaducar, long stockBajo ) { }
    public record PerfilRes( Long id, String nombre, String correo, String rol, Long clienteId ) { }
    public record MensajeRes( String mensaje ) { }
}
