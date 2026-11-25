package org.TecNova.service;

import org.TecNova.model.Cliente;
import org.TecNova.repository.ClienteRepository;
import org.TecNova.util.Validador;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService
{
    private final ClienteRepository repository;

    public ClienteService( ClienteRepository repository )
    {
        this.repository = repository;
    }

    public List<Cliente> findAll( )
    {
        return repository.findAllByOrderByNombreAsc( );
    }

    public List<Cliente> buscar( String texto )
    {
        String t = texto == null ? "" : texto.trim( );
        return repository.findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCaseOrderByNombreAsc( t, t, t );
    }

    public Cliente findById( Long id )
    {
        return id == null ? null : repository.findById( id ).orElse( null );
    }

    public Cliente findByTelefono( String telefono )
    {
        return repository.findByTelefono( telefono ).orElse( null );
    }

    /** RF-09 y RF-10: alta/edición con validación estricta (expresiones regulares) en el servidor. */
    @Transactional
    public Cliente guardar( Cliente c )
    {
        String error = Validador.validarCliente( c.getNombre( ), c.getTelefono( ), c.getCorreo( ) );
        if( error != null )
        {
            throw new IllegalArgumentException( error );
        }
        c.setNombre( c.getNombre( ).trim( ) );
        c.setTelefono( c.getTelefono( ).trim( ) );
        c.setCorreo( c.getCorreo( ).trim( ).toLowerCase( ) );
        if( c.getPuntosAcumulados( ) == null )
        {
            c.setPuntosAcumulados( 0 );
        }
        Cliente guardado = repository.save( c );
        repository.flush( );
        return guardado;
    }

    /** Sus ventas se conservan (id_cliente pasa a NULL en la base de datos). */
    @Transactional
    public void eliminar( Long id )
    {
        repository.findById( id ).ifPresent( repository::delete );
        repository.flush( );
    }
}
