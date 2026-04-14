package com.usbcommander.server.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.usbcommander.server.dto.LogDTO;
import com.usbcommander.server.utils.CommanderLogger;
import com.usbcommander.server.utils.WrapperMapper;

@Component
@Scope("prototype")
public class MachineTalker {
    private static Map<String, MachineTalker> machines = new HashMap<>();
    @Autowired
    private WrapperMapper mapper;
    @Autowired
    private CommanderLogger logger;
    private Socket socket;



    public MachineTalker(Socket socket){
        this.socket = socket;
    }

    public void register(){
        machines.put(socket.getInetAddress().toString(), this);
    }

    public void startToListen(){
        Thread listenerThread = new Thread(() -> {
            while (true) {  //TODO Use a better condition
                try {
                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String message = input.readUTF();
                    logger.writeLog("Debug message " + message);
                    List<LogDTO> log = mapper.stringToLogDTOList(message);
                    //TODO add a way to add the logs to the database
                } catch (IOException e) {
                    logger.writeLog(e.getMessage());
                }
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public boolean sendMessage(String message){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException e) {
            logger.writeLog(e.getMessage());
            return false;
        }
    }

    public static MachineTalker getMachineTalker(String ip){
        return machines.get(ip);
    }


}
