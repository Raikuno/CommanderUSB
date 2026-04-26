package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;

import com.usbcommander.server.entity.Log;
import com.usbcommander.server.entity.Machine;

public interface ILogService {
    public List<Log> getByMachine(Machine machine);
    public List<Log> getByLogCode(Integer logCode);
    public List<Log> getByRecievedDateBetweenAndMachine(LocalDateTime start, LocalDateTime end, Machine machine);
    public List<Log> getByCreationDateAndMachine(LocalDateTime creationDate, Machine machine);
    public List<Log> getAllUnrevised();
    public void save(Log log);
}
