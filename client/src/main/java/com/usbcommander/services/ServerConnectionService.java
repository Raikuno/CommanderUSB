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
import com.usbcommander.errors.NotConnectionException;
import com.usbcommander.errors.ServiceDisabledException;
import com.usbcommander.managers.StatusManager;
import com.usbcommander.managers.contract.IStatusManager;
import com.usbcommander.services.contract.CommanderService;

public class ServerConnectionService extends CommanderService{
    private static ServerConnectionService instance;
    private final String SENDING_ERROR = "Error while sending server message: ";
    private final String SERVICE_ERROR_MESSAGE = AppConst.ErrorMessages.SERVICE_NOT_RUNNING;
    private final String CONNECTION_ERROR_MESSAGE = AppConst.ErrorMessages.CONNECTION_ERROR_MESSAGE;
    private final long RECONECT_TIMEOUT = 15000;
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
        while(running){
            while (!connected) {
                try {
                    Thread.sleep(RECONECT_TIMEOUT);
                } catch (InterruptedException e) {
                    statusManager.errorLog(e.getMessage());
                }
                connectToServer();
                
            }
        }
    }

    private void connectToServer(){
        try {
            socket = new Socket(machineConfig.getIp()
            ,machineConfig.getPort());
            connected = true;
            listenerThread().setDaemon(true);
            listenerThread().start();
        } catch (IOException e) {
            connected = false;
            statusManager.errorLog(e.getMessage());
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
                statusManager.errorLog("Error while reading server message \n" + e.getMessage());
                error = true;
                connected = false;
            } catch (ServiceDisabledException e) {
                statusManager.errorLog(SERVICE_ERROR_MESSAGE + e.getMessage());
            } 
            
        });
    }

    public void sendMessage(String message) throws ServiceDisabledException, NotConnectionException{
        if(!running){
            throw new ServiceDisabledException(SERVICE_ERROR_MESSAGE);
        }

        if(!connected){
            throw new NotConnectionException(CONNECTION_ERROR_MESSAGE);
        }

        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            statusManager.errorLog(SENDING_ERROR + e.getMessage());
            connected = false;
        }

    }


}
