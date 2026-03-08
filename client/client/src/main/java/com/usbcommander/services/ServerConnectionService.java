package com.usbcommander.services;

public class ServerConnectionService {
    private static ServerConnectionService instance;

    public static ServerConnectionService getInstance(){
        if(instance == null){
            instance = new ServerConnectionService();
        }
        return instance;
    }
}
