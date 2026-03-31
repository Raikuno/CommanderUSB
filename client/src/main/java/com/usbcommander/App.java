package com.usbcommander;
import com.usbcommander.services.ThreadService;

/**
 * Hello world!
 */
public class App {
    
    public static void main(String[] args) {
        /*
    Advapi32Util.registrySetIntValue(
        AppConst.MAIN_LOCATION, 
        AppConst.ConfigReferences.CONFIG_LOCATION, 
        AppConst.ConfigReferences.PORT_ENTRY, 
        8067);
    Advapi32Util.registrySetStringValue(
        AppConst.MAIN_LOCATION, 
        AppConst.ConfigReferences.CONFIG_LOCATION, 
        AppConst.ConfigReferences.IP_ENTRY, 
        "192.168.1.154");
         */
/*
    final String SOURCE_NAME = "UsbCommndr";
    final String REG_PATH =
        "SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application\\" + SOURCE_NAME;

        Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, REG_PATH);
        Advapi32Util.registrySetIntValue(
            WinReg.HKEY_LOCAL_MACHINE, REG_PATH,
            "TypesSupported", 7
        );
 */
        ThreadService.getInstance().start();
    }
}
