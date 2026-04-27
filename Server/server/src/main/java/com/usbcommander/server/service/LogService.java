package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<Log> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void reviseAll(List<Long> ids) {
        List<Log> logs = repository.findAllById(ids);
        logs.forEach(log -> log.setNeedsRevission(false));
        repository.saveAll(logs);
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
