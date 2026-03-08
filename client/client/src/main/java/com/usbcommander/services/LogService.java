package com.usbcommander.services;

public class LogService {
    private static LogService instance;

    public static LogService getInstance(){
        if(instance == null){
            instance = new LogService();
        }
        return instance;
    }
}
