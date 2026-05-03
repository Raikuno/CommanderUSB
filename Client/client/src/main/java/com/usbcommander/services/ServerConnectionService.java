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

/**
 * Servicio encargado de contolar la conexión con el servidor
 */
public class ServerConnectionService extends CommanderService{
    /**
     * Instancia del servicio
     */
    private static ServerConnectionService instance;
    /**
     * Mensaje de error a usar cuándo se produce un error al enviar información al servidor
     */
    private final String SENDING_ERROR = "Error while sending server message: ";
    /**
     * Mensaje de error a usar cuándo se produce un error al leer un mensaje del servidor
     */
    private final String SERVER_MSG_ERROR = "Error while reading server message";
    /**
     * Mensaje de error a usar cuándo se produce un error al establecer la conexión con el servidor
     */
    private final String CONN_ERROR = "Error while trying to stablish connection. Error Info: ";
    /**
     * Mensaje de error a usar cuándo se llega al número máximo de reintentos de conexíon
     */
    private final String MAX_RETRY_MSG = "Max connection attemps reached. Connection will be disable this session";
    /**
     * Número máximo de reintentos de conexión
     */
    private final int MAX_RETRY = 5;
    /**
     * Tiempo a esperar entre reintentos de conexión
     */
    private final long RECONECT_TIMEOUT = 30000;
    /**
     * Objeto socket encargado de conectarse al servidor
     */
    private Socket socket;
    /**
     * Instancia de IMachineConfig para obtener la dirección del servidor
     */
    private IMachineConfig machineConfig;
    /**
     * Instancia de IStatusManager para la creación de registros de error
     */
    private IStatusManager statusManager;
    /**
     * Variable encargada de almacenar si existe o no una conexión con el servidor
     */
    private boolean connected;
    /**
     * Instancia de jackson para serializar la información a enviar al servidor y deserializar la información enviada por este
     */
    private ObjectMapper mapper;

    /**
     * Constructor encargado de inicializar las propiedades del objeto
     */
    private ServerConnectionService(){
        machineConfig = MachineConfig.getInstance();
        statusManager = StatusManager.getInstance();
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        connectToServer();
    }

    /**
     * Método estático usado para inicializar y obtener la instancia de este servicio
     * @return La instancia inicializada del Servicio
     */
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

    /**
     * Método usado para llevar a cabo un intento de conexión con el servidor
     */
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

    /**
     * Método encargado de generar un hilo que escuche constantemente un mensaje del servidor para interpretarlo, llevando a cabo las instrucciones necesarias para cumplir aquellas descritas en el mensaje.
     * @return
     */
    private Thread listenerThread(){
        return new Thread(()->{
            boolean error = false;
            try {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                while(!error && running && connected){
                    String message = input.readUTF();
                    ConfigDTO newConfig = mapper.readValue(message, ConfigDTO.class);

                    if(newConfig.getFrecuency() != null){
                        SecurityService.getInstance().changeLogFrecuency(newConfig.getFrecuency());
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
            } catch (RuntimeException e) {
                statusManager.generateLog(LogType.ERROR, "Failed to apply config: " + e.getMessage());
            }
            
        });
    }

    /**
     * Método cuya función es la de enviar el mensaje descrito por parámetro al servidor
     * @param message El mensaje a enviar al servidor
     * @return Un booleano que expresa si la operación a tenido éxito o no
     * @throws ServiceDisabledException En el caso de que al momento de llamarse el método, este servicio no este activo
     * @throws NotConnectionException En el caso de que al momento de llamarse el método no exista una conexión con el servidor
     */
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
