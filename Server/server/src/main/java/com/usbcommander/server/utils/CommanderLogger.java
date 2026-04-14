package com.usbcommander.server.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CommanderLogger {
    @Value("${usbcommander.logger.route}")
    private String logRoute;
    private final String LOG_INIT_MSG = "ERROR WHILE INITIALIZING LOGGER. NO MESSAGE OR ERROR WILL BE LOGGED";
    private final String LOG_ERROR_MSG = "COULD NOT WRITE LOG: ";

    @PostConstruct
    public void init(){
        File temp = new File(logRoute);
        if(temp.exists()){
            return;
        }
        if(!temp.mkdirs()){
            System.err.println(LOG_INIT_MSG);
        }
    }

    public void writeLog(String message){
        try {
            FileWriter logFile = new FileWriter(logRoute + "/" + System.currentTimeMillis() + ".log");
            logFile.write(message);
            logFile.flush();
            logFile.close();
            
        } catch (IOException e) {
            System.err.println(LOG_ERROR_MSG + e.getMessage());
        }
        
    }
}
