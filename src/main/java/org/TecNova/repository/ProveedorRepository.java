package org.TecNova.repository;

import org.TecNova.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long>
{
    List<Proveedor> findAllByOrderByEstatusDescNombreAsc( );

    List<Proveedor> findByEstatusTrueOrderByNombreAsc( );
}
