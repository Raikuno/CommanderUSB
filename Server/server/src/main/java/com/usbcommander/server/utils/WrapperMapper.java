package com.usbcommander.server.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.usbcommander.server.dto.ConfigDTO;
import com.usbcommander.server.dto.LogDTO;

@Component
/**
 * Clase encarrgada de envolver un objeto Jackson para facilitar su uso en la aplicación
 */
public class WrapperMapper {
    /**
     * El propio objeto de jackson
     */
    private final ObjectMapper mapper;

    public WrapperMapper(){
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Permite transformar un json en formato string a un objeto LogDTO
     * @param logJson El string que representa el json
     * @return El objeto LogDTO formado a partir del string del json
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public LogDTO stringToLogDTO(String logJson) throws JsonMappingException, JsonProcessingException{
        LogDTO log = mapper.readValue(logJson, LogDTO.class);
        return log;
    }
    /**
     * Permite transformar un json en formato string a una lista de LogDTO
     * @param logJson El string que representa el json
     * @return La lista de LogDTO formado a partir del string del json
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    public List<LogDTO> stringToLogDTOList(String logJson) throws JsonMappingException, JsonProcessingException{
        List<LogDTO> log = mapper.readValue(logJson, mapper.getTypeFactory().constructCollectionType(List.class, LogDTO.class));
        return log;
    }

    /**
     * Permite transformar un objeto de la clase ConfigDTO a json
     * @param configDTO El objeto ConfigDTO a conertir
     * @return Un string que representa el objeto configDTO en formato json
     * @throws JsonProcessingException
     */
    public String configdtoToString(ConfigDTO configDTO) throws JsonProcessingException {
        return mapper.writeValueAsString(configDTO);
    }
}
