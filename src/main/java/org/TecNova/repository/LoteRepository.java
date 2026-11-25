package org.TecNova.repository;

import org.TecNova.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long>
{
    Optional<Lote> findByMedicamentoIdAndNumLote( Long idMedicamento, String numLote );

    List<Lote> findByStockActualGreaterThanOrderByMedicamentoNombreComercialAscFechaCaducidadAsc( Integer minimo );

    @Lock( LockModeType.PESSIMISTIC_WRITE )
    @Query( "select l from Lote l where l.id = :id" )
    Optional<Lote> findByIdForUpdate( @Param( "id" ) Long id );
}
