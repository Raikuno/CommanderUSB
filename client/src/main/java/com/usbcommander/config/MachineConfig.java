package com.usbcommander.config;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.usbcommander.AppConst;

/**
 * Class with cerrtain values used in differents areas of the software.
 * Uses a singleton pattern to evade duplicated instances.
 */
public class MachineConfig {
    private static HKEY mainLocation = AppConst.MAIN_LOCATION;
    private static String configLocation = AppConst.ConfigReferences.CONFIG_LOCATION;
    private static String usbAllowEntry = AppConst.ConfigReferences.USB_ALLOW_ENTRY;
    private static String logEntry = AppConst.ConfigReferences.LOG_ENTRY;

    private static MachineConfig instance;

    private boolean usbEnable;
    private long logFrecuency;

    
    private MachineConfig(){
        this.usbEnable = false;
        this.logFrecuency = 300000;
        try{
            Advapi32Util.registryCreateKey(mainLocation, configLocation);
        }catch(Win32Exception err){
            System.out.println(err.getMessage());
            //TODO Should create a logger for these things
        }
        
        saveConfig();
    }

    private MachineConfig(boolean usbEnable, long logFrecuency){
        this.usbEnable = usbEnable;
        this.logFrecuency = logFrecuency;
        
    }

    public static MachineConfig getInstance(){
        if(instance == null){
            try{
                long logValue;
                instance = new MachineConfig(
                    Advapi32Util.registryGetIntValue(mainLocation, configLocation, usbAllowEntry ) == 0?false:true,
                    (logValue = Advapi32Util.registryGetLongValue(mainLocation, configLocation, logEntry)) > 300000?
                        logValue:300000
                );
            }catch(Win32Exception err){
                instance = new MachineConfig();
                //TODO add a way to see the error throuugh a safe external source.
            }
        }

        return instance;
    }


    /**
     * 
     */
    public boolean saveConfig(){
        try{
            Advapi32Util.registrySetIntValue(mainLocation, configLocation, "allowUsb", usbEnable?1:0);
            Advapi32Util.registrySetLongValue(mainLocation, configLocation, "logFrec", logFrecuency);
            return true;
        }catch(Win32Exception err){
            return false;
        }
        
    }

    public boolean getUsbEnable(){
        return usbEnable;
    } 

    public long getLogFrecuency(){
        return logFrecuency;
    }

    public void setUsbEnable(boolean usbEnable){
        this.usbEnable = usbEnable;
    } 

    public void setLogFrecuency(long logFrecuency){
        if(logFrecuency < 300000){
            return;
        }
        this.logFrecuency = logFrecuency;
    }
}
