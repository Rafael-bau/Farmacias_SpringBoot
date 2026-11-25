package org.TecNova.service;

import org.TecNova.model.Lote;
import org.TecNova.model.Salida;
import org.TecNova.repository.LoteRepository;
import org.TecNova.repository.SalidaRepository;
import org.TecNova.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/** Salidas que no son ventas: merma, caducidad, ajustes. */
@Service
public class SalidaService
{
    private static final Set<String> MOTIVOS = Set.of( "MERMA", "CADUCIDAD", "AJUSTE", "OTRO" );

    private final SalidaRepository salidas;
    private final LoteRepository lotes;
    private final UsuarioRepository usuarios;

    public SalidaService( SalidaRepository salidas, LoteRepository lotes, UsuarioRepository usuarios )
    {
        this.salidas = salidas;
        this.lotes = lotes;
        this.usuarios = usuarios;
    }

    public List<Salida> findAll( )
    {
        return salidas.findTop100ByOrderByFechaDescIdDesc( );
    }

    @Transactional
    public Salida registrar( Long idLote, int cantidad, String motivo, String observaciones, Long idUsuario )
    {
        if( cantidad <= 0 )
        {
            throw new IllegalArgumentException( "La cantidad debe ser mayor a 0." );
        }
        if( !MOTIVOS.contains( motivo ) )
        {
            throw new IllegalArgumentException( "Selecciona un motivo válido." );
        }
        if( observaciones != null && observaciones.length( ) > 200 )
        {
            throw new IllegalArgumentException( "Las observaciones son demasiado largas (máximo 200)." );
        }
        Lote lote = lotes.findByIdForUpdate( idLote ).orElseThrow( ( ) -> new IllegalArgumentException( "El lote no existe." ) );
        if( lote.getStockActual( ) < cantidad )
        {
            throw new IllegalArgumentException( "Stock insuficiente en el lote (disponibles: " + lote.getStockActual( ) + ")." );
        }
        lote.setStockActual( lote.getStockActual( ) - cantidad );
        Salida salida = new Salida( );
        salida.setLote( lote );
        salida.setCantidad( cantidad );
        salida.setMotivo( motivo );
        salida.setObservaciones( observaciones == null || observaciones.isBlank( ) ? null : observaciones.trim( ) );
        salida.setUsuario( usuarios.getReferenceById( idUsuario ) );
        return salidas.saveAndFlush( salida );
    }
}
