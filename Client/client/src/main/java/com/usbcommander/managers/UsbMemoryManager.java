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

public class UsbMemoryManager extends IUsbMemoryManager{

    /**
     * This constant represent a windows constant not present on the library jna. Is used to umount a drive
     */
    private final int IOCTL_STORAGE_EJECT_MEDIA = 0x2D4808;

    private UsbMemoryManager(){

    }

    public static IUsbMemoryManager getInstance(){
        if(instance == null){
            instance = new UsbMemoryManager();
        }
        return instance;
    }

    /**
     * This method checks the usb storage units connected to the machine and returns an array with the name, the drive letter assigned to the unit and the serial of every unit.
     * It does not need the registry value to be 'open'
     * @return An array with the name, the drive letter assigned to the unit and the serial of each connected usb storage unit
     */
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

    /**
     * Check the windows registry to see if there is any usb storage unit connected to the machine
     * @return An int repreesenting the number of usb storage units connected
     */
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

    /**
     * This method checks and returns the value saved on the windows registry related to the use of usb storage units
     * @return the value saved on the windows registry
     */
    @Override
    public int getAccessValue() throws Win32Exception{
        String keyValue = "Start";
        return Advapi32Util.registryGetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue);
        
    }

    /**
     * Change the registry value to allow the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    @Override
    public void enableAccess() throws Win32Exception{
        String keyValue = "Start";
        Advapi32Util.registrySetIntValue(
            AppConst.MAIN_LOCATION, 
            AppConst.RegistryReferences.USB_STOR, 
            keyValue, 
            AppConst.RegistryReferences.ENABLE_VALUE);

    }

    /**
     * Change the registry value to prevent the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    @Override
    public void disableAccess() throws Win32Exception{
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
            removeDrive(v);
        });
    }

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
