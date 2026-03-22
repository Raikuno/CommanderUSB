package com.usbcommander;
import com.usbcommander.services.ThreadService;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
/*
    final String SOURCE_NAME = "UsbCommndr";
    final String REG_PATH =
        "SYSTEM\\CurrentControlSet\\Services\\EventLog\\Application\\" + SOURCE_NAME;

        Advapi32Util.registryCreateKey(WinReg.HKEY_LOCAL_MACHINE, REG_PATH);
        Advapi32Util.registrySetIntValue(
            WinReg.HKEY_LOCAL_MACHINE, REG_PATH,
            "TypesSupported", 7
        );
 */
        ThreadService.getInstance().start();
    }
}
