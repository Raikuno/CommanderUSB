package com.usbcommander.services;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.services.contract.CommanderService;

public class LogService extends CommanderService{
    private static LogService instance;
    private IStatusManager statusManager;
    private IMachineConfig machineConfig;
    private ObjectMapper mapper;
    private ScheduledExecutorService schedule;

    private LogService(){
        this.schedule = Executors.newSingleThreadScheduledExecutor();
        this.statusManager = StatusManager.getInstance();
        this.machineConfig = MachineConfig.getInstance();
        this.mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static LogService getInstance(){
        if(instance == null){
            instance = new LogService();
        }
        return instance;
    }

    public void changeLogFrecuency(long frecuency){
        machineConfig.setLogFrecuency(frecuency);
    }

    @Override
    public void run() {
        schedule.scheduleAtFixedRate(()-> {
            statusManager.infoLog();
            List<LogDTO> history =statusManager.getHistory();
            
            try { 
                String message = mapper.writeValueAsString(history);
                ServerConnectionService.getInstance().sendMessage(message);
            } catch (ServiceDisabledException e) {
                StatusManager.getInstance().errorLog(AppConst.ErrorMessages.SERVICE_NOT_RUNNING + e.getMessage());
            } catch (NotConnectionException e) {
                StatusManager.getInstance().errorLog(AppConst.ErrorMessages.CONNECTION_ERROR_MESSAGE + e.getMessage());
            } catch (JsonProcessingException e) {
                statusManager.errorLog(AppConst.ErrorMessages.JACKSON_ERROR_TEXT + e.getMessage());
            }
        }, 
        machineConfig.getLogFrecuency(), 
        machineConfig.getLogFrecuency(), 
        TimeUnit.MILLISECONDS);
    while(running);
    }

    
}
