package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
/**
 * En esta clase se definen los endpoints dedicados a renderizar los archivos html relacionados con las acciones de la cuenta del usuario
 */
public class AccountController {

    @GetMapping("/change-password")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /account/change-password. 
     * Renderiza la página html de cambio de contraseña de la cuenta de usuario
     * @return
     */
    public String changePasswordPage() {
        return "account/change_password";
    }
}
