package com.usbcommander.services;

import java.util.function.Consumer;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

/**
 * Servicio encargado de controlar y asegurar las funciones de seguridad que controlan el uso de memorias usb y las correctas modificaciones en los archivos de configuración de la aplicación
 * Por el momento, su implementación es específica de Windows
 */
public class SecurityService extends CommanderService{

    /**
     * Instancia del servicio
     */
    private static SecurityService instance;

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
    private IUsbMemoryManager usbMemoryManager;
    /**
     * Instancia de IStatusManager para la creación de registros en el caso de que se detecten modificaciones no permitidas en la máquina
     */
    private IStatusManager statusManager;
    /**
     * Instancia de IMachineConfig para obtener la configuración esperada, asi como modificarla
     */
    private IMachineConfig machineConfig;
    /**
     * Variable que representa el tiempo de acceso permitido para el uso de memorias usb
     */
    private long grantedFor;
    /**
     * Objeto Thread que almacena las instrucciones a ejecutar al momento de posibilitar el uso de memorias usb
     */
    private Thread grantedAccess;

    /**
     * Constructor encargado de inicializar las propiedades del objeto
     */
    private SecurityService(){
        this.usbMemoryManager = UsbMemoryManager.getInstance();
        this.statusManager = StatusManager.getInstance();
        this.machineConfig = MachineConfig.getInstance();
        usbStorListener = setListenerOn(AppConst.RegistryReferences.USB_STOR, () -> {
            if(usbStorMatchesMemory()){
                return;
            }
            statusManager.generateLog(LogType.REGISTRY_MOD);
            try{
                forceClose();
            }catch(ServiceDisabledException err){
                statusManager.generateLog(LogType.ERROR, err.getMessage());
            }
            try {
                LogService.getInstance().sendLogs();
            } catch (ServiceDisabledException e) {
                statusManager.generateLog(LogType.ERROR, e.getMessage());
            }
        }, null);

        appConfigListener = setListenerOn(AppConst.ConfigReferences.CONFIG_LOCATION, () -> {
            if(configMatchesMemory()){
                return;
            }
            statusManager.generateLog(LogType.CONFIG_MOD);
            machineConfig.saveConfig();
            try {
                LogService.getInstance().sendLogs();
            } catch (ServiceDisabledException e) {
                statusManager.generateLog(LogType.ERROR, e.getMessage());
            }
        }, null);
    }

    /**
     * Método estático usado para inicializar y obtener la instancia de este servicio
     * @return La instancia inicializada del Servicio
     */
    public static SecurityService getInstance(){
        if(instance == null){
            instance = new SecurityService();
        }
        return instance;
    }

    /**
     * Compara el estado actual del permiso de uso de las memorias usb en el registro de windows con el valor de configuración de la aplicación almacenado en memoria
     * @return True si el registro coincide con el valor de configuración, y falso en el caso contrario
    */
    private boolean usbStorMatchesMemory(){
        try{
            int expected = machineConfig.isUsbEnable()
                ? AppConst.RegistryReferences.ENABLE_VALUE
                : AppConst.RegistryReferences.DISABLE_VALUE;
            return usbMemoryManager.getAccessValue() == expected;
        }catch(Win32Exception err){
            return false;
        }
    }

    /**
     * Compara los valores de configuración almacenados en memoria con los almacenados en la máquina
     * @return True si los valores coinciden, false en el caso opuesto
     */
    private boolean configMatchesMemory(){
        try{
            int regUsb = Advapi32Util.registryGetIntValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION,
                AppConst.ConfigReferences.USB_ALLOW_ENTRY);
            if((regUsb == 1) != machineConfig.isUsbEnable()){
                return false;
            }

            long regLog = Advapi32Util.registryGetLongValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION,
                AppConst.ConfigReferences.LOG_ENTRY);
            if(regLog != machineConfig.getLogFrecuency()){
                return false;
            }

            if(machineConfig.getIp() != null){
                String regIp = Advapi32Util.registryGetStringValue(
                    AppConst.MAIN_LOCATION,
                    AppConst.ConfigReferences.CONFIG_LOCATION,
                    AppConst.ConfigReferences.IP_ENTRY);
                int regPort = Advapi32Util.registryGetIntValue(
                    AppConst.MAIN_LOCATION,
                    AppConst.ConfigReferences.CONFIG_LOCATION,
                    AppConst.ConfigReferences.PORT_ENTRY);
                if(!machineConfig.getIp().equals(regIp) || regPort != machineConfig.getPort()){
                    return false;
                }
            }

            return true;
        }catch(Win32Exception err){
            return false;
        }
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
                } catch (Win32Exception e) {
                    statusManager.generateLog(LogType.ERROR, "Failed to enable USB access: " + e.getMessage());
                }
                machineConfig.setUsbEnable(false);
                machineConfig.saveConfig();
                try {
                    usbMemoryManager.removeExternalDrives();
                } catch (Win32Exception e) {
                    statusManager.generateLog(LogType.ERROR, "Couldnt eject drives after closing access: " + e.getMessage());
                }
                try {
                    usbMemoryManager.disableAccess();
                } catch (Win32Exception e) {
                    statusManager.generateLog(LogType.ERROR, "Couldnt disable USB access: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                    statusManager.generateLog(LogType.ERROR, "Open access thread interrupted: " + e.getMessage());
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
        } catch (Win32Exception e) {
            statusManager.generateLog(LogType.ERROR, "Couldnt eject drives during forceClose: " + e.getMessage());
        }
        try {
            usbMemoryManager.disableAccess();
        } catch (Win32Exception e) {
            statusManager.generateLog(LogType.ERROR, "Couldnt disable USB access during forceClose: " + e.getMessage());
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
     * Método empleador para facilitar la creación de Threads encargados de supervisar entradas en el registro de windows
     * @param route La ruta del valor del registro en la cuál se va a crear el hilo de supervisión
     * @param onChannge La función a ejecutar cuando se detectan cambios en el registro
     * @param onCatch La función a ejecutar si se lanza una excepción al momento de lanzar el hilo
     * @return El hilo de supervisión
     */
    private Thread setListenerOn(String route, Runnable onChannge, Consumer<Win32Exception> onCatch){
        return new Thread(()->{
            HKEYByReference keyHandler = new HKEYByReference();
            int op_status;
            int notify_status;
            try{
                op_status = Advapi32.INSTANCE.RegOpenKeyEx(
                    AppConst.MAIN_LOCATION, 
                    route, 
                    0, 
                    WinNT.KEY_NOTIFY, 
                    keyHandler
                );
            
                if(op_status != WinError.ERROR_SUCCESS){
                    throw new Win32Exception(op_status);
                }
        
                while(running){
                        notify_status = Advapi32.INSTANCE.RegNotifyChangeKeyValue(
                        keyHandler.getValue(), 
                        false,
                        WinNT.REG_NOTIFY_CHANGE_LAST_SET,
                        null, 
                        false);

                        if(notify_status == WinError.ERROR_SUCCESS){
                            onChannge.run();
                        }
                    }

                }catch(Win32Exception err){
                    if(onCatch != null){
                        onCatch.accept(err);
                    }
                }

                Advapi32.INSTANCE.RegCloseKey(keyHandler.getValue());
        });
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
