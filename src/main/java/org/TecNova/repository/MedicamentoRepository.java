package org.TecNova.repository;

import org.TecNova.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Long>
{
    List<Medicamento> findAllByOrderByEstatusDescNombreComercialAsc( );

    List<Medicamento> findByEstatusTrueOrderByNombreComercialAsc( );
}
