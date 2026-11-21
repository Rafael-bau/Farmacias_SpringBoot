package org.TecNova.controller;

import org.TecNova.config.UsuarioPrincipal;
import org.TecNova.service.VentaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Portal del cliente: historial de sus compras. */
@Controller
public class MisComprasController
{
    private final VentaService service;

    public MisComprasController( VentaService service )
    {
        this.service = service;
    }

    @GetMapping( "/mis-compras" )
    public String compras( @AuthenticationPrincipal UsuarioPrincipal actual, Model model )
    {
        boolean sinCliente = actual.getClienteId( ) == null;
        model.addAttribute( "sinCliente", sinCliente );
        if( !sinCliente )
        {
            model.addAttribute( "compras", service.comprasDeCliente( actual.getClienteId( ) ) );
        }
        return "mis_compras";
    }
}
