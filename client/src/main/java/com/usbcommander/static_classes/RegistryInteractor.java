package com.usbcommander.static_classes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;

public abstract class RegistryInteractor{
    private static final int ENABLE_VALUE = 3;
    private static final int DISABLE_VALUE = 4;
    private static final String usbenum = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR\\Enum";
    private static final String usbstor = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR";
    private static final HKEY location = WinReg.HKEY_LOCAL_MACHINE;

    /**
     * This method checks the usb storage units connected to the machine and returns an array with the name, the drive letter assigned to the unit and the serial of every unit.
     * It does not need the registry value to be 'open'
     * @return An array with the name, the drive letter assigned to the unit and the serial of each connected usb storage unit
     */
    public static Object[] getConnectedDrives(){
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
                        result.add(Map.of("DriveLetter", value, "Name", String.valueOf(volumeName), "Serial", String.valueOf(serialNumber)));
                    }
                }
        }

        return result.toArray();
    }

    /**
     * Check the windows registry to see if there is any usb storage unit connected to the machine
     * @return An int repreesenting the number of usb storage units connected
     */
    public static int isDriveConnected(){
        int count = 0;
        boolean exit = false;

        while (!exit) {
            try{
                Advapi32Util.registryGetStringValue(location, usbenum, String.valueOf(count));
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
    public static int getAccessValue(){
        String keyValue = "Start";
        try{
            return Advapi32Util.registryGetIntValue(location, usbstor, keyValue);
        }catch(Win32Exception err){
            //TODO Add a way to save the error on a log
            return -1;
        }
        
    }

    /**
     * Change the registry value to allow the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public static boolean enableAccess(){
        String keyValue = "Start";
        try{
            Advapi32Util.registrySetIntValue(location, usbstor, keyValue, ENABLE_VALUE);
            return getAccessValue() == ENABLE_VALUE;
        }catch(Win32Exception err){
            //TODO Add a way to save the error on a log
            return false;
        }

    }

    /**
     * Change the registry value to prevent the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public static boolean disableAccess(){
        String keyValue = "Start";
        try{
            Advapi32Util.registrySetIntValue(location, usbstor, keyValue, DISABLE_VALUE);
            return getAccessValue() == DISABLE_VALUE;
        }catch(Win32Exception err){
            //TODO Add a way to save the error on a log
            return false;
        }
    }


    /**
     * Get the date and time of the last modification made to the windows registry
     * @return The last modification date of the windows registry entry related to the use of usb storage unit
     */
    public static LocalDateTime getLastWriteTime(){
        return LocalDateTime.now();
    }
}
