package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.repository.LogRepository;

@Service
public class LogService implements ILogService{
    @Autowired
    private LogRepository repository;

    @Override
    public List<Log> getByMachine(Machine machine) {
        return repository.findByMachine(machine);
    }

    @Override
    public List<Log> getByLogCode(Integer logCode) {
        return repository.findByLogCode(logCode);
    }

    @Override
    public List<Log> getByRecievedDateBetweenAndMachine(LocalDateTime start, LocalDateTime end, Machine machine) {
        return repository.findByRecievedDateBetweenAndMachine(start, end, machine);
    }

    @Override
    public List<Log> getByCreationDateAndMachine(LocalDateTime creationDate, Machine machine) {
        return repository.findByCreationDateAndMachine(creationDate, machine);
    }

    @Override
    public void save(Log log) {
        repository.save(log);
    }

    @Override
    public List<Log> getAllUnrevised() {
        return repository.findByNeedsRevission(true);
    }
    
}
