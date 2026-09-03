package com.usbcommander.services;

import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.errors.UnallowedOperationException;
import com.usbcommander.managers.contract.IUsbSecurityManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

import jakarta.inject.Inject;

/**
 * Servicio encargado de controlar y asegurar las funciones de seguridad que controlan el uso de memorias usb y las correctas modificaciones en los archivos de configuración de la aplicación
 * Por el momento, su implementación es específica de Windows
 */
@Singleton 
public class SecurityService extends CommanderService{

    /**
     * Objeto Thread que almacena las instrucciones a ejecutar en el caso de que se produzca una modificación en el registro de windows
     */
    private Thread usbStorListener;
    /**
     * Objeto Thread que almacena las instrucciones a ejecutar en el caso de que se produzca una modificación en los archivos de configuración de la aplicación
     */
    private Thread appConfigListener;
    /**
     * Instancia de IUsbMemoryManager para controlar el estado de las memorias usb con respecto a la máquina
     */
    private final IUsbMemoryManager usbMemoryManager;
    /**
     * Instancia de IStatusManager para la creación de registros en el caso de que se detecten modificaciones no permitidas en la máquina
     */
    private final IStatusManager statusManager;

    private final IUsbSecurityManager securityManager;
    /**
     * Instancia de IMachineConfig para obtener la configuración esperada, asi como modificarla
     */
    private final IMachineConfig machineConfig;
    /**
     * Variable que representa el tiempo de acceso permitido para el uso de memorias usb
     */
    private long grantedFor;
    /**
     * Objeto Thread que almacena las instrucciones a ejecutar al momento de posibilitar el uso de memorias usb
     */
    private Thread grantedAccess;

    @Inject
    public SecurityService(IUsbMemoryManager usbMemoryManager,
                          IStatusManager statusManager,
                          IUsbSecurityManager securityManager,
                          IMachineConfig machineConfig,
                          Provider<LogService> logServiceProvider){
        this.usbMemoryManager = usbMemoryManager;
        this.statusManager = statusManager;
        this.securityManager = securityManager;
        this.machineConfig = machineConfig;
        usbStorListener = securityManager.setUsbListener(() -> {
            try{
                if(securityManager.usbStorMatchesMemory(machineConfig.isUsbEnable(), usbMemoryManager.getAccessValue()) ){
                    return;
                }
                statusManager.generateLog(LogType.REGISTRY_MOD, "", usbMemoryManager.getAccessValue(), usbMemoryManager.getConnectedDrives());
                try{
                    forceClose();
                }catch(ServiceDisabledException err){
                    statusManager.generateErrorLog(err.getMessage());
                }
                try {
                    logServiceProvider.get().sendLogs();
                } catch (ServiceDisabledException e) {
                    statusManager.generateErrorLog(e.getMessage());
                }
            } catch (UnallowedOperationException e) {
                statusManager.generateErrorLog(e.getMessage());
            }
            
        }, null);

        appConfigListener = securityManager.setConfigListener(() -> {
            try{
                if(securityManager.configMatchesMemory(machineConfig.isUsbEnable(), machineConfig.getLogFrecuency(), machineConfig.getIp(), machineConfig.getPort())){
                    return;
                }
                statusManager.generateLog(LogType.CONFIG_MOD, "", usbMemoryManager.getAccessValue(), usbMemoryManager.getConnectedDrives());
                machineConfig.saveConfig();
                try {
                    logServiceProvider.get().sendLogs();
                } catch (ServiceDisabledException e) {
                    statusManager.generateErrorLog(e.getMessage());
                }
            }catch (UnallowedOperationException e) {
                statusManager.generateErrorLog(e.getMessage());
            }
        }, null);
    }

    /**
     * Este método habilita la conexión de memorias usb en la máquina durante el tiempo establecido
     * @param time El tiempo durante el cuál será posible conectar memoras usb en la máquina
     * @throws ServiceDisabledException En el caso de que al momento de llamarse el método, este servicio no este activo
     */
    public void openAccess(long time) throws ServiceDisabledException{
        if(!running){
            throw new ServiceDisabledException(AppConst.ErrorMessages.SERVICE_NOT_RUNNING);
        }
        if (grantedAccess != null && grantedAccess.isAlive()) {
            grantedAccess.interrupt();
        }
        grantedAccess = new Thread(() -> {
            try {
                try {
                    machineConfig.setUsbEnable(true);
                    machineConfig.saveConfig();
                    usbMemoryManager.enableAccess();
                    Thread.sleep(grantedFor);
                } catch (UnallowedOperationException e) {
                    statusManager.generateErrorLog("Failed to enable USB access: " + e.getMessage());
                }
                machineConfig.setUsbEnable(false);
                machineConfig.saveConfig();
                try {
                    usbMemoryManager.removeExternalDrives();
                } catch (UnallowedOperationException e) {
                    statusManager.generateErrorLog("Couldnt eject drives after closing access: " + e.getMessage());
                }
                try {
                    usbMemoryManager.disableAccess();
                } catch (UnallowedOperationException e) {
                    statusManager.generateErrorLog("Couldnt disable USB access: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                    statusManager.generateErrorLog("Open access thread interrupted: " + e.getMessage());
            }
        });
        grantedFor = time;
        grantedAccess.setDaemon(true);
        grantedAccess.start();

        
    }

    /**
     * Este método deshabilita la posibilidad de conectar memorias usb y eyecta las actualmente montadas
     * @return Un booleano representando si la operación tuvo exito o no
     * @throws ServiceDisabledException En el caso de que al momento de llamarse el método, este servicio no este activo
     */
    public void forceClose() throws ServiceDisabledException{
        if(!running){
            throw new ServiceDisabledException(AppConst.ErrorMessages.SERVICE_NOT_RUNNING);
        }
        if (grantedAccess != null && grantedAccess.isAlive()) {
            grantedAccess.interrupt();
        }
        machineConfig.setUsbEnable(false);
        machineConfig.saveConfig();
        try {
            usbMemoryManager.removeExternalDrives();
        } catch (UnallowedOperationException e) {
            statusManager.generateErrorLog("Couldnt eject drives during forceClose: " + e.getMessage());
        }
        try {
            usbMemoryManager.disableAccess();
        } catch (UnallowedOperationException e) {
            statusManager.generateErrorLog("Couldnt disable USB access during forceClose: " + e.getMessage());
        }
    }

    public void changeLogFrecuency(long frecuency){
        machineConfig.setLogFrecuency(frecuency);
        machineConfig.saveConfig();
    }

    @Override
    public void run() {
        startListener(appConfigListener);
        startListener(usbStorListener);
        while(running);
    }

    /**
     * Método usado para iniciar la ejecución de los Thread creados en el Servicio para la supervisión de los campos en el registro de windows
     * @param listener El Thread de supervisión a iniciar
     * @return Un booleano indicando si la operación tuvo exito o no
     */
    private boolean startListener(Thread listener){
        try{
            listener.setDaemon(true);
            listener.start();
            return true;
        } catch(IllegalThreadStateException err){
            return false;
        }
    }
}
