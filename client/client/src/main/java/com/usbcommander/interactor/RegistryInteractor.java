package com.usbcommander.interactor;

import java.time.LocalDateTime;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class RegistryInteractor{
    private String key = "SYSTEM\\CurrentControlSet\\Services\\USBSTOR";
    private HKEY location = WinReg.HKEY_LOCAL_MACHINE;

    public RegistryInteractor(){
    }

    /**
     * This method checks wether the specified registry entry has been initialized
     * @return A boolean stating wether the registry entry has been created or not
     */
    public boolean isRegistryInit(){
        return false;
    }


    /**
     * This method checks the usb storage units connected to the machine and returns an array with the name of every unit.
     * It does not need the registry value to be 'open'
     * @return An array with the name of each connected usb storage unit
     */
    public String[] getConnectedDrives(){
        return new String[0];
    }

    /**
     * Check the windows registry to see if there is any usb storage unit connected to the machine
     * @return A boolean repreesenting if there is a usb storage unit connected
     */
    public boolean isDriveConnected(){
        return false;
    }

    /**
     * This method checks and returns the value saved on the windows registry related to the use of usb storage units
     * @return the value saved on the windows registry
     */
    public int getAccessValue(){
        String value = "Start";
        return Advapi32Util.registryGetIntValue(location, key, value);
    }

    /**
     * Change the registry value to allow the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public boolean enableAccess(){
        return false;
    }

    /**
     * Change the registry value to prevent the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public boolean disableAccess(){
        return false;
    }


    /**
     * Get the date and time of the last modification made to the windows registry
     * @return The last modification date of the windows registry entry related to the use of usb storage unit
     */
    public LocalDateTime getLastWriteTime(){
        return LocalDateTime.now();
    }
}
