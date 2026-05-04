package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
/**
 * En esta clase se definen los endpoints dedicados a la página de inicio
 */
public class HomePageController {
    @GetMapping({"/", "", "/home"})
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a / o /home. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String welcomePage() {
        return "welcome.html";
    }

}
