package com.usbcommander.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.usbcommander.server.errors.MachineDisableException;
import com.usbcommander.server.utils.CommanderLogger;

import jakarta.annotation.PostConstruct;

@Component
public class SocketConnection {
    @Value("${usbcommander.socket.port}")
    public int port;
    
    @Autowired
    private CommanderLogger logger;
    
    @Autowired
    private ApplicationContext context;
    private ServerSocket socket;
    private ExecutorService threadPool;

    @PostConstruct
    public void init(){
        try {
            socket = new ServerSocket(port);
            threadPool = Executors.newCachedThreadPool();
            startServer();
        } catch (IOException e) {
            logger.writeLog(e.getMessage());
        }
    }

    public void startServer(){
        Thread process = new Thread(() -> {
            while(true){
                try {
                    Socket client = socket.accept();
                    Socket clientSocket = client;
                    MachineTalker talker = context.getBean(MachineTalker.class);
                    talker.setSocker(clientSocket);
                    talker.startToListen();
                } catch (IOException | MachineDisableException e) {
                    logger.writeLog(e.getMessage());
                }
            }
        });
        process.setDaemon(true);
        process.start();
    }

}
