package com.usbcommander.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.usbcommander.AppConst;
import com.usbcommander.managers.contract.IUsbMemoryManager;

/**
 * Implementación de IUsbMemoryManager para funcionar en un entorno de windows, mediante modificaciónes en el registro de windows y el uso de herramientas propias de este sistema operativo
 */
public class UsbMemoryManager extends IUsbMemoryManager{

    /**
     * Esta constante representa un valor de windows no presente en la librería de jna empleado a la hora de desmontar una unidad de memoria usb.
     */
    private final int IOCTL_STORAGE_EJECT_MEDIA = 0x2D4808;

    private UsbMemoryManager(){

    }

    /**
     * Método estático que construye una instancia de IUsbMemoryManager con la implementación definida en esta clase.
     * @return La instancia almacenada en la clase abstracta, inicializada como objeto de esta clase
     */
    public static IUsbMemoryManager getInstance(){
        if(instance == null){
            instance = new UsbMemoryManager();
        }
        return instance;
    }

    @Override
    public List<Map<String, String>> getConnectedDrives(){
        List<Map<String, String>> result = new ArrayList<>();
        String[] driveLetters = Kernel32Util.getLogicalDriveStrings().toArray(new String[0]);
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
    public int getAccessValue(){
        String keyValue = "Start";
        return Advapi32Util.registryGetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue);
        
    }

    @Override
    public void enableAccess(){
        String keyValue = "Start";
        Advapi32Util.registrySetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue, 
            AppConst.RegistryReferences.ENABLE_VALUE);

    }

    @Override
    public void disableAccess(){
        String keyValue = "Start";
        Advapi32Util.registrySetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue, 
            AppConst.RegistryReferences.DISABLE_VALUE);
    }

    
    @Override
    public void removeExternalDrives(){
        getConnectedDrives().stream()
        .map((v) -> {
            return v.get("DriveLetter");
        })
        .forEach((v) -> {
            try {
                removeDrive(v);
            } catch (Win32Exception e) {
                System.err.println("Failed to eject drive " + v + ": " + e.getMessage());
            }
        });
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
