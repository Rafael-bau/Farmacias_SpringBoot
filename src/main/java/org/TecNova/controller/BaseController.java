package org.TecNova.controller;

import org.TecNova.util.Errores;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Mensajes que se muestran tras un redirect (patrón POST-redirect-GET). */
public abstract class BaseController
{
    protected void ok( RedirectAttributes ra, String mensaje )
    {
        ra.addFlashAttribute( "mensaje", mensaje );
        ra.addFlashAttribute( "tipo", "success" );
    }

    protected void error( RedirectAttributes ra, Exception e )
    {
        ra.addFlashAttribute( "mensaje", Errores.mensaje( e ) );
        ra.addFlashAttribute( "tipo", "danger" );
    }
}
