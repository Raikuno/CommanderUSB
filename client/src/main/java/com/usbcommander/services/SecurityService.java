package com.usbcommander.services;

import java.util.function.Consumer;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.UsbMemoryManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.managers.contract.IUsbMemoryManager;
import com.usbcommander.services.contract.CommanderService;

public class SecurityService extends CommanderService{

    private static SecurityService instance;

    private boolean expectedChange = false;

    private boolean enumListenerFix = false;

    private Thread usbStorListener;

    private Thread usbEnumListener;

    private Thread appConfigListener;

    private IUsbMemoryManager usbMemoryManager;

    private IStatusManager statusManager;

    private SecurityService(){
        this.usbMemoryManager = UsbMemoryManager.getInstance();
        this.statusManager = StatusManager.getInstance();

        usbStorListener = setListenerOn(AppConst.RegistryReferences.USB_STOR, () -> {
            if(!expectedChange){
                statusManager.registryModificationLog();
                usbMemoryManager.removeExternalDrives();
                forceClose();
                System.out.println("Stor changed");
            } else {
                expectedChange = false;
            }
            
        }, null);
        
        usbEnumListener = setListenerOn(AppConst.RegistryReferences.USB_ENUM, () -> {
            if(!enumListenerFix){
                System.out.println("Enum changed");
                enumListenerFix = true;
            } else {
                enumListenerFix = false;
            }
            //TODO Add the log
        }, (err) -> {

        });
        appConfigListener = setListenerOn(AppConst.ConfigReferences.CONFIG_LOCATION, () -> {
            statusManager.unauthorizedConfigurationModificationLog();
            MachineConfig.getInstance().saveConfig();
            System.out.println("Config changed");
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
     */
    public boolean openAccess(long time){
        if(!running){
            return false;
        }
        expectedChange = true;
        boolean enabled = usbMemoryManager.enableAccess();
        if (!enabled){
            return enabled;
        }
        try {
            Thread.sleep(time);
            expectedChange = true; //Second change because it will be changed by the listener
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            usbMemoryManager.disableAccess();
            return false;
        }

        usbMemoryManager.disableAccess();
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
        boolean closed = usbMemoryManager.disableAccess();
        return closed;
    }

    @Override
    public void run() {
        startListener(appConfigListener);
        startListener(usbEnumListener);
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
}
