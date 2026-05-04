package com.usbcommander.server.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.usbcommander.server.errors.MachineDisableException;
import com.usbcommander.server.utils.CommanderLogger;

import jakarta.annotation.PostConstruct;

@Component
/**
 * Clase encargada de administrar las conexiones con los clientes, creando instancias de MachineTalker por cada conexión 
 */
public class SocketConnection {
    @Value("${usbcommander.socket.port}")
    /**
     * Port that will be dedicated to the socket connections with the clients
     */
    private int port;
    
    @Autowired
    private CommanderLogger logger;
    
    @Autowired
    private ApplicationContext context;
    /**
     * El objeto que representa el socket de servidor que será abierto para permitir las conexiones
     */
    private ServerSocket socket;

    @PostConstruct
    /**
     * Método a ejecutar tras la ejecución del constructor de la clase.
     * Inicializa el ServerSocket e inicia el bucle para detectar conexiones
     */
    public void init(){
        try {
            socket = new ServerSocket(port);
            startServer();
        } catch (IOException e) {
            logger.writeLog(e.getMessage());
        }
    }

    /**
     * Método empleado parra controlar las conexiones con los clientes, generando una instancia de MachineTalker por cada conexión
     */
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
