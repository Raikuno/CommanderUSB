package com.usbcommander;

import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Installer installer = new Installer();
        Optional<Integer> port = Optional.empty();
        Optional<String> ip = Optional.empty();
        String ipParam = "--ip=";
        String portParam = "--port=";
        String uninstallParam = "--uninstall";
        String uninstallAbr = "-u";
        String ipRegex = "(\\b25[0-5]|\\b2[0-4][0-9]|\\b[01]?[0-9][0-9]?)(\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}";
        String portRegex = "[0-9]{0,4}";
        boolean uninstall = false;

        for (String e : args) {
            if(e.startsWith(uninstallAbr) || e.startsWith(uninstallParam)){
                uninstall = true;
                break;
            }

            if (e.startsWith(ipParam)) {
                ip = Optional.of(e.substring(ipParam.length()));
                if (ip.isEmpty() || !ip.get().matches(ipRegex)) {
                    throw new IllegalArgumentException("Ip not valid");
                }
            }

            if (e.startsWith(portParam)) {
                var tempPort = (e.substring(portParam.length()));
                if (!tempPort.matches(portRegex)) {
                    throw new IllegalArgumentException("Port not valid");
                }
                port = Optional.of(Integer.valueOf(tempPort));
            }

        }

        if(uninstall){
            installer.uninstallService();
            installer.registryDeletion();
            installer.logRegistryDeletion();
            if(installer.deleteFile()){
                System.out.println("File deleted successfully.");
            } else {
                System.err.println("File could not be deleted.");
                return;
            }
            return;
        }
        try (Scanner key = new Scanner(System.in)) {
            if (ip.isEmpty()) {
                System.out.println("Insert valid ip:");
                var temp = key.next();
                if (temp.isEmpty() || !temp.matches(ipRegex)) {
                    throw new IllegalArgumentException("Ip not valid");
                }
                ip = Optional.of(temp);
            }

            if (port.isEmpty()) {
                System.out.println("Insert valid port:");
                var tempPort = key.next();
                if (!tempPort.matches(portRegex)) {
                    throw new IllegalArgumentException("Port not valid");
                }
                port = Optional.of(Integer.valueOf(tempPort));
            }
        } catch (NumberFormatException e1) {
            System.err.println(e1.getMessage());
            return;
        }

        if (!installer.copyFile()) {
            System.err.println("Application not found or could not be copied to folder.");
            return;
        }

        installer.registryCreation(ip.get(), port.get());
        installer.logRegistryCreation();
        installer.installService();
    }
}