package org.TecNova.repository;

import org.TecNova.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long>
{
    List<DetalleVenta> findByVentaIdOrderByIdAsc( Long idVenta );
}
