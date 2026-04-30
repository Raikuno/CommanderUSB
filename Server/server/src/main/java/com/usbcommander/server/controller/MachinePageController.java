package com.usbcommander.server.controller;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/machines")
public class MachinePageController {

    @GetMapping({"", "/"})
    public String machinesIndex() {
        return "machines/index";
    }

    @GetMapping("/{id}")
    public String machineDetail(@PathVariable UUID id, Model model) {
        model.addAttribute("machineId", id);
        return "machines/detail";
    }

    @GetMapping("/{machineId}/logs/{logId}")
    public String machineLogDetail(@PathVariable UUID machineId, @PathVariable Long logId, Model model) {
        model.addAttribute("machineId", machineId);
        model.addAttribute("logId", logId);
        return "machines/log_detail";
    }
}
