package org.TecNova.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.TecNova.api.ApiDtos.*;
import org.TecNova.model.Medicamento;
import org.TecNova.service.CategoriaService;
import org.TecNova.service.MedicamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/medicamentos" )
@Tag( name = "Medicamentos" )
public class MedicamentoApiController
{

    private final MedicamentoService service;
    private final CategoriaService categoriaService;

    public MedicamentoApiController( MedicamentoService service, CategoriaService categoriaService )
    {
        this.service = service;
        this.categoriaService = categoriaService;
    }

    private Medicamento llenar( Medicamento m, MedicamentoReq body )
    {
        m.setCodigoBarras( body.codigoBarras( ) );
        m.setNombreComercial( body.nombreComercial( ) );
        m.setSustanciaActiva( body.sustanciaActiva( ) );
        m.setPrecioVenta( body.precioVenta( ) );
        m.setStockMinimo( body.stockMinimo( ) );
        m.setCategoria( categoriaService.findById( body.categoriaId( ) ) );
        m.setEstatus( body.estatus( ) == null ? Boolean.TRUE : body.estatus( ) );
        return m;
    }

    @GetMapping
    @Operation( summary = "Lista los medicamentos (activos y dados de baja)" )
    public List<MedicamentoRes> listar( )
    {
        return service.findAll( ).stream( ).map( ApiMapper::medicamento ).toList( );
    }

    @GetMapping( "/{id}" )
    @Operation( summary = "Obtiene un medicamento por su id" )
    public MedicamentoRes obtener( @PathVariable Long id )
    {
        return ApiMapper.medicamento( ApiMapper.requerido( service.findById( id ), "El medicamento no existe." ) );
    }

    @PostMapping
    @ResponseStatus( HttpStatus.CREATED )
    @Operation( summary = "Registra un medicamento" )
    public MedicamentoRes crear( @RequestBody MedicamentoReq body )
    {
        return ApiMapper.medicamento( service.guardar( llenar( new Medicamento( ), body ) ) );
    }

    @PutMapping( "/{id}" )
    @Operation( summary = "Actualiza un medicamento (con estatus=true se reactiva)" )
    public MedicamentoRes actualizar( @PathVariable Long id, @RequestBody MedicamentoReq body )
    {
        Medicamento m = ApiMapper.requerido( service.findById( id ), "El medicamento no existe." );
        return ApiMapper.medicamento( service.guardar( llenar( m, body ) ) );
    }

    @DeleteMapping( "/{id}" )
    @Operation( summary = "Da de baja un medicamento (baja lógica, RF-03)" )
    public MensajeRes baja( @PathVariable Long id )
    {
        service.baja( id );
        return new MensajeRes( "Medicamento dado de baja." );
    }
}
