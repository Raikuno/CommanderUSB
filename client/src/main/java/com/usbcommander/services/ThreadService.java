package com.usbcommander.services;

import com.usbcommander.config.MachineConfig;
import com.usbcommander.managers.DriveManager;
import com.usbcommander.managers.UsbRegistryManager;
import com.usbcommander.services.contract.CommanderService;

public class ThreadService extends CommanderService{
    private static ThreadService instance;

    public static ThreadService getInstance(){
        if(instance == null){
            instance = new ThreadService();
        }
        return instance;
    }

    @Override
    public void run() {
        MachineConfig config = MachineConfig.getInstance();

        if(config.getUsbEnable()){
            UsbRegistryManager.enableAccess();
        } else {
            UsbRegistryManager.disableAccess();
            DriveManager.removeExternalDrives();
        }

        System.out.println(config.getUsbEnable() + "\n"+ config.getLogFrecuency() + "\n");
        SecurityService.getInstance().start();
        while (running);
    }
}
