package com.usbcommander.services;

public class ThreadService {
    private static ThreadService instance;

    public static ThreadService getInstance(){
        if(instance == null){
            instance = new ThreadService();
        }
        return instance;
    }
}
