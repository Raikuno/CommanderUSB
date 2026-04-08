package com.usbcommander;

import java.io.File;

import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.Winsvc.SC_HANDLE;
import com.sun.jna.platform.win32.Winsvc;

public class Installer {
    private final String SERVICE_NAME = "usbcmmndr";
    private final String SERVICE_DISPLAY_NAME = "USBCommander";
    private final String SERVICE_BINARY_PATH = "C:\\Program Files\\USBCommander\\usbcommander.exe";
    private final String JAR_INSTALLED_PATH = "C:\\Program Files\\USBCommander\\usbcommander.jar";
    private final String JAR_PATH = "application\\usbcommander.jar";
    private final String BINARY_INSTALLED_LOCATION = "C:\\Program Files\\USBCommander";
    private final String BINARY_PATH = "application\\usbcommander.exe";
    private final HKEY MAIN_LOCATION = WinReg.HKEY_LOCAL_MACHINE;
    private final String CONFIG_LOCATION = "SOFTWARE\\UsbCmmdr";
    private final String SOURCE_NAME = "UsbCommndr";
    private final String REG_PATH = "SYSTEM\\CurrentControlSet\\Services\\EventLog\\USBCommanderLog";
    private final String IP_ENTRY = "ipdir";
    private final String PORT_ENTRY = "port";
    private Advapi32 advapi32;

    public Installer(){
        advapi32 = Advapi32.INSTANCE;
    }
    
    public void registryCreation(String ip, int port){
        Advapi32Util.registryCreateKey(MAIN_LOCATION, CONFIG_LOCATION);
        Advapi32Util.registrySetIntValue(
            MAIN_LOCATION, 
            CONFIG_LOCATION, 
            PORT_ENTRY, 
            port);

        Advapi32Util.registrySetStringValue(
            MAIN_LOCATION, 
            CONFIG_LOCATION, 
            IP_ENTRY, 
            ip);
    }

    public void registryDeletion(){
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, CONFIG_LOCATION);
    }

    public void logRegistryCreation(){
        Advapi32Util.registryCreateKey(MAIN_LOCATION, REG_PATH);
        Advapi32Util.registryCreateKey(MAIN_LOCATION, REG_PATH + "\\" + SOURCE_NAME);
        Advapi32Util.registrySetIntValue(
            MAIN_LOCATION, REG_PATH + "\\" + SOURCE_NAME,
            "TypesSupported", 7
        );

        Advapi32Util.registrySetStringValue(
            WinReg.HKEY_LOCAL_MACHINE,
            REG_PATH + "\\" + SOURCE_NAME,
            "EventMessageFile",
            SERVICE_BINARY_PATH
        );
    }

    public void logRegistryDeletion(){
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, REG_PATH + "\\" + SOURCE_NAME);
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, REG_PATH);
    }


    public boolean  copyFile(){
        File file = new File(BINARY_PATH);
        File location = new File(BINARY_INSTALLED_LOCATION);
        File destination = new File(SERVICE_BINARY_PATH);
        File jarFile = new File(JAR_PATH);
        File jarDestination = new File(JAR_INSTALLED_PATH);
        if (!location.exists()) {
            boolean opResult = location.mkdirs();
            if (!opResult) {
                return false;
            }
        }
        return file.renameTo(destination) && jarFile.renameTo(jarDestination);
        
    }

    public boolean deleteFile(){
        File file = new File(SERVICE_BINARY_PATH);
        File jarFile = new File(JAR_INSTALLED_PATH);
        return file.delete() && jarFile.delete();
    }

    public void installService() {
        Winsvc.SC_HANDLE scm = Advapi32.INSTANCE.OpenSCManager(
            null,
            null,
            Winsvc.SC_MANAGER_ALL_ACCESS
        );

        if (scm == null) throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

        try {
            Winsvc.SC_HANDLE service = Advapi32.INSTANCE.CreateService(
                scm,
                SERVICE_NAME,                        
                SERVICE_DISPLAY_NAME,                
                Winsvc.SERVICE_ALL_ACCESS,           
                WinNT.SERVICE_WIN32_OWN_PROCESS,     
                WinNT.SERVICE_AUTO_START,            
                WinNT.SERVICE_ERROR_NORMAL,          
                SERVICE_BINARY_PATH,                 
                null,                                
                null,                                
                null,                                
                null,                                
                null                                 
            );

            if (service == null) throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

            System.out.println("Service installed successfully.");
            Advapi32.INSTANCE.CloseServiceHandle(service);

        } finally {
            Advapi32.INSTANCE.CloseServiceHandle(scm);
        }
    }

    public void uninstallService() {
        SC_HANDLE scheduler = advapi32.OpenSCManager(
            null, null, Winsvc.SC_MANAGER_ALL_ACCESS
        );

        if (scheduler == null) throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

        try {
            SC_HANDLE service = advapi32.OpenService(
                scheduler, SERVICE_NAME, Winsvc.SERVICE_ALL_ACCESS
            );

            if (service == null) throw new Win32Exception(Kernel32.INSTANCE.GetLastError());

            try {
                boolean deleted = advapi32.DeleteService(service);
                if (!deleted) throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                System.out.println("Service uninstalled successfully.");
            } finally {
                advapi32.CloseServiceHandle(service);
            }

        } finally {
            advapi32.CloseServiceHandle(scheduler);
        }
    }
    
}
