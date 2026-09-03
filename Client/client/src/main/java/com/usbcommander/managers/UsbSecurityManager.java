package com.usbcommander.managers;

import java.util.function.Consumer;

import com.google.inject.Singleton;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import com.usbcommander.AppConst;
import com.usbcommander.managers.contract.IUsbSecurityManager;

@Singleton 
public class UsbSecurityManager extends IUsbSecurityManager{

    @Override 
    public Thread setUsbListener(Runnable execute, Consumer<Exception> onCatch){
        return setListenerOn(AppConst.RegistryReferences.USB_STOR, execute, onCatch);
    };

    @Override
    public Thread setConfigListener(Runnable execute, Consumer<Exception> onCatch){
        return setListenerOn(AppConst.ConfigReferences.CONFIG_LOCATION, execute, onCatch);
    };

    /**
     * Método empleador para facilitar la creación de Threads encargados de supervisar entradas en el registro de windows
     * @param route La ruta del valor del registro en la cuál se va a crear el hilo de supervisión
     * @param onChannge La función a ejecutar cuando se detectan cambios en el registro
     * @param onCatch La función a ejecutar si se lanza una excepción al momento de lanzar el hilo
     * @return El hilo de supervisión
     */
    private Thread setListenerOn(String route, Runnable onChannge, Consumer<Exception> onCatch){
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
        
                while(true /*runninng */){
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

    @Override 
    public boolean configMatchesMemory(boolean isUsbEnable, long logFrecuency, String ip, Integer port){
        try{
            int regUsb = Advapi32Util.registryGetIntValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION,
                AppConst.ConfigReferences.USB_ALLOW_ENTRY);
            if((regUsb == 1) != isUsbEnable){
                return false;
            }

            long regLog = Advapi32Util.registryGetLongValue(
                AppConst.MAIN_LOCATION,
                AppConst.ConfigReferences.CONFIG_LOCATION,
                AppConst.ConfigReferences.LOG_ENTRY);
            if(regLog != logFrecuency){
                return false;
            }

            if(ip != null){
                String regIp = Advapi32Util.registryGetStringValue(
                    AppConst.MAIN_LOCATION,
                    AppConst.ConfigReferences.CONFIG_LOCATION,
                    AppConst.ConfigReferences.IP_ENTRY);
                int regPort = Advapi32Util.registryGetIntValue(
                    AppConst.MAIN_LOCATION,
                    AppConst.ConfigReferences.CONFIG_LOCATION,
                    AppConst.ConfigReferences.PORT_ENTRY);
                if(!ip.equals(regIp) || regPort != port){
                    return false;
                }
            }
            return true;
        }catch(Win32Exception err){
            return false;
        }
    }

    @Override
    public boolean usbStorMatchesMemory(boolean isUsbEnable, int usbAccessValue){
        int expected = isUsbEnable
            ? AppConst.RegistryReferences.ENABLE_VALUE
            : AppConst.RegistryReferences.DISABLE_VALUE;
        return usbAccessValue == expected;
    }
}
