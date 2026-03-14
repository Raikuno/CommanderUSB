package com.usbcommander.managers;

import java.util.Map;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

public class DriveManager{
    /**
     * This constant represent a windows constant not present on the library jna. Is used to umount a drive
     */
    private static final int IOCTL_STORAGE_EJECT_MEDIA = 0x2D4808;

    /**
     * This method checks every drive detected by UsbRegistryManager and try to eject them.
     */
    public static void removeExternalDrives(){
        UsbRegistryManager.getConnectedDrives().stream()
        .map((v) -> {
            return ((Map<String, String>)v).get("DriveLetter");
        })
        .forEach((v) -> {
            removeDrive(v);
        });
    }

    private static void removeDrive(String drive){
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