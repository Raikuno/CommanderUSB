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

    public static class FileRoutes{
        public static final String FOLDER_ROUTE = "C:\\Program Files\\UsbCommndr";
        public static final String LOG_FOLDER_ROUTE = "C:\\Program Files\\UsbCommndr\\Unsended";
    }

    public static class EventLogReferences{
        public static final String ENTRY_NAME = "UsbCmmndr";
        public static final int INFO_CODE = 1001;
        public static final int UNAUTHORIZED_CONFIGURATION_MODIFICATION = 1002;
        public static final int REGISTRY_MODIFICATION = 1003;
        public static final int INCOHERENT_VALUE = 1004;
        public static final int MEMORY_CONNECTED = 1005;
        public static final int ERROR_CODE = 1006;
    }
}
