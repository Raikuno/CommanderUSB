package com.usbcommander.config;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;

/**
 * Class with cerrtain values used in differents areas of the software.
 * Uses a singleton pattern to evade duplicated instances.
 */
public class MachineConfig extends IMachineConfig{
    private static final HKEY MAIN_LOCATION = AppConst.MAIN_LOCATION;
    private static final String CONFIG_LOCATION = AppConst.ConfigReferences.CONFIG_LOCATION;
    private static final String USB_ALLOW_ENTRY = AppConst.ConfigReferences.USB_ALLOW_ENTRY;
    private static final String LOG_ENTRY = AppConst.ConfigReferences.LOG_ENTRY;
    private static final String IP_ENTRY = AppConst.ConfigReferences.IP_ENTRY;
    private static final String PORT_ENTRY = AppConst.ConfigReferences.PORT_ENTRY;

    private boolean usbEnable;
    private long logFrecuency;
    private String ip;
    private int port;
    
    private MachineConfig(){
        this.usbEnable = false;
        this.logFrecuency = 300000;
        try{
            Advapi32Util.registryCreateKey(MAIN_LOCATION, CONFIG_LOCATION);
        }catch(Win32Exception err){
            System.out.println(err.getMessage());
        }
        
        saveConfig();
    }

    private MachineConfig(boolean usbEnable, long logFrecuency){
        this.usbEnable = usbEnable;
        this.logFrecuency = logFrecuency;
        //ip = "192.168.1.154";
        //port = 8067;
    }

    public static IMachineConfig getInstance(){
        if(instance == null){
            try{
                long logValue;
                instance = new MachineConfig(
                    Advapi32Util.registryGetIntValue(MAIN_LOCATION, CONFIG_LOCATION, USB_ALLOW_ENTRY ) == 0?false:true,
                    (logValue = Advapi32Util.registryGetLongValue(MAIN_LOCATION, CONFIG_LOCATION, LOG_ENTRY)) > 300000?
                        logValue:300000
                );
            }catch(Win32Exception err){
                instance = new MachineConfig();
                //TODO add a way to see the error throuugh a safe external source.
            }
        }

        return instance;
    }

    @Override
    public boolean saveConfig(){
        try{
            Advapi32Util.registrySetIntValue(MAIN_LOCATION, CONFIG_LOCATION, USB_ALLOW_ENTRY, usbEnable?1:0);
            Advapi32Util.registrySetLongValue(MAIN_LOCATION, CONFIG_LOCATION, LOG_ENTRY, logFrecuency);
            return true;
        }catch(Win32Exception err){
            return false;
        }
        
    }

    @Override
    public void enableServerService() {
        ip = Advapi32Util.registryGetStringValue(MAIN_LOCATION, CONFIG_LOCATION, IP_ENTRY);
        port = Advapi32Util.registryGetIntValue(MAIN_LOCATION, CONFIG_LOCATION, PORT_ENTRY);
    }

    @Override
    public boolean getUsbEnable(){
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
        if(logFrecuency < 300000){
            return;
        }
        this.logFrecuency = logFrecuency;
    }

}
