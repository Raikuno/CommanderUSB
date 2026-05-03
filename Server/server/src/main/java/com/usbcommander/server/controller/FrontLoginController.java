package com.usbcommander.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.usbcommander.server.service.IFirstStartService;


@Controller
@RequestMapping("/session")
/**
 * En esta clase se definen los endpoints dedicados a las páginas de inicio de sesión
 */
public class FrontLoginController {

    @Autowired
    private IFirstStartService loginService;

    @GetMapping("/login")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/session/login. 
     * Renderiza la página html pertinente. En caso de que no exista un usuario en la base de datos, redirige a la página de creación de usuario
     * @return
     */
    public String loginPage() {
        if (!loginService.adminAccountCreated()) {
            return "redirect:/session/create-admin";
        }
        return "login.html";
    }

    @GetMapping("/create-admin")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/session/login. 
     * Renderiza la página html pertinente. En caso de que exista un usuario en la base de datos, redirige a la página de inicio de sesión
     * @return
     */
    public String adminCreationPage(){
        if (loginService.adminAccountCreated()) {
            return "redirect:/session/login";
        }
        return "admin_creation.html";
    }
    
    @GetMapping
    ("/logout")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/session/login. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String logout() {
        return "logout.html";
    }

}
