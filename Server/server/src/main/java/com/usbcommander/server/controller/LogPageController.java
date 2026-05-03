package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logs")
/**
 * En esta clase se definen los endpoints dedicados a las páginas relacionadas con los log
 */
public class LogPageController {

    @GetMapping({"", "/"})
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /logs/ o /logs. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String logsIndex() {
        return "logs/index";
    }

    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /logs/{id}, siendo {id} el id de un log en específico. 
     * Obtiene la información del log deseado y renderiza la página html pertinente.
     * @param id El id del log que se quire visualizar
     * @param model El parámetro de modelo necesario para acceder al objeto que se pretende insertar en la página
     * @return
     */
    @GetMapping("/{id}")
    public String logDetail(@PathVariable Long id, Model model) {
        model.addAttribute("logId", id);
        return "logs/detail";
    }
}
