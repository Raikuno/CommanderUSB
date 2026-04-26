package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserAdministrationPageController {

    @GetMapping({"", "/"})
    public String usersPanel() {
        return "users/index";
    }

    @GetMapping("/new")
    public String newUserForm() {
        return "users/new";
    }

    @GetMapping("/roles")
    public String rolesPanel() {
        return "users/roles";
    }

    @GetMapping("/roles/new")
    public String newRoleForm() {
        return "users/roles_new";
    }

    @GetMapping("/roles/{id}/edit")
    public String editRoleForm(@PathVariable String id, Model model) {
        model.addAttribute("roleId", id);
        return "users/roles_edit";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable String id, Model model) {
        model.addAttribute("userId", id);
        return "users/edit";
    }

    @GetMapping("/permissions")
    public String permissionsPanel() {
        return "users/permissions";
    }
}
