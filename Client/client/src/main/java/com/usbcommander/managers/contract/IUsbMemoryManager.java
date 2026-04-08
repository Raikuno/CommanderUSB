package com.usbcommander.managers.contract;

import java.util.List;
import java.util.Map;

import com.sun.jna.platform.win32.Win32Exception;

public abstract class IUsbMemoryManager {
    protected static IUsbMemoryManager instance;
    
    protected IUsbMemoryManager(){}

    /**
     * This method checks the usb storage units connected to the machine and returns an array with the name, the drive letter assigned to the unit and the serial of every unit.
     * It does not need the registry value to be 'open'
     * @return An array with the name, the drive letter assigned to the unit and the serial of each connected usb storage unit
     */
    public abstract List<Map<String, String>> getConnectedDrives();

    /**
     * Check the windows registry to see if there is any usb storage unit connected to the machine
     * @return An int repreesenting the number of usb storage units connected
     */
    public abstract int isDriveConnected();

    /**
     * This method checks and returns the value saved on the windows registry related to the use of usb storage units
     * @return the value saved on the windows registry
     */
    public abstract int getAccessValue() throws Win32Exception;

    /**
     * Change the registry value to allow the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public abstract void enableAccess() throws Win32Exception;

    /**
     * Change the registry value to prevent the machine to read the usb storage units connected
     * @return A boolean representing if the instruction was succesful
     */
    public abstract void disableAccess() throws Win32Exception;

    public abstract void removeExternalDrives() throws Win32Exception;
}