package com.usbcommander;

import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class AppConst {
    public static final HKEY MAIN_LOCATION = WinReg.HKEY_LOCAL_MACHINE;

    public static class ConfigReferences{
        public static final String CONFIG_LOCATION = "SOFTWARE\\UbCmmdr";
        public static final String USB_ALLOW_ENTRY = "allowUsb";
        public static final String LOG_ENTRY = "logFrec";
    }
    
    public static class RegistryReferences {
        public static final int ENABLE_VALUE = 3;
        public static final int DISABLE_VALUE = 4;
        public static final String USB_ENUM = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR\\Enum";
        public static final String USB_STOR = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR";
    }

}
