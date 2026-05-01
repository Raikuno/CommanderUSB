package com.usbcommander.config;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;


public class MachineConfig extends IMachineConfig{
    private boolean usbEnable;
    private long logFrecuency;
    private String ip;
    private Integer port;
    
    private MachineConfig(){
        this.usbEnable = false;
        this.logFrecuency = 300000;
        try{
            Advapi32Util.registryCreateKey(
                AppConst.MAIN_LOCATION, 
                AppConst.ConfigReferences.CONFIG_LOCATION);
        }catch(Win32Exception err){
            System.out.println(err.getMessage());
        }
        saveConfig();
    }

    private MachineConfig(boolean usbEnable, long logFrecuency){
        this.usbEnable = usbEnable;
        this.logFrecuency = logFrecuency;
    }

    public static IMachineConfig getInstance(){
        if(instance == null){
            try{
                long logValue;
                instance = new MachineConfig(
                    Advapi32Util.registryGetIntValue(
                        AppConst.MAIN_LOCATION,
                        AppConst.ConfigReferences.CONFIG_LOCATION, 
                        AppConst.ConfigReferences.USB_ALLOW_ENTRY ) == 0?false:true,

                    (logValue = Advapi32Util.registryGetLongValue(
                        AppConst.MAIN_LOCATION, 
                        AppConst.ConfigReferences.CONFIG_LOCATION, 
                        AppConst.ConfigReferences.LOG_ENTRY)) > AppConst.ConfigReferences.MIN_FRECUENNCY?
                        logValue:AppConst.ConfigReferences.MIN_FRECUENNCY
                );
            }catch(Win32Exception err){
                instance = new MachineConfig();
            }
        }

        return instance;
    }

    @Override
    public boolean saveConfig(){
        try{
            Advapi32Util.registrySetIntValue(
                AppConst.MAIN_LOCATION, 
                AppConst.ConfigReferences.CONFIG_LOCATION, 
                AppConst.ConfigReferences.USB_ALLOW_ENTRY, 
                usbEnable?1:0);

            Advapi32Util.registrySetLongValue(
                AppConst.MAIN_LOCATION, 
                AppConst.ConfigReferences.CONFIG_LOCATION, 
                AppConst.ConfigReferences.LOG_ENTRY, 
                logFrecuency);

            if(ip != null && port != null){
                Advapi32Util.registrySetIntValue(
                    AppConst.MAIN_LOCATION, 
                    AppConst.ConfigReferences.CONFIG_LOCATION, 
                    AppConst.ConfigReferences.PORT_ENTRY, 
                    port);

                Advapi32Util.registrySetStringValue(
                    AppConst.MAIN_LOCATION, 
                    AppConst.ConfigReferences.CONFIG_LOCATION, 
                    AppConst.ConfigReferences.IP_ENTRY, 
                    ip);
            }

            return true;
        }catch(Win32Exception err){
            return false;
        }
        
    }

    @Override
    public void enableServerService() {
        ip = Advapi32Util.registryGetStringValue(
            AppConst.MAIN_LOCATION, 
            AppConst.ConfigReferences.CONFIG_LOCATION, 
            AppConst.ConfigReferences.IP_ENTRY);

        port = Advapi32Util.registryGetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.ConfigReferences.CONFIG_LOCATION, 
            AppConst.ConfigReferences.PORT_ENTRY);
    }

    @Override
    public boolean isUsbEnable(){
        return usbEnable;
    } 

    @Override
    public long getLogFrecuency(){
        return logFrecuency;
    }

    @Override
    public String getIp(){
        return ip;
    }

    @Override
    public int getPort(){
        return port;
    }

    @Override
    public void setUsbEnable(boolean usbEnable){
        this.usbEnable = usbEnable;
    } 

    @Override
    public void setLogFrecuency(long logFrecuency){
        if(logFrecuency < AppConst.ConfigReferences.MIN_FRECUENNCY){
            return;
        }
        this.logFrecuency = logFrecuency;
    }

}
