package com.usbcommander.services;

import com.usbcommander.config.MachineConfig;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.services.contract.CommanderService;

public class LogService extends CommanderService{
    private static LogService instance;

    public static LogService getInstance(){
        if(instance == null){
            instance = new LogService();
        }
        return instance;
    }

    @Override
    public void run() {
        while(running){
            try {
                Thread.sleep(MachineConfig.getInstance().getLogFrecuency());
                StatusManager.statusLog();
            } catch (InterruptedException e) {
                StatusManager.statusLog(e.getMessage());
                running = false;
            }
        }
    }

    
}
