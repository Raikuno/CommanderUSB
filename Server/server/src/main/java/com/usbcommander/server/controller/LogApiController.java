package com.usbcommander.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.service.ILogService;
import com.usbcommander.server.service.IMachineService;

@RestController
@RequestMapping("/api/logs")
public class LogApiController {

    @Autowired 
    private ILogService logService;
    @Autowired 
    private IMachineService machineService;

    @GetMapping("/unrevised")
    public ResponseEntity<List<Log>> getUnrevised() {
        return ResponseEntity.ok(logService.getAllUnrevised());
    }

    @GetMapping("/by-machine/{machineId}")
    public ResponseEntity<List<Log>> getByMachine(@PathVariable UUID machineId) {
        java.util.Optional<Machine> machine = machineService.getById(machineId);
        if (machine.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logService.getByMachine(machine.get()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Log> getById(@PathVariable Long id) {
        return logService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/revise")
    public ResponseEntity<?> revise(@PathVariable Long id) {
        return logService.getById(id)
                .map(log -> {
                    log.setNeedsRevission(false);
                    logService.save(log);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/revise-bulk")
    public ResponseEntity<?> reviseBulk(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No log IDs provided");
        }
        logService.reviseAll(ids);
        return ResponseEntity.ok().build();
    }
}
