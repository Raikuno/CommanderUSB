package com.usbcommander.services;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.errors.UnallowedOperationException;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

/**
 * Servicio encargado de lanzar y controlar el estado de otros servicios
 */
@Singleton 
public class ThreadService extends CommanderService{
    /**
     * Instancia de IMachineConfig usada para habilitar la conexión con el servidor
     */
    private final IMachineConfig machineConfig;
    /**
     * Instacía de IStatusManager usada para generar una entrada de registro al iniciar la aplicación
     */
    private final IUsbMemoryManager usbMemoryManager;
    private final IStatusManager statusManager;
    private final Provider<SecurityService> securityServiceProvider;
    private final Provider<LogService> logServiceProvider;
    private final Provider<ServerConnectionService> serverConnectionServiceProvider;

    @Inject 
    public ThreadService(IMachineConfig machineConfig,
                         IStatusManager statusManager,
                         Provider<SecurityService> securityServiceProvider,
                         Provider<LogService> logServiceProvider,
                         Provider<ServerConnectionService> serverConnectionServiceProvider,
                         IUsbMemoryManager usbMemoryManager){
        this.machineConfig = machineConfig;
        this.statusManager = statusManager;
        this.securityServiceProvider = securityServiceProvider;
        this.logServiceProvider = logServiceProvider;
        this.serverConnectionServiceProvider = serverConnectionServiceProvider;
        this.usbMemoryManager = usbMemoryManager;
    }

    @Override
    public void run() {
        machineConfig.enableServerService();
        try{
            statusManager.generateLog(usbMemoryManager.getAccessValue(), usbMemoryManager.getConnectedDrives());
        } catch (UnallowedOperationException e) {
            statusManager.generateErrorLog(e.getMessage());
        }
        securityServiceProvider.get().start();
        logServiceProvider.get().start();
        serverConnectionServiceProvider.get().start();
        try {
            securityServiceProvider.get().forceClose();
        } catch (ServiceDisabledException e) {
            statusManager.generateErrorLog(e.getMessage());
        }
        while (running);
    }
}
