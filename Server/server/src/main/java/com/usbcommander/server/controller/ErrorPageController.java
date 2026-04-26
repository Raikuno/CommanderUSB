package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@RequestMapping("/error")
public class ErrorPageController {
    @GetMapping("/noAuthority")
    public String getMethodName() {
        return "errors/denied_access.html";
    }
    
    @GetMapping("/")
    public String defaultError() {
        return "errors/default.html";
    }
    
    
}
