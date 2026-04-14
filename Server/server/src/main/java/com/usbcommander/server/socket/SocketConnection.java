package com.usbcommander.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.usbcommander.server.utils.CommanderLogger;

import jakarta.annotation.PostConstruct;

@Component
public class SocketConnection {
    @Value("${usbcommander.socket.port}")
    public int port;
    
    @Autowired
    private CommanderLogger logger;
    
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
            boolean error = false;
            while(!error){  //TODO Use a better condition
                try {
                Socket client = socket.accept();
                threadPool.execute(() -> {
                    Socket clientSocket = client;
                    MachineTalker talker = new MachineTalker(clientSocket);
                    talker.register();
                    talker.startToListen();
                });
                } catch (IOException e) {
                    logger.writeLog(e.getMessage());
                    error = true;
                }
            }
        });
        process.setDaemon(true);
        process.start();
    }

}
