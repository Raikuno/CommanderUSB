package com.usbcommander.server.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/machines")
/**
 * En esta clase se definen los endpoints dedicados a las páginas relacionadas con las máquinas
 */
public class MachinePageController {

    @GetMapping({"", "/"})
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /machines/ o /machines. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String machinesIndex() {
        return "machines/index";
    }

    @GetMapping("/{id}")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /machines/{id}, siendo {id} el id de una máquina en específico. 
     * Obtiene la información de la máquina deseada y renderiza la página html pertinente.
     * @param id El id de la máquina que se quire visualizar
     * @param model El parámetro de modelo necesario para acceder al objeto que se pretende insertar en la página
     * @return
     */
    public String machineDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("machineId", id);
        return "machines/detail";
    }

    @GetMapping("/{machineId}/logs/{logId}")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /machines/{machineId}/logs/{logid}, siendo {machineId} el id de una máquina en específico y logid el id de un log específico
     * Obtiene la información de la máquina deseada y renderiza la página html pertinente.
     * @param machineId El id de la máquina a la que pertenece el log
     * @param logId El id del log que se quire visualizar
     * @param model El parámetro de modelo necesario para acceder al objeto que se pretende insertar en la página
     * @return
     */
    public String machineLogDetail(@PathVariable UUID machineId, @PathVariable Long logId, Model model) {
        model.addAttribute("machineId", machineId);
        model.addAttribute("logId", logId);
        return "machines/log_detail";
    }
}
