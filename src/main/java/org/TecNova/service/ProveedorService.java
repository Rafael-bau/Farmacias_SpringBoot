package org.TecNova.service;

import org.TecNova.model.Proveedor;
import org.TecNova.repository.ProveedorRepository;
import org.TecNova.util.Validador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProveedorService
{
    private final ProveedorRepository repository;

    public ProveedorService( ProveedorRepository repository )
    {
        this.repository = repository;
    }

    public List<Proveedor> findAll( )
    {
        return repository.findAllByOrderByEstatusDescNombreAsc( );
    }

    public List<Proveedor> findActivos( )
    {
        return repository.findByEstatusTrueOrderByNombreAsc( );
    }

    public Proveedor findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }

    @Transactional
    public Proveedor guardar( Proveedor p )
    {
        if( p.getNombre( ) == null || p.getNombre( ).isBlank( ) || p.getNombre( ).length( ) > 100 )
        {
            throw new IllegalArgumentException( "El nombre del proveedor es obligatorio (máximo 100 caracteres)." );
        }
        if( !blanco( p.getTelefono( ) ) && !Validador.telefono( p.getTelefono( ).trim( ) ) )
        {
            throw new IllegalArgumentException( "Teléfono inválido: deben ser 10 dígitos." );
        }
        if( !blanco( p.getCorreo( ) ) && !Validador.correo( p.getCorreo( ).trim( ) ) )
        {
            throw new IllegalArgumentException( "Correo electrónico inválido." );
        }
        if( p.getDireccion( ) != null && p.getDireccion( ).length( ) > 200 )
        {
            throw new IllegalArgumentException( "La dirección es demasiado larga." );
        }
        p.setNombre( p.getNombre( ).trim( ) );
        p.setTelefono( blanco( p.getTelefono( ) ) ? null : p.getTelefono( ).trim( ) );
        p.setCorreo( blanco( p.getCorreo( ) ) ? null : p.getCorreo( ).trim( ).toLowerCase( ) );
        p.setDireccion( blanco( p.getDireccion( ) ) ? null : p.getDireccion( ).trim( ) );
        if( p.getEstatus( ) == null )
        {
            p.setEstatus( true );
        }
        Proveedor guardado = repository.save( p );
        repository.flush( );
        return guardado;
    }

    @Transactional
    public void baja( Long id )
    {
        repository.findById( id ).ifPresent( p -> p.setEstatus( false ) );
    }

    private boolean blanco( String s )
    {
        return s == null || s.isBlank( );
    }
}
