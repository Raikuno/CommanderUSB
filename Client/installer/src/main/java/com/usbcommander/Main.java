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
        Optional<Boolean> uninstall = Optional.empty();

        for (String e : args) {
            if(e.startsWith(uninstallAbr) || e.startsWith(uninstallParam)){
                uninstall = Optional.of(true);
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

        

        try (Scanner key = new Scanner(System.in)) {

            if(installer.isInstalled() && uninstall.isEmpty()){
            System.out.println("An installation or patial installation was found. Do you want to remove it?(Y/N)");
                var answer = key.next().toLowerCase().trim().charAt(0);
                if(answer == 'y'){
                    uninstall = Optional.of(true);
                } else {
                    System.err.println("Cant install when an installation or partial installation is present");
                    return;
                }
            } else if(uninstall.isEmpty()){
                uninstall = Optional.of(false);
            }

            

            if (ip.isEmpty() && !uninstall.get()) {
                System.out.println("Insert valid ip:");
                var temp = key.next();
                if (temp.isEmpty() || !temp.matches(ipRegex)) {
                    throw new IllegalArgumentException("Ip not valid");
                }
                ip = Optional.of(temp);
            }

            if (port.isEmpty() && !uninstall.get()) {
                System.out.println("Insert valid port:");
                var tempPort = key.next();
                if (!tempPort.matches(portRegex)) {
                    throw new IllegalArgumentException("Port not valid");
                }
                port = Optional.of(Integer.valueOf(tempPort));
            }
        

            if(uninstall.get()){
                var retry = true;
                installer.stopScheduledTask();
                try{
                    installer.registryDeletion();
                }catch(Exception ex){
                    System.out.println("Eror on registry deletion");
                }
                try{
                    installer.logRegistryDeletion();
                }catch(Exception ex){
                    System.out.println("Eror on log registry deletion");
                }
                for(int i =0; i<2&&retry; i++){
                    try{
                    installer.deleteApplication();
                    retry = false;
                    }catch(Exception ex){
                        System.out.println("Eror on file deletion");
                    }
                }
                    return;
                }

            if (!installer.copyFile()) {
                System.err.println("Application files couldnt be copied.");
                return;
            }

            installer.registryCreation(ip.get(), port.get());
            installer.logRegistryCreation();
            installer.installScheduledTask();
        } catch (NumberFormatException e1) {
            System.err.println(e1.getMessage());
            return;
        }
    }
}