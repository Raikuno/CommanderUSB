package com.usbcommander.server.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component

/**
 * Logger sencillo creado para informar de errores sucedidos en la aplicación
 */
public class CommanderLogger {
    @Value("${usbcommander.logger.route}")
    /**
     * La ruta en la que se almacenarán los logs
     */
    private String logRoute;
    /**
     * Mensaje de error a mostrar si el logger no puede iniciarse
     */
    private final String LOG_INIT_MSG = "ERROR WHILE INITIALIZING LOGGER. NO MESSAGE OR ERROR WILL BE LOGGED";
    /**
     * Mensaje de error a mostrar si no se puede escribir un log
     */
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

    /**
     * Método empleado para escrbir un mensaje en un archivo
     * @param message El mensaje a escribir
     */
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
