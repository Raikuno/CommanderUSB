package com.usbcommander.services;

import com.usbcommander.config.MachineConfig;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

public class ThreadService extends CommanderService{
    private static ThreadService instance;
    private UsbMemoryManager usbMemoryManager;

    public static ThreadService getInstance(){
        if(instance == null){
            instance = new ThreadService();
        }
        return instance;
    }

    private ThreadService(){
        this.usbMemoryManager = UsbMemoryManager.getInstance();
    } 

    @Override
    public void run() {
        MachineConfig config = MachineConfig.getInstance();
        if(config.getUsbEnable()){
            usbMemoryManager.enableAccess();
        } else {
            usbMemoryManager.disableAccess();
            usbMemoryManager.removeExternalDrives();
        }

        System.out.println(config.getUsbEnable() + "\n"+ config.getLogFrecuency() + "\n");
        SecurityService.getInstance().start();
        LogService.getInstance().start();
        while (running);
    }
}
