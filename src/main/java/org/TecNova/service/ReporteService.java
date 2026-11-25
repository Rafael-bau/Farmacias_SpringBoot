package org.TecNova.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consultas de solo lectura sobre las vistas de la base de datos (alertas, inventario, almacén, catálogo). */
@Service
@Transactional( readOnly = true )
public class ReporteService
{
    @PersistenceContext
    private EntityManager em;

    @SuppressWarnings( "unchecked" )
    private List<Map<String, Object>> consultar( String sql, Map<String, Object> parametros )
    {
        Query query = em.createNativeQuery( sql, Tuple.class );
        for( Map.Entry<String, Object> p : parametros.entrySet( ) )
        {
            query.setParameter( p.getKey( ), p.getValue( ) );
        }
        List<Map<String, Object>> filas = new ArrayList<>( );
        for( Tuple tupla : (List<Tuple>) query.getResultList( ) )
        {
            Map<String, Object> fila = new LinkedHashMap<>( );
            for( TupleElement<?> elemento : tupla.getElements( ) )
            {
                fila.put( elemento.getAlias( ), tupla.get( elemento ) );
            }
            filas.add( fila );
        }
        return filas;
    }

    private List<Map<String, Object>> consultar( String sql )
    {
        return consultar( sql, new HashMap<>( ) );
    }

    private long contar( String sql )
    {
        return ( (Number) consultar( sql ).get( 0 ).get( "n" ) ).longValue( );
    }

    private String limpio( String s )
    {
        return s == null ? "" : s.trim( );
    }

    public long totalCaducidad( )
    {
        return contar( "select count(*) as n from v_alerta_caducidad" );
    }

    public long totalStockBajo( )
    {
        return contar( "select count(*) as n from v_alerta_stock_critico" );
    }

    public List<Map<String, Object>> resumenStock( int limite )
    {
        return consultar( "select nombre_comercial, stock_vigente, estado from v_almacen "
                + "order by stock_vigente asc, nombre_comercial limit " + limite );
    }

    public List<Map<String, Object>> inventario( String filtro, String texto )
    {
        StringBuilder sql = new StringBuilder( "select * from v_inventario_lotes where 1 = 1" );
        Map<String, Object> p = new HashMap<>( );
        if( "caducidad".equals( filtro ) )
        {
            sql.append( " and estado in ('CADUCADO', 'POR CADUCAR')" );
        }
        else if( "stock".equals( filtro ) )
        {
            sql.append( " and stock_total <= stock_minimo" );
        }
        if( !limpio( texto ).isEmpty( ) )
        {
            sql.append( " and (nombre_comercial like :t or codigo_barras like :t)" );
            p.put( "t", "%" + limpio( texto ) + "%" );
        }
        sql.append( " order by field(estado, 'CADUCADO', 'POR CADUCAR', 'STOCK BAJO', 'VIGENTE'), "
                + "fecha_caducidad, nombre_comercial limit 300" );
        return consultar( sql.toString( ), p );
    }

    public List<Map<String, Object>> almacen( String filtro, String texto )
    {
        StringBuilder sql = new StringBuilder( "select * from v_almacen where 1 = 1" );
        Map<String, Object> p = new HashMap<>( );
        if( "stock".equals( filtro ) )
        {
            sql.append( " and estado in ('AGOTADO', 'STOCK BAJO')" );
        }
        if( !limpio( texto ).isEmpty( ) )
        {
            sql.append( " and (nombre_comercial like :t or codigo_barras like :t or sustancia_activa like :t)" );
            p.put( "t", "%" + limpio( texto ) + "%" );
        }
        sql.append( " order by nombre_comercial limit 300" );
        return consultar( sql.toString( ), p );
    }

    private static final String SQL_CATALOGO =
            "select m.id_medicamento, m.nombre_comercial, m.sustancia_activa, m.precio_venta, "
            + "c.nombre_categoria, coalesce(s.stock, 0) as stock "
            + "from medicamentos m join categorias c on c.id_categoria = m.id_categoria "
            + "left join (select id_medicamento, sum(stock_actual) as stock from lotes "
            + "           where fecha_caducidad >= curdate() group by id_medicamento) s "
            + "       on s.id_medicamento = m.id_medicamento "
            + "where m.estatus = true ";

    public List<Map<String, Object>> catalogo( String texto )
    {
        Map<String, Object> p = new HashMap<>( );
        p.put( "t", "%" + limpio( texto ) + "%" );
        return consultar( SQL_CATALOGO + "and (m.nombre_comercial like :t or m.sustancia_activa like :t) "
                + "order by m.nombre_comercial limit 120", p );
    }

    public List<Map<String, Object>> novedades( int limite )
    {
        return consultar( SQL_CATALOGO + "order by m.id_medicamento desc limit " + limite );
    }

    public List<Map<String, Object>> lotesDisponibles( String texto )
    {
        Map<String, Object> p = new HashMap<>( );
        p.put( "t", "%" + limpio( texto ) + "%" );
        return consultar( "select * from v_lotes_disponibles where nombre_comercial like :t or codigo_barras like :t "
                + "limit 40", p );
    }

    public Map<String, Object> lotePorId( Long idLote )
    {
        Map<String, Object> p = new HashMap<>( );
        p.put( "id", idLote );
        List<Map<String, Object>> filas = consultar( "select * from v_lotes_disponibles where id_lote = :id", p );
        return filas.isEmpty( ) ? null : filas.get( 0 );
    }
}
