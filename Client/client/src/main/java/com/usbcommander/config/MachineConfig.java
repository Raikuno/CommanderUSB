package com.usbcommander.config;
import com.google.inject.Singleton;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.usbcommander.AppConst;
import com.usbcommander.config.contract.IMachineConfig;

/**
 * Implementación de IMachineConfig para máquinas con windows como sistema operativo
 */
@Singleton 
public class MachineConfig extends IMachineConfig{
    /**
     * Valor que almacena si debería ser posible montar unidades de memoria usb extraibles en la máquina
     */
    private boolean usbEnable;
    /**
     * Valor que almacena la frecuencia con la que se envían los registros automáticos
     */
    private long logFrecuency;
    /**
     * Valor que almacena la ip del servidor
     */
    private String ip;
    /**
     * Valor que almacena el puerto del servidor
     */
    private Integer port;
    
    /**
     * Constructor que tratará de leer los archivos de configuración. Si no existen, se crearán con valores por defecto
     */
    public MachineConfig(){
        try{
            long logValue;
            this.usbEnable = Advapi32Util.registryGetIntValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION, 
                AppConst.ConfigReferences.USB_ALLOW_ENTRY ) == 0 ? false : true;

            this.logFrecuency = (logValue = Advapi32Util.registryGetLongValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION,
                AppConst.ConfigReferences.LOG_ENTRY)) > AppConst.ConfigReferences.MIN_FRECUENNCY ?
                logValue : AppConst.ConfigReferences.MIN_FRECUENNCY;
        }catch(Win32Exception err){
            this.usbEnable = false;
            this.logFrecuency = 300000;
            try{
                Advapi32Util.registryCreateKey(
                    AppConst.MAIN_LOCATION,
                    AppConst.ConfigReferences.CONFIG_LOCATION);
            }catch(Win32Exception createErr){
                System.out.println(createErr.getMessage());
            }
        }
        saveConfig();
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
