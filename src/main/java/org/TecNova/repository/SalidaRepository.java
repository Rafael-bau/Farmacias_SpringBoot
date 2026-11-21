package org.TecNova.repository;

import org.TecNova.model.Salida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SalidaRepository extends JpaRepository<Salida, Long>
{
    List<Salida> findTop100ByOrderByFechaDescIdDesc( );
}
