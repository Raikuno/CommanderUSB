package com.usbcommander.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.usbcommander.server.repository.RoleRepository;


@Controller
public class ExampleTest {

    @Autowired
    private RoleRepository repo;

    @GetMapping("/myApp")
    public String getMethodName() {
        var smth = repo.findByName("ADMIN");
        smth.ifPresent((t) -> System.out.println(t.getName()));
        System.out.println("Seems to work");
        return "example.html";
    }
    
}
