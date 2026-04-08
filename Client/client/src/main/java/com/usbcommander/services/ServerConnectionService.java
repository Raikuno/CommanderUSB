package com.usbcommander.services;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usbcommander.AppConst;
import com.usbcommander.config.MachineConfig;
import com.usbcommander.config.contract.IMachineConfig;
import com.usbcommander.dto.ConfigDTO;
import com.usbcommander.enums.LogType;
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.services.contract.CommanderService;

public class ServerConnectionService extends CommanderService{
    private static ServerConnectionService instance;
    private final String SENDING_ERROR = "Error while sending server message: ";
    private final String SERVER_MSG_ERROR = "Error while reading server message";
    private final String CONN_ERROR = "Error while trying to stablish connection. Error Info: ";
    private final String MAX_RETRY_MSG = "Max connection attemps reached. Connection will be disable this session";
    private final int MAX_RETRY = 5;
    private final long RECONECT_TIMEOUT = 30000;
    private Socket socket;
    private IMachineConfig machineConfig;
    private IStatusManager statusManager;
    private boolean connected;
    private ObjectMapper mapper;

    private ServerConnectionService(){
        machineConfig = MachineConfig.getInstance();
        statusManager = StatusManager.getInstance();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        connectToServer();
    }

    public static ServerConnectionService getInstance(){
        if(instance == null){
            instance = new ServerConnectionService();
        }
        return instance;
    }

    @Override
    public void run() {
        int retry_count = 0;  
        while(running){
            while (!connected && retry_count != MAX_RETRY) {
                try {
                    Thread.sleep(RECONECT_TIMEOUT);
                    connectToServer();
                    retry_count += 1;
                    if (retry_count == MAX_RETRY) {
                        statusManager.generateLog(LogType.CONNECTION, MAX_RETRY_MSG);
                    }
                } catch (InterruptedException e) {
                    statusManager.generateLog(LogType.ERROR, e.getMessage());
                    running = false;
                }
            }
            retry_count = 0;
        }
    }

    private void connectToServer(){
        try {
            socket = new Socket(machineConfig.getIp(), machineConfig.getPort());
            
            if(socket.isConnected()){
                connected = true;
                listenerThread().setDaemon(true);
                listenerThread().start();
            }
            
        } catch (IOException e) {
            connected = false;
            statusManager.generateLog(LogType.CONNECTION, CONN_ERROR + e.getMessage());
        }
    }

    public Thread listenerThread(){
        return new Thread(()->{
            boolean error = false;
            try {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                while(!error && running && connected){
                    String message = input.readUTF();
                    ConfigDTO newConfig = mapper.readValue(message, ConfigDTO.class);

                    if(newConfig.getFrecuency() != null){
                        LogService.getInstance().changeLogFrecuency(newConfig.getFrecuency());
                    }

                    if(newConfig.isAllow() != null && newConfig.isAllow() && newConfig.getAllowedTime() != null){
                        SecurityService.getInstance().openAccess(newConfig.getAllowedTime());
                    }

                    if(newConfig.isAllow() != null && !newConfig.isAllow()){
                        SecurityService.getInstance().forceClose();
                    }
                    
                }
            }
            catch (IOException e) {
                statusManager.generateLog(LogType.CONNECTION, SERVER_MSG_ERROR + e.getMessage());
                error = true;
                connected = false;
            } catch (ServiceDisabledException e) {
                statusManager.generateLog(LogType.ERROR,e.getMessage());
            } 
            
        });
    }

    public boolean sendMessage(String message) throws ServiceDisabledException, NotConnectionException{
        if(!running){
            throw new ServiceDisabledException(AppConst.ErrorMessages.SERVICE_NOT_RUNNING);
        }

        if(!connected){
            throw new NotConnectionException(AppConst.ErrorMessages.CONNECTION_ERROR_MESSAGE);
        }

        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
            return true;
        } catch (IOException e) {
            statusManager.generateLog(LogType.ERROR, SENDING_ERROR + e.getMessage());
            connected = false;
            return false;
        }

    }


}
