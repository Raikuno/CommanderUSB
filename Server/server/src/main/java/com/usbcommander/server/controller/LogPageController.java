package com.usbcommander.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/logs")
public class LogPageController {

    @GetMapping({"", "/"})
    public String logsIndex() {
        return "logs/index";
    }

    @GetMapping("/{id}")
    public String logDetail(@PathVariable Long id, Model model) {
        model.addAttribute("logId", id);
        return "logs/detail";
    }
}
