package com.usbcommander.config;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class MachineConfig {
    private static final HKEY location = WinReg.HKEY_LOCAL_MACHINE;
    private static final String configLocation = "SOFTWARE\\ubcmmdr";
    private static final String usbAllowEntry = "allowUsb";
    private static final String logEntry = "logFrec";
    public static MachineConfig instance;
    private boolean usbEnable;
    private long logFrecuency;

    private MachineConfig(){}

    private MachineConfig(boolean usbEnable, long logFrecuency){
        this.usbEnable = usbEnable;
        this.logFrecuency = logFrecuency;
    }

    public static MachineConfig getInstance(){
        if(instance == null){
            try{
                long logValue;
                instance = new MachineConfig(
                    Advapi32Util.registryGetIntValue(location, configLocation, usbAllowEntry ) == 0?false:true,
                    (logValue = Advapi32Util.registryGetLongValue(location, configLocation, logEntry)) > 300000?
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
            Advapi32Util.registrySetIntValue(location, configLocation, "allowUsb", usbEnable?1:0);
            Advapi32Util.registrySetLongValue(location, configLocation, "logFrec", logFrecuency);
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
