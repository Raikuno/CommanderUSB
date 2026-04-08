package com.usbcommander.services;

import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

public class ThreadService extends CommanderService{
    private static ThreadService instance;
    private IUsbMemoryManager usbMemoryManager;
    private IMachineConfig machineConfig;

    public static ThreadService getInstance(){
        if(instance == null){
            instance = new ThreadService();
        }
        return instance;
    }

    private ThreadService(){
        this.usbMemoryManager = UsbMemoryManager.getInstance();
        this.machineConfig = MachineConfig.getInstance();
        machineConfig.enableServerService();
    } 

    @Override
    public void run() {
        if(machineConfig.isUsbEnable()){
            usbMemoryManager.enableAccess();
        } else {
            usbMemoryManager.disableAccess();
            usbMemoryManager.removeExternalDrives();
        }
        SecurityService.getInstance().start();
        LogService.getInstance().start();
        ServerConnectionService.getInstance().start();
        while (running);
    }
}
