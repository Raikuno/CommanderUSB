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
/**
 * En esta clase se definen los endpoints dedicados a las acciones relacionados a los log
 */
public class LogApiController {

    @Autowired 
    private ILogService logService;
    @Autowired 
    private IMachineService machineService;

    @GetMapping("/unrevised")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/logs/unrevised
     * Devuelve una respuesta con todos los logs no marcados como revisados
     * @return Respuesta con una lista con todos los logs no marcados como revisados
     */
    public ResponseEntity<List<Log>> getUnrevised() {
        return ResponseEntity.ok(logService.getAllUnrevised());
    }

    @GetMapping("/by-machine/{machineId}")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/logs//by-machine/{machineId}, siendo {machineId} la id de la máquina por cuyos log se esta preguntando
     * Busca la máquina almacenada y actua en función de si se encuentra o no, enviando los logs asignados a esta en caso de que ese encuentre
     * @param machineId El id de la máquina por cuyos logs se esta preguntando
     * @return Una respuesta en función de si se ha encontrado o no la máquina pedida
     */
    public ResponseEntity<List<Log>> getByMachine(@PathVariable UUID machineId) {
        java.util.Optional<Machine> machine = machineService.getById(machineId);
        if (machine.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(logService.getByMachine(machine.get()));
    }

    @GetMapping("/{id}")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/logs/{id}, siendo {id} la id de un log
     * @param id El id del log por el que se esta preguntando
     * @return Una respuesta en función de si se ha encontrado el id o no, enviando este en caso de que se encuentre
     */
    public ResponseEntity<Log> getById(@PathVariable Long id) {
        return logService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/revise")
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/logs/{id}/revise, siendo {id} la id de un log.
     * Permite marcar un log como revisado.
     * @param id El id del log a revisar
     * @return Una respuesta en función de si se ha encontrado o no el log y si se ha marcado como revisado correctamente
     */
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
    /**
     * Método a ejecutar cuándo se lleva a cabo una petición a /api/logs/revise-bulk
     * Permite revisar un grupo de logs sin revisar enviado por parámetro
     * @param ids La lista de logs a revisar enviada en la petición
     * @return Una respuesta en función de si se han revisado los logs o si no se ha encontrado logs en la lista enviada 
     */
    public ResponseEntity<?> reviseBulk(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body("No log IDs provided");
        }
        logService.reviseAll(ids);
        return ResponseEntity.ok().build();
    }
}
