package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class ExampleTest {
    @GetMapping("/myApp")
    public String getMethodName() {
        System.out.println("Seems to work");
        return "example.html";
    }
    
}
