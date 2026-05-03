package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
/**
 * En esta clase se definen los endpoints dedicados a las páginas relacionadas a la administración de usuarios
 */
public class UserAdministrationPageController {

    @GetMapping({"", "/"})
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/ o /users. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String usersPanel() {
        return "users/index";
    }

    @GetMapping("/new")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/new. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String newUserForm() {
        return "users/new";
    }

    @GetMapping("/roles")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/roles. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String rolesPanel() {
        return "users/roles";
    }

    @GetMapping("/roles/new")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/roles/new. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String newRoleForm() {
        return "users/roles_new";
    }

    @GetMapping("/roles/{id}/edit")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/roles/{id}/edit. Siendo {id} el id del rol a editar 
     * Obtiene el rol deseado y renderiza la página html pertinente.
     * @param id El id del rol a editar
     * @param model El parámetro de modelo necesario para acceder al objeto que se pretende insertar en la página
     * @return
     */
    public String editRoleForm(@PathVariable String id, Model model) {
        model.addAttribute("roleId", id);
        return "users/roles_edit";
    }

    @GetMapping("/{id}/edit")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/{id}/edit. Siendo {id} el id del usuario a editar 
     * Obtiene el usuario deseado y renderiza la página html pertinente.
     * @param id El id del usuario a editar
     * @param model El parámetro de modelo necesario para acceder al objeto que se pretende insertar en la página
     * @return
     */
    public String editUserForm(@PathVariable String id, Model model) {
        model.addAttribute("userId", id);
        return "users/edit";
    }

    @GetMapping("/permissions")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /users/roles/permissions. 
     * Renderiza la página html pertinente.
     * @return
     */
    public String permissionsPanel() {
        return "users/permissions";
    }
}
