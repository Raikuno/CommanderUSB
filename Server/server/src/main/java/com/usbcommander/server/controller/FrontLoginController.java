package com.usbcommander.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.usbcommander.server.service.IFirstStartService;


@Controller
@RequestMapping("/session")
public class FrontLoginController {

    @Autowired
    private IFirstStartService loginService;

    @GetMapping("/login")
    public String loginPage() {
        if (!loginService.adminAccountCreated()) {
            return "redirect:/session/create-admin";
        }
        return "login.html";
    }

    @GetMapping("/create-admin")
    public String adminCreationPage(){
        if (loginService.adminAccountCreated()) {
            return "redirect:/session/login";
        }
        return "admin_creation.html";
    }
    
    @GetMapping
    ("/logout")
    public String logout() {
        return "logout.html";
    }

}
