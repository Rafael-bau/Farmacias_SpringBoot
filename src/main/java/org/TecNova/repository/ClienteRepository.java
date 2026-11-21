package org.TecNova.repository;

import org.TecNova.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long>
{
    Optional<Cliente> findByTelefono( String telefono );

    Optional<Cliente> findByUsuarioId( Long idUsuario );

    List<Cliente> findAllByOrderByNombreAsc( );

    List<Cliente> findByNombreContainingIgnoreCaseOrTelefonoContainingOrCorreoContainingIgnoreCaseOrderByNombreAsc(
            String nombre, String telefono, String correo );

    /** Desliga la cuenta de usuario de cualquier cliente (antes de volver a ligarla). */
    @Modifying( clearAutomatically = true )
    @Query( "update Cliente c set c.usuario = null where c.usuario.id = :u" )
    int desvincular( @Param( "u" ) Long idUsuario );
}
