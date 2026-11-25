package org.TecNova.service;

import org.TecNova.model.Lote;
import org.TecNova.repository.LoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService
{
    private final LoteRepository repository;

    public LoteService( LoteRepository repository )
    {
        this.repository = repository;
    }

    public List<Lote> findConStock( )
    {
        return repository.findByStockActualGreaterThanOrderByMedicamentoNombreComercialAscFechaCaducidadAsc( 0 );
    }
}
