package com.usbcommander.services;

import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.services.contract.CommanderService;

/**
 * Servicio encargado de lanzar y controlar el estado de otros servicios
 */
public class ThreadService extends CommanderService{
    /**
     * Instancia del servicio
     */
    private static ThreadService instance;
    /**
     * Instancia de IMachineConfig usada para habilitar la conexión con el servidor
     */
    private IMachineConfig machineConfig;
    /**
     * Instacía de IStatusManager usada para generar una entrada de registro al iniciar la aplicación
     */
    private IStatusManager statusManager;

    /**
     * Método estático usado para inicializar y obtener la instancia de este servicio
     * @return La instancia inicializada del Servicio
     */
    public static ThreadService getInstance(){
        if(instance == null){
            instance = new ThreadService();
        }
        return instance;
    }

    /**
     * Constructor encargado de inicializar las propiedades del objeto
     */
    private ThreadService(){
        this.machineConfig = MachineConfig.getInstance();
        this.statusManager = StatusManager.getInstance();
        machineConfig.enableServerService();
    } 

    @Override
    public void run() {
        statusManager.generateLog();
        SecurityService.getInstance().start();
        LogService.getInstance().start();
        ServerConnectionService.getInstance().start();
        try {
            SecurityService.getInstance().forceClose();
        } catch (ServiceDisabledException e) {
            statusManager.generateLog(LogType.ERROR, e.getMessage());
        }
        while (running);
    }
}
