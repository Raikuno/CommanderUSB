package com.usbcommander;

import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class AppConst {
    public static final HKEY MAIN_LOCATION = WinReg.HKEY_LOCAL_MACHINE;

    public static class ConfigReferences{
        public static final String CONFIG_LOCATION = "SOFTWARE\\UsbCmmdr";
        public static final String USB_ALLOW_ENTRY = "allowUsb";
        public static final String LOG_ENTRY = "logFrec";
        public static final String IP_ENTRY = "ipdir";
        public static final String PORT_ENTRY = "port";
        public static final long MIN_FRECUENNCY = 300000;
    }
    
    public static class RegistryReferences {
        public static final int ENABLE_VALUE = 3;
        public static final int DISABLE_VALUE = 4;
        public static final String USB_ENUM = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR\\Enum";
        public static final String USB_STOR = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR";
    }

    public static class EventLogReferences{
        public static final String ENTRY_ROUTE = "SYSTEM\\CurrentControlSet\\Services\\EventLog\\USBCommanderLog";
        public static final String ENTRY_NAME = "UsbCommndr";
        public static final int INFO_CODE = 1001;
        public static final int UNAUTHORIZED_CONFIGURATION_MODIFICATION = 1002;
        public static final int REGISTRY_MODIFICATION = 1003;
        public static final int INCOHERENT_VALUE = 1004;
        public static final int MEMORY_CONNECTED = 1005;
        public static final int ERROR_CONNECTION_CODE = 1006;
        public static final int ERROR_CODE = 1007;
    }

    public static class ErrorMessages{
        public static final String SERVICE_NOT_RUNNING = "Could not complete task as the service is not running. Error Info: ";
        public static final String CONNECTION_ERROR_MESSAGE = "Could not complete task as there is no connection established. Error Info: ";
        public static final String JACKSON_ERROR_TEXT = "Error while creating text from class. Error Info: ";
        public static final String LOG_GENERATION_ERROR = "Couldn't read machine information, log values couldn't be set. Error Info: ";
        public static final String ERROR_DELETE = "Error deleting event log history. Error Info: ";
        public static final String WEIRDVALUE_ERROR = "WeirdValue found while parsing logs. Error Info: ";
    }
}
