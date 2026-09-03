package com.usbcommander.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.usbcommander.AppConst;
import com.usbcommander.errors.UnallowedOperationException;
import com.usbcommander.managers.contract.IUsbMemoryManager;

/**
 * Implementación de IUsbMemoryManager para funcionar en un entorno de windows, mediante modificaciónes en el registro de windows y el uso de herramientas propias de este sistema operativo
 */
@Singleton
public class UsbMemoryManager extends IUsbMemoryManager{

    /**
     * Esta constante representa un valor de windows no presente en la librería de jna empleado a la hora de desmontar una unidad de memoria usb.
     */
    private final int IOCTL_STORAGE_EJECT_MEDIA = 0x2D4808;

    @Override
    public List<Map<String, String>> getConnectedDrives() throws UnallowedOperationException{
        List<Map<String, String>> result = new ArrayList<>();
        String[] driveLetters = new String[0];
        try{
             driveLetters = Kernel32Util.getLogicalDriveStrings().toArray(new String[0]);
                    for (String value:driveLetters) {
                            if (Kernel32.INSTANCE.GetDriveType(value) == WinBase.DRIVE_REMOVABLE) {
                                char[] volumeName  = new char[256];
                                IntByReference serialNumber = new IntByReference();

                                boolean success = Kernel32.INSTANCE.GetVolumeInformation(
                                    value,
                                    volumeName, 256,
                                    serialNumber,
                                    null, null,
                                    null, 0
                                );

                                if (success) {
                                    result.add(Map.of("DriveLetter", value.trim(), "Name", String.valueOf(volumeName).trim()));
                                }
                            }
                    }
        } catch (Win32Exception e) {
            throw new UnallowedOperationException("Failed to get connected drives " + e.getMessage());
        }
        return result;
    }

    @Override
    public int isDriveConnected(){
        int count = 0;
        boolean exit = false;

        while (!exit) {
            try{
                Advapi32Util.registryGetStringValue(
                    AppConst.MAIN_LOCATION, 
                    AppConst.RegistryReferences.USB_ENUM, 
                    String.valueOf(count));
                count += 1;
            }catch(Win32Exception err){
                exit = true;
            }
        }

        return count;
    }

    @Override
    public int getAccessValue() throws UnallowedOperationException{
        String keyValue = "Start";
        try{
            return Advapi32Util.registryGetIntValue(
                AppConst.MAIN_LOCATION, 
                AppConst.RegistryReferences.USB_STOR, 
                keyValue);
        }catch(Win32Exception err){
            throw new UnallowedOperationException("Failed to get USB access value: " + err.getMessage());
        }
        
    }

    @Override
    public void enableAccess() throws UnallowedOperationException{
        String keyValue = "Start";
        try{
        Advapi32Util.registrySetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue, 
            AppConst.RegistryReferences.ENABLE_VALUE);
        }catch(Win32Exception err){
            throw new UnallowedOperationException("Failed to enable USB access: " + err.getMessage());
        }

    }

    @Override
    public void disableAccess() throws UnallowedOperationException{
        String keyValue = "Start";
        try{
            Advapi32Util.registrySetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue, 
            AppConst.RegistryReferences.DISABLE_VALUE);
        }catch(Win32Exception err){
            throw new UnallowedOperationException("Failed to disable USB access: " + err.getMessage());
        }
        
    }

    
    @Override
    public void removeExternalDrives() throws UnallowedOperationException {
        for (Map<String, String> drive : getConnectedDrives()) {
            String driveLetter = drive.get("DriveLetter");
            try {
                removeDrive(driveLetter);
            } catch (Win32Exception e) {
                throw new UnallowedOperationException( "Failed to eject drive " + driveLetter + ": " + e.getMessage());
            }
        }
    }

    /**
     * Este método es usado a la hora de desmontar las unidades de memoria usb. Se encarga de desmontar una unidad de memoria usb en función de la letra asignada a esta.
     * @param drive La letra a la que la memoria usb esta asignada
     */
    private void removeDrive(String drive){
        String _drive = "\\\\.\\" + drive.substring(0,2);
        WinNT.HANDLE handler = Kernel32.INSTANCE.CreateFile(
            _drive,
            WinNT.GENERIC_READ | WinNT.GENERIC_WRITE,
            WinNT.FILE_SHARE_READ | WinNT.FILE_SHARE_WRITE,
            null,
            WinNT.OPEN_EXISTING,
            0,
            null
        );
        if (handler == WinBase.INVALID_HANDLE_VALUE) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }

        try {
            boolean ejected = Kernel32.INSTANCE.DeviceIoControl(
                handler,
                IOCTL_STORAGE_EJECT_MEDIA,
                null, 0, null, 0, null, null
            );

            if (!ejected) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }

        } finally {
            Kernel32.INSTANCE.CloseHandle(handler);
        }
    }
}
