package org.TecNova.repository;

import org.TecNova.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long>
{
    List<Venta> findTop200ByOrderByFechaVentaDescIdDesc( );

    List<Venta> findTop200ByUsuarioIdOrderByFechaVentaDescIdDesc( Long idUsuario );

    List<Venta> findTop60ByClienteIdOrderByFechaVentaDescIdDesc( Long idCliente );

    @Lock( LockModeType.PESSIMISTIC_WRITE )
    @Query( "select v from Venta v where v.id = :id" )
    Optional<Venta> findByIdForUpdate( @Param( "id" ) Long id );
}
