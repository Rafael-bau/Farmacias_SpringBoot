package org.TecNova.api;

import org.TecNova.api.ApiDtos.MensajeRes;
import org.TecNova.util.Errores;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/** Respuestas de error en JSON: { "mensaje": "..." }. */
@RestControllerAdvice( basePackages = "org.TecNova.api" )
public class ApiExceptionHandler
{
    @ExceptionHandler( ResponseStatusException.class )
    public ResponseEntity<MensajeRes> estado( ResponseStatusException e )
    {
        return ResponseEntity.status( e.getStatus( ) ).body( new MensajeRes( e.getReason( ) ) );
    }

    @ExceptionHandler( HttpMessageNotReadableException.class )
    public ResponseEntity<MensajeRes> jsonInvalido( HttpMessageNotReadableException e )
    {
        return ResponseEntity.status( HttpStatus.BAD_REQUEST )
                .body( new MensajeRes( "El cuerpo de la petición no es un JSON válido." ) );
    }

    /** Reglas de negocio (stock insuficiente, datos inválidos, duplicados...) -> 400 con el mensaje en español. */
    @ExceptionHandler( RuntimeException.class )
    public ResponseEntity<MensajeRes> reglaDeNegocio( RuntimeException e )
    {
        return ResponseEntity.status( HttpStatus.BAD_REQUEST ).body( new MensajeRes( Errores.mensaje( e ) ) );
    }
}
