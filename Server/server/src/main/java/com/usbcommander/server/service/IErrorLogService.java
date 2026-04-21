package com.usbcommander.server.service;

import java.time.LocalDateTime;
import java.util.List;

import com.usbcommander.server.entity.ErrorLog;
import com.usbcommander.server.entity.Machine;

public interface IErrorLogService {
    public List<ErrorLog> getByErrorLogsByMachine(Machine machine);
    public List<ErrorLog> getByRecievedDate(LocalDateTime recievedDate);
    public List<ErrorLog> getByRecievedDateBetween(LocalDateTime start, LocalDateTime end);
    public void save(ErrorLog errorLog);
}
