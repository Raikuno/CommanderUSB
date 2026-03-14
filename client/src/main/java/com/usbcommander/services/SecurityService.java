package com.usbcommander.services;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.managers.DriveManager;
import com.usbcommander.managers.UsbRegistryManager;
import com.usbcommander.services.contract.CommanderService;

public class SecurityService extends CommanderService{

    private static SecurityService instance;

    private boolean expectedChange = false;

    private boolean enumListenerFix = false;

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
     */
    public boolean openAccess(long time){
        if(!running){
            return false;
        }
        expectedChange = true;
        boolean enabled = UsbRegistryManager.enableAccess();
        if (!enabled){
            return enabled;
        }
        try {
            Thread.sleep(time);
            expectedChange = true; //Second change because it will be changed by the listener
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            UsbRegistryManager.disableAccess();
            return false;
        }

        UsbRegistryManager.disableAccess();
        return true;
    }

    /**
     * This method forces to close the access to usb storage units
     * @return A boolean representing if the operation was succesful
     */
    public boolean forceClose(){
        if(!running){
            return false;
        }
        expectedChange = true;
        boolean closed = UsbRegistryManager.disableAccess();
        return closed;
    }

    @Override
    public void run() {
        Thread usbStorListener = setListenerOn(AppConst.RegistryReferences.USB_STOR, () -> {
            if(!expectedChange){
                DriveManager.removeExternalDrives();
                forceClose();
                System.out.println("Stor changed");
            } else {
                expectedChange = false;
            }
            
            //TODO Add the log
            
        });
        
        Thread usbEnumListener = setListenerOn(AppConst.RegistryReferences.USB_ENUM, () -> {
            if(!enumListenerFix){
                System.out.println("Enum changed");
                enumListenerFix = true;
            } else {
                enumListenerFix = false;
            }
            
            //TODO Add the log

        });
        Thread appConfigListener = setListenerOn(AppConst.ConfigReferences.CONFIG_LOCATION, () -> {
            MachineConfig.getInstance().saveConfig();
            //TODO Add the log
            System.out.println("Config changed");
        });

        try{
            usbStorListener.setDaemon(true);
            usbEnumListener.setDaemon(true);
            appConfigListener.setDaemon(true);

            usbStorListener.start();
            usbEnumListener.start();
            appConfigListener.start();

            while(running);
        }catch(Win32Exception err){
            //TODO Add the log
            running = false;
        }
        
        
    }
    /**
     * Returns a thread object with a function that will set an event listener waiting for changes to be made on the value of the entry defined by the parameter route, to then perform the function defined
     * @param route The route to the windows registry entry
     * @param onChannge The function to be executed when the listener detect changes on the registry
     * @return A Thread object
     * @throws Win32Exception If the application can't access the registry values
     */
    private Thread setListenerOn(String route, Runnable onChannge){
        return new Thread(()->{
            boolean error = false;
            HKEYByReference keyHandler = new HKEYByReference();
            int op_status;
            int notify_status;

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

            while(!error && running){
                try{
                    notify_status = Advapi32.INSTANCE.RegNotifyChangeKeyValue(
                    keyHandler.getValue(), 
                    false,
                    WinNT.REG_NOTIFY_CHANGE_LAST_SET,
                    null, 
                    false);

                    if(notify_status == WinError.ERROR_SUCCESS){
                        onChannge.run();
                    }
                } catch(Win32Exception err){
                    //TODO Add the log
                    error = true;
                }
            }
            Advapi32.INSTANCE.RegCloseKey(keyHandler.getValue());
            
        });
    }
}
