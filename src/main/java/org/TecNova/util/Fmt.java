package org.TecNova.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Ayudas de formato para las plantillas Thymeleaf: ${@fmt.dinero(x)}, ${@fmt.fecha(x)}, ${@fmt.entero(x)}. */
@Component( "fmt" )
public class Fmt
{
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern( "dd/MM/yyyy" );
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern( "dd/MM/yyyy HH:mm" );

    public String dinero( Object o )
    {
        if( o == null )
        {
            return "$0.00";
        }
        return String.format( Locale.US, "$%,.2f", new BigDecimal( o.toString( ) ) );
    }

    public String fecha( Object o )
    {
        if( o == null )
        {
            return "";
        }
        if( o instanceof LocalDate )
        {
            return ( (LocalDate) o ).format( FECHA );
        }
        if( o instanceof LocalDateTime )
        {
            return ( (LocalDateTime) o ).format( FECHA_HORA );
        }
        if( o instanceof java.sql.Timestamp )
        {
            return ( (java.sql.Timestamp) o ).toLocalDateTime( ).format( FECHA_HORA );
        }
        if( o instanceof java.sql.Date )
        {
            return ( (java.sql.Date) o ).toLocalDate( ).format( FECHA );
        }
        return o.toString( );
    }

    public int entero( Object o )
    {
        return o == null ? 0 : ( (Number) o ).intValue( );
    }
}
