package com.usbcommander.services;

import java.util.List;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.LogDTO;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

public class SecurityService extends CommanderService{

    private static SecurityService instance;

    private ObjectMapper mapper; 
    private boolean expectedChange = false;
    private Thread usbStorListener;
    private Thread appConfigListener;
    private IUsbMemoryManager usbMemoryManager;
    private IStatusManager statusManager;
    private IMachineConfig machineConfig;
    private long grantedFor;
    private Thread grantedAccess;

    private SecurityService(){
        this.usbMemoryManager = UsbMemoryManager.getInstance();
        this.statusManager = StatusManager.getInstance();
        this.machineConfig = MachineConfig.getInstance();

        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        usbStorListener = setListenerOn(AppConst.RegistryReferences.USB_STOR, () -> {
            if(!machineConfig.isUsbEnable()){
                    LogDTO log = statusManager.generateLog(LogType.REGISTRY_MOD);
                    usbMemoryManager.removeExternalDrives();
                    try{
                        forceClose();
                    }catch(ServiceDisabledException err){
                        statusManager.generateLog(LogType.ERROR, err.getMessage());
                    }
                    sendLog(log);
                    System.out.println("Stor changed");
            }
        }, null);
        
        appConfigListener = setListenerOn(AppConst.ConfigReferences.CONFIG_LOCATION, () -> {
            if(!expectedChange){
                expectedChange = true;
                LogDTO log = statusManager.generateLog(LogType.CONFIG_MOD);
                machineConfig.saveConfig();
                sendLog(log);
                System.out.println("Config changed");
            } else {
                expectedChange = false;
            }
        }, null);
    }

    public static SecurityService getInstance(){
        if(instance == null){
            instance = new SecurityService();
        }
        return instance;
    }

    /**
     * This method enables the use of usb storage units during the specified duration
     * @param time The time during the usb storage units will be able to be mounted
     * @return  A boolean representing if the action was succesfull or not. This will only be returned after the access has been closed
     * @throws ServiceDisabledException 
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
                machineConfig.setUsbEnable(true);
                machineConfig.saveConfig();
                usbMemoryManager.enableAccess();
                try {
                    Thread.sleep(grantedFor);
                } catch (InterruptedException e) {
                    statusManager.generateLog(LogType.ERROR, "Open access thread interrupted: " + e.getMessage());
                }
            } catch (Win32Exception e) {
                statusManager.generateLog(LogType.ERROR, "Failed to enable USB access: " + e.getMessage());
            } finally {
                machineConfig.setUsbEnable(false);
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
            }
        });
        grantedFor = time;
        grantedAccess.setDaemon(true);
        grantedAccess.start();

        
    }

    /**
     * This method forces to close the access to usb storage units
     * @return A boolean representing if the operation was succesful
     * @throws ServiceDisabledException 
                } else {
                    expectedChange = false;
                }
     */
    public void forceClose() throws ServiceDisabledException{
        if(!running){
            throw new ServiceDisabledException(AppConst.ErrorMessages.SERVICE_NOT_RUNNING);
        }
        if (grantedAccess != null && grantedAccess.isAlive()) {
            grantedAccess.interrupt();
        }
        expectedChange = true;
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
        expectedChange = false;
    }

    @Override
    public void run() {
        startListener(appConfigListener);
        startListener(usbStorListener);

        while(running);
    }

    /**
     * Returns a thread object with a function that will set an event listener waiting for changes to be made on the value of the entry defined by the parameter route, to then perform the function defined
     * @param route The route to the windows registry entry
     * @param onChannge The function to be executed when the listener detect changes on the registry
     * @param onCatch The function to be executed in case tan error is encountered when creating the lsitener
     * @return A Thread object
     * @throws Win32Exception If the application can't access the registry values
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

    private boolean startListener(Thread listener){
        try{
            listener.setDaemon(true);
            listener.start();
            return true;
        } catch(IllegalThreadStateException err){
            return false;
        }
    }

    private void sendLog(LogDTO log){
        List<LogDTO> logList = List.of(log);
        try { 
            String message = mapper.writeValueAsString(logList);
            ServerConnectionService.getInstance().sendMessage(message);
        } catch (ServiceDisabledException | NotConnectionException e ) {
            StatusManager.getInstance().generateLog(LogType.ERROR, e.getMessage());
        } catch (JsonProcessingException e) {
            statusManager.generateLog(LogType.ERROR, AppConst.ErrorMessages.JACKSON_ERROR_TEXT + e.getMessage());
        }
    }
}
