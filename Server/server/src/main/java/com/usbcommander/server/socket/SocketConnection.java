package com.usbcommander.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.usbcommander.server.utils.CommanderLogger;

import jakarta.annotation.PostConstruct;

@Component
public class SocketConnection {
    @Value("${usbcommander.socket.port}")
    private int port;
    
    @Autowired
    private CommanderLogger logger;
    
    private ServerSocket socket;
    private ExecutorService threadPool;

    @PostConstruct
    public void init(){
        try {
            socket = new ServerSocket(port);
        } catch (IOException e) {
            logger.writeLog(e.getMessage());
            return;
        }
    }

}
