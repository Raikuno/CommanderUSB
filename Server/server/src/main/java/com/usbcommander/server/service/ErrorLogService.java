package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;
import com.usbcommander.server.repository.ErrorLogRepository;

@Service
public class ErrorLogService implements IErrorLogService{
    @Autowired
    private ErrorLogRepository repository;
    
    @Override
    public List<ErrorLog> getByErrorLogsByMachine(Machine machine) {
        return repository.findByMachine(machine);
    }

    @Override
    public List<ErrorLog> getByRecievedDate(LocalDateTime recievedDate) {
        return repository.findByRecievedDate(recievedDate);
    }

    @Override
    public List<ErrorLog> getByRecievedDateBetween(LocalDateTime start, LocalDateTime end) {
        return repository.findByRecievedDateBetween(start, end);
    }
    
}
