package com.usbcommander.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.service.IErrorLogService;
import com.usbcommander.server.service.ILogService;
import com.usbcommander.server.service.IMachineService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/machine")
public class MachineApiController {
    
    @Autowired
    private IMachineService machineService;

    @Autowired
    private ILogService logService;

    @Autowired
    private IErrorLogService errorLogService;
    
    @GetMapping("/getUnrevisedMachines")
    public ResponseEntity<List<Machine>> getMethodName() {
        List<Machine> machines = new ArrayList<>();
        logService.getAllUnrevised().forEach(t -> {
            if(!machines.contains(t.getMachine())){
                machines.add(t.getMachine());
            }
        });
        return ResponseEntity.ok(machines);
    }
    
}
