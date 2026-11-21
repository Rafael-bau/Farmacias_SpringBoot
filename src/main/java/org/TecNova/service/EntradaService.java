package org.TecNova.service;

import org.TecNova.model.Entrada;
import org.TecNova.model.Lote;
import org.TecNova.model.Medicamento;
import org.TecNova.model.Proveedor;
import org.TecNova.model.Usuario;
import org.TecNova.repository.EntradaRepository;
import org.TecNova.repository.LoteRepository;
import org.TecNova.repository.MedicamentoRepository;
import org.TecNova.repository.ProveedorRepository;
import org.TecNova.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Abastecimiento: recepción de mercancía de un proveedor. */
@Service
public class EntradaService
{
    private final EntradaRepository entradas;
    private final LoteRepository lotes;
    private final ProveedorRepository proveedores;
    private final MedicamentoRepository medicamentos;
    private final UsuarioRepository usuarios;

    public EntradaService( EntradaRepository entradas, LoteRepository lotes, ProveedorRepository proveedores,
                           MedicamentoRepository medicamentos, UsuarioRepository usuarios )
    {
        this.entradas = entradas;
        this.lotes = lotes;
        this.proveedores = proveedores;
        this.medicamentos = medicamentos;
        this.usuarios = usuarios;
    }

    public List<Entrada> findAll( )
    {
        return entradas.findTop100ByOrderByFechaDescIdDesc( );
    }

    /** Crea el lote si no existe (o suma stock si ya existe) y guarda la entrada, todo en una transacción. */
    @Transactional
    public Entrada registrar( Long idProveedor, Long idMedicamento, String numLote, LocalDate caducidad,
                              int cantidad, BigDecimal costo, Long idUsuario )
    {
        if( numLote == null || numLote.isBlank( ) || numLote.length( ) > 50 )
        {
            throw new IllegalArgumentException( "El número de lote es obligatorio (máximo 50 caracteres)." );
        }
        if( cantidad <= 0 )
        {
            throw new IllegalArgumentException( "La cantidad debe ser mayor a 0." );
        }
        if( costo != null && costo.signum( ) < 0 )
        {
            throw new IllegalArgumentException( "El costo no puede ser negativo." );
        }
        if( caducidad == null || !caducidad.isAfter( LocalDate.now( ) ) )
        {
            throw new IllegalArgumentException( "La fecha de caducidad debe ser futura." );
        }
        Proveedor proveedor = proveedores.findById( idProveedor ).orElse( null );
        if( proveedor == null || !Boolean.TRUE.equals( proveedor.getEstatus( ) ) )
        {
            throw new IllegalArgumentException( "El proveedor no existe o está dado de baja." );
        }
        Medicamento medicamento = medicamentos.findById( idMedicamento ).orElse( null );
        if( medicamento == null || !Boolean.TRUE.equals( medicamento.getEstatus( ) ) )
        {
            throw new IllegalArgumentException( "El medicamento no existe o está dado de baja." );
        }
        Lote lote = lotes.findByMedicamentoIdAndNumLote( idMedicamento, numLote.trim( ) ).orElse( null );
        if( lote == null )
        {
            lote = new Lote( );
            lote.setMedicamento( medicamento );
            lote.setNumLote( numLote.trim( ) );
            lote.setFechaCaducidad( caducidad );
            lote.setStockActual( cantidad );
            lote = lotes.save( lote );
        }
        else
        {
            if( !lote.getFechaCaducidad( ).equals( caducidad ) )
            {
                throw new IllegalArgumentException( "Ese lote ya existe con otra fecha de caducidad." );
            }
            lote.setStockActual( lote.getStockActual( ) + cantidad );
        }
        Entrada entrada = new Entrada( );
        entrada.setLote( lote );
        entrada.setProveedor( proveedor );
        entrada.setCantidad( cantidad );
        entrada.setCostoUnitario( costo == null ? BigDecimal.ZERO : costo );
        entrada.setUsuario( usuarios.getReferenceById( idUsuario ) );
        return entradas.saveAndFlush( entrada );
    }
}
