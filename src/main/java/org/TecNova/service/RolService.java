package org.TecNova.service;

import org.TecNova.model.Rol;
import org.TecNova.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService
{
    private final RolRepository repository;

    public RolService( RolRepository repository )
    {
        this.repository = repository;
    }

    public List<Rol> findAll( )
    {
        return repository.findAll( );
    }

    public Rol findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }
}
