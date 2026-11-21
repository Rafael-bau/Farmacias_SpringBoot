package org.TecNova.service;

import org.TecNova.model.Medicamento;
import org.TecNova.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService
{
    private final MedicamentoRepository repository;

    public MedicamentoService( MedicamentoRepository repository )
    {
        this.repository = repository;
    }

    public List<Medicamento> findAll( )
    {
        return repository.findAllByOrderByEstatusDescNombreComercialAsc( );
    }

    public List<Medicamento> findActivos( )
    {
        return repository.findByEstatusTrueOrderByNombreComercialAsc( );
    }

    public Medicamento findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }

    @Transactional
    public Medicamento guardar( Medicamento m )
    {
        if( vacio( m.getCodigoBarras( ) ) || vacio( m.getNombreComercial( ) ) || vacio( m.getSustanciaActiva( ) ) )
        {
            throw new IllegalArgumentException( "Código de barras, nombre y sustancia activa son obligatorios." );
        }
        if( m.getCodigoBarras( ).length( ) > 50 || m.getNombreComercial( ).length( ) > 100 || m.getSustanciaActiva( ).length( ) > 100 )
        {
            throw new IllegalArgumentException( "Alguno de los textos es demasiado largo." );
        }
        if( m.getPrecioVenta( ) == null || m.getPrecioVenta( ).signum( ) <= 0 )
        {
            throw new IllegalArgumentException( "El precio debe ser mayor a 0." );
        }
        if( m.getStockMinimo( ) == null || m.getStockMinimo( ) < 0 )
        {
            throw new IllegalArgumentException( "El stock mínimo no puede ser negativo." );
        }
        if( m.getCategoria( ) == null )
        {
            throw new IllegalArgumentException( "Selecciona una categoría." );
        }
        m.setCodigoBarras( m.getCodigoBarras( ).trim( ) );
        m.setNombreComercial( m.getNombreComercial( ).trim( ) );
        m.setSustanciaActiva( m.getSustanciaActiva( ).trim( ) );
        if( m.getEstatus( ) == null )
        {
            m.setEstatus( true );
        }
        Medicamento guardado = repository.save( m );
        repository.flush( );
        return guardado;
    }

    /** Baja lógica (RF-03): el medicamento ya pudo venderse. */
    @Transactional
    public void baja( Long id )
    {
        repository.findById( id ).ifPresent( m -> m.setEstatus( false ) );
    }

    private boolean vacio( String s )
    {
        return s == null || s.isBlank( );
    }
}
