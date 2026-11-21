package org.TecNova.service;

import org.TecNova.model.Categoria;
import org.TecNova.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService
{
    private final CategoriaRepository repository;

    public CategoriaService( CategoriaRepository repository )
    {
        this.repository = repository;
    }

    public List<Categoria> findAll( )
    {
        return repository.findAllByOrderByNombreAsc( );
    }

    public Categoria findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }

    @Transactional
    public Categoria guardar( Categoria categoria )
    {
        if( categoria.getNombre( ) == null || categoria.getNombre( ).isBlank( ) || categoria.getNombre( ).length( ) > 50 )
        {
            throw new IllegalArgumentException( "El nombre de la categoría es obligatorio (máximo 50 caracteres)." );
        }
        categoria.setNombre( categoria.getNombre( ).trim( ) );
        Categoria guardada = repository.save( categoria );
        repository.flush( );
        return guardada;
    }

    @Transactional
    public void eliminar( Long id )
    {
        repository.findById( id ).ifPresent( repository::delete );
        repository.flush( );
    }
}
