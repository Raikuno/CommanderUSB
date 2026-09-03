package com.usbcommander;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.enums.SupportedOS;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.managers.UsbSecurityManager;
import com.usbcommander.managers.contract.IUsbSecurityManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.LogService;
import com.usbcommander.services.SecurityService;
import com.usbcommander.services.ServerConnectionService;
import com.usbcommander.services.ThreadService;

public class AppModuleConfiiguration extends AbstractModule {
    private SupportedOS getSystemOS(){
        if(System.getProperty("os.name").toLowerCase().contains("windows")){
            return SupportedOS.WINDOWS;
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + System.getProperty("os.name"));
        }
    }
    @Override
    protected void configure() {
        switch (getSystemOS()) {
            case WINDOWS:
                    bind(IUsbSecurityManager.class).to(UsbSecurityManager.class).in(Singleton.class);
                    bind(IMachineConfig.class).to(MachineConfig.class).in(Singleton.class);
                    bind(IStatusManager.class).to(StatusManager.class).in(Singleton.class);
                    bind(IUsbMemoryManager.class).to(UsbMemoryManager.class).in(Singleton.class);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported OS: " + System.getProperty("os.name"));
        }
        // Clases que no dependen del sistema operativo
        bind(LogService.class).in(Singleton.class);
        bind(ServerConnectionService.class).in(Singleton.class);
        bind(ThreadService.class).in(Singleton.class);
        bind(SecurityService.class).in(Singleton.class);

        
    }
}
