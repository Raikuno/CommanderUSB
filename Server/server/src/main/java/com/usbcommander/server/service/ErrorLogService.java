package com.usbcommander.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.repository.ErrorLogRepository;

@Service
/**
 * Implementación de IErrorLogService
 */
public class ErrorLogService implements IErrorLogService{
    @Autowired
    /**
     * Repositorio vinculado al servicio
     */
    private ErrorLogRepository repository;
    
    @Override
    public List<ErrorLog> getByErrorLogsByMachine(Machine machine) {
        return repository.findByMachine(machine);
    }

    @Override
    public void save(ErrorLog errorLog) {
        repository.save(errorLog);
    }
    
}
