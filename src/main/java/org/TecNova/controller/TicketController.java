package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.model.Venta;
import org.TecNova.service.VentaService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** RF-08: ticket digital en PDF. GET /ticket/{id} */
@Controller
public class TicketController
{
    private final VentaService service;

    public TicketController( VentaService service )
    {
        this.service = service;
    }

    @GetMapping( "/ticket/{id}" )
    public ResponseEntity<byte[]> ticket( @PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal actual )
    {
        Venta venta = service.findById( id );
        if( venta == null )
        {
            return ResponseEntity.notFound( ).build( );
        }
        String rol = actual.getUsuario( ).getRol( ).getNombre( );
        boolean permitido = "Administrador".equals( rol )
                || ( "Cajero".equals( rol ) && venta.getUsuario( ).getId( ).equals( actual.getUsuario( ).getId( ) ) )
                || ( "Cliente".equals( rol ) && actual.getClienteId( ) != null && venta.getCliente( ) != null
                     && venta.getCliente( ).getId( ).equals( actual.getClienteId( ) ) );
        if( !permitido )
        {
            return ResponseEntity.status( HttpStatus.FORBIDDEN ).build( );
        }
        HttpHeaders headers = new HttpHeaders( );
        headers.setContentType( MediaType.APPLICATION_PDF );
        headers.set( HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ticket-" + venta.getFolioTicket( ) + ".pdf\"" );
        return new ResponseEntity<>( service.ticketPdf( id ), headers, HttpStatus.OK );
    }
}
