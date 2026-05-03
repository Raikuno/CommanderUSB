package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@RequestMapping("/error")
/**
 * En esta clase se definen los endpoints dedicados a las páginas de error
 */
public class ErrorPageController {
    @GetMapping("/noAuthority")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/error/noAuthority. 
     * Renderizará un html que mostrará la descripción del error
     * @return 
     */
    public String noAuthority() {
        return "errors/denied_access.html";
    }

    @GetMapping({"/", ""})
        
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/error/. 
     * @return 
     */
    public String defaultError() {
        return "errors/default.html";
    }
    
    
}
