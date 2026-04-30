package com.usbcommander.server.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.usbcommander.server.dto.ConfigDTO;
import com.usbcommander.server.dto.MachineSummaryDTO;
import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.enums.LogType;
import com.usbcommander.server.service.IErrorLogService;
import com.usbcommander.server.service.ILogService;
import com.usbcommander.server.service.IMachineService;
import com.usbcommander.server.socket.MachineTalker;
import com.usbcommander.server.utils.WrapperMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private WrapperMapper mapper;

    @GetMapping("/getUnrevisedMachines")
    public ResponseEntity<List<Machine>> getUnrevisedMachines() {
        List<Machine> machines = new ArrayList<>();
        logService.getAllUnrevised().forEach(t -> {
            if(!machines.contains(t.getMachine())){
                machines.add(t.getMachine());
            }
        });
        return ResponseEntity.ok(machines);
    }

    @GetMapping("")
    public ResponseEntity<List<MachineSummaryDTO>> getAllMachines() {
        List<MachineSummaryDTO> result = new ArrayList<>();
        for (Machine machine : machineService.getAll()) {
            result.add(toSummary(machine));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MachineSummaryDTO> getMachineById(@PathVariable UUID id) {
        Optional<Machine> machine = machineService.getById(id);
        return machine.map(m -> ResponseEntity.ok(toSummary(m)))
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/error-logs")
    public ResponseEntity<List<ErrorLog>> getErrorLogsByMachine(@PathVariable UUID id) {
        Optional<Machine> machine = machineService.getById(id);
        if (machine.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(errorLogService.getByErrorLogsByMachine(machine.get()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMachine(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Optional<Machine> machineOpt = machineService.getById(id);
        if (machineOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Machine machine = machineOpt.get();

        if (updates.containsKey("name")) {
            Object raw = updates.get("name");
            if (raw == null) return ResponseEntity.badRequest().body("Name cannot be blank");
            String name = raw.toString().trim();
            if (name.isEmpty()) return ResponseEntity.badRequest().body("Name cannot be blank");
            machine.setName(name);
        }
        if (updates.containsKey("description")) {
            Object raw = updates.get("description");
            machine.setDescription(raw == null ? null : raw.toString());
        }
        if (updates.containsKey("disable")) {
            machine.setDisable(Boolean.TRUE.equals(updates.get("disable")));
        }

        machineService.save(machine);
        return ResponseEntity.ok(toSummary(machine));
    }

    @PostMapping("/{id}/change-machine-conf")
    public ResponseEntity<?> changeMachineConf(@PathVariable UUID id, @RequestBody Map<String, String> updates) {
        Optional<Machine> selectedMachine = machineService.getById(id);
        if (selectedMachine.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MachineTalker machineTalker = MachineTalker.getMachines().getOrDefault(selectedMachine.get().getIp(), null);
        
        if(machineTalker == null){
            return ResponseEntity.badRequest().body("Machine is not connected");
        }
        try{
            String logFrecStr = updates.getOrDefault("logFrecuency", null);
            Long logFrecuency = (logFrecStr != null && !logFrecStr.isBlank()) ? Long.valueOf(logFrecStr.strip()) : null;
            String enableUsbStr = updates.getOrDefault("enableUsb", null);
            Boolean enableUsb = (enableUsbStr != null && !enableUsbStr.isBlank()) ? Boolean.valueOf(enableUsbStr.strip()) : null;
            String enableForStr = updates.getOrDefault("enableFor", null);
            Long enableFor = (enableForStr != null && !enableForStr.isBlank()) ? Long.valueOf(enableForStr.strip()) * 1000 : null; //Keep it 1000th

            ConfigDTO configDTO = new ConfigDTO();
            if(logFrecuency != null){            
                configDTO.setFrecuency(logFrecuency);
            }

            if (enableUsb != null) {
                configDTO.setAllow(enableUsb);
            } else {
                configDTO.setAllow(false);
            }

            if((enableFor == null || enableFor < 0) && enableUsb != null && enableUsb){
                return ResponseEntity.badRequest().body("If usb is going to be enabled, a valid amount of seconds must be defined");
            } else if (enableFor != null) {
                configDTO.setAllowedTime(enableFor);
            }

            if(machineTalker.sendMessage(mapper.configdtoToString(configDTO))){
                return ResponseEntity.ok("Configuration sent successfully");
            } else {
                return ResponseEntity.status(500).body("Failed to send configuration to machine");
            }

        }catch(ClassCastException e){
            return ResponseEntity.badRequest().body("Invalid data types in request body");
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Error generating json of configDTO");
        }
    }

    private MachineSummaryDTO toSummary(Machine machine) {
        MachineSummaryDTO dto = new MachineSummaryDTO();
        dto.setId(machine.getId());
        dto.setName(machine.getName());
        dto.setIp(machine.getIp());
        dto.setDisable(machine.getDisable());
        dto.setLogFrecuency(machine.getLogFrecuency());
        dto.setRegisteredDate(machine.getRegisteredDate());
        dto.setDescription(machine.getDescription());
        dto.setConnected(MachineTalker.getMachines().containsKey(machine.getIp()));
        dto.setTopUnrevisedLogCode(topUnrevisedLogCode(machine));
        return dto;
    }

    private Integer topUnrevisedLogCode(Machine machine) {
        List<Log> unrevised = logService.getByMachineAndNeedsRevission(machine, true);
        Integer dangerCode = null, warningCode = null, infoCode = null;
        for (Log log : unrevised) {
            int code = log.getLogCode();
            if (code == LogType.CONFIG_MOD.getCode() || code == LogType.REGISTRY_MOD.getCode()) {
                dangerCode = code;
            } else if (code == LogType.INCOHERENT.getCode() || code == LogType.MEMORY_CONN.getCode()) {
                if (warningCode == null) warningCode = code;
            } else if (code == LogType.INFO.getCode()) {
                if (infoCode == null) infoCode = code;
            }
        }
        if (dangerCode != null) return dangerCode;
        if (warningCode != null) return warningCode;
        return infoCode;
    }

}
