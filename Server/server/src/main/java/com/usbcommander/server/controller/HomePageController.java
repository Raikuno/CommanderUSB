package com.usbcommander.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.usbcommander.server.service.IMachineService;




@Controller
public class HomePageController {
    @Autowired
    IMachineService service;
    @GetMapping({"/", "", "/home"})
    public String getMethodName(Model model) {
        model.addAttribute("machine", service.getAll().get(0));
        return "welcome.html";
    }
    
}
