package com.usbcommander;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.platform.win32.WinReg.HKEY;

public class Installer {
    private final String TASK_NAME = "USBCommander";
    private final String EXECUTABLE_PATH = "C:\\Program Files\\USBCommander\\USBcommander.exe";
    private final String EXECUTABLE_NAME = "USBcommander.exe";
    private final String BINARY_INSTALLED_LOCATION = "C:\\Program Files\\USBCommander";
    private final String APP_IMAGE_SOURCE = "application\\USBcommander";
    private final HKEY MAIN_LOCATION = WinReg.HKEY_LOCAL_MACHINE;
    private final String CONFIG_LOCATION = "SOFTWARE\\UsbCmmdr";
    private final String SOURCE_NAME = "UsbCommndr";
    private final String REG_PATH = "SYSTEM\\CurrentControlSet\\Services\\EventLog\\USBCommanderLog";
    private final String IP_ENTRY = "ipdir";
    private final String PORT_ENTRY = "port";

    public Installer(){
    }

    public boolean isInstalled(){
        if(new File(BINARY_INSTALLED_LOCATION).exists() 
            || Advapi32Util.registryKeyExists(MAIN_LOCATION, CONFIG_LOCATION)
            || Advapi32Util.registryKeyExists(MAIN_LOCATION, REG_PATH + "\\" + SOURCE_NAME)){
            return true;
        }
        return false;
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
        System.out.println("Registry entries for configuration created");
    }

    public void registryDeletion(){
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, CONFIG_LOCATION);
        Advapi32Util.registrySetIntValue(
            WinReg.HKEY_LOCAL_MACHINE, 
            "SYSTEM\\CurrentControlSet\\Services\\USBSTOR", 
            "Start", 
            3);
        System.out.println("Registry configuration deleted");
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
            EXECUTABLE_PATH
        );
        System.out.println("Registry entries for log functionality created");
    }

    public void logRegistryDeletion(){
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, REG_PATH + "\\" + SOURCE_NAME);
        Advapi32Util.registryDeleteKey(MAIN_LOCATION, REG_PATH);
        System.out.println("Log registry entry deleted");
    }


    public boolean copyFile() {
        Path source = Paths.get(APP_IMAGE_SOURCE);
        Path destination = Paths.get(BINARY_INSTALLED_LOCATION);
        try {
            Files.walk(source).forEach(src -> {
                Path dest = destination.resolve(source.relativize(src));
                try {
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failed to copy: " + src, e);
                }
            });
            return true;
        } catch (IOException | RuntimeException e) {
            System.err.println("Copy failed: " + e.getMessage());
            return false;
        }
    }

    public void deleteApplication() {
        try {
            Path path = Paths.get(BINARY_INSTALLED_LOCATION);
            File folder = path.toFile();
            deleteFiles(folder);
            folder.delete();
            System.out.println("Aplication delted");
        } catch (Exception e) {
            System.err.println("Delete failed: " + e.getMessage());
        }
    }

    private void deleteFiles(File file){
        for(File e: file.listFiles()){
            if(e.isDirectory()){
                deleteFiles(e);
            }
            e.delete();
        }
    }

    public void installScheduledTask() {
        String full = "schtasks /Create"
            + " /TN " + TASK_NAME
            + " /TR \"\\\"" + EXECUTABLE_PATH + "\\\"\""
            + " /SC ONSTART"
            + " /RL HIGHEST"
            + " /RU SYSTEM"
            + " /F";

        int code = run("cmd.exe", "/c", full);
        if (code != 0) {
            throw new RuntimeException("Task could not be created " + code);
        }
        System.out.println("Scheduled task installed successfully.");
        configureRestartOnFailure();
    }

    private void configureRestartOnFailure() {
        String script = "$t=Get-ScheduledTask -TaskName '" + TASK_NAME + "';"
            + "$t.Settings.RestartCount=999;"
            + "$t.Settings.RestartInterval='PT1M';"
            + "$t.Settings.MultipleInstances='IgnoreNew';"
            + "Set-ScheduledTask -InputObject $t | Out-Null";

        int code = run(
            "C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",
            "-NoProfile", "-NonInteractive",
            "-Command", script
        );
        if (code != 0) {
            throw new RuntimeException(
                "Task could not be configured to be able to restart on failure");
        }
    }

    public void stopScheduledTask() {
        run("schtasks", "/Delete", "/TN", TASK_NAME, "/F");
        run("taskkill", "/IM", EXECUTABLE_NAME, "/F");
        System.out.println("Task deleted");
    }

    private int run(String... cmd) {
        try {
            return new ProcessBuilder(cmd).inheritIO().start().waitFor();
        } catch (IOException e) {
            throw new RuntimeException("Failed to run script: " + String.join(" ", cmd), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while running script: " + String.join(" ", cmd), e);
        }
    }
}
