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
public class WrapperMapper {
    private final ObjectMapper mapper;

    public WrapperMapper(){
        mapper = new ObjectMapper().registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public LogDTO stringToLogDTO(String logJson) throws JsonMappingException, JsonProcessingException{
        LogDTO log = mapper.readValue(logJson, LogDTO.class);
        return log;
    }

    public List<LogDTO> stringToLogDTOList(String logJson) throws JsonMappingException, JsonProcessingException{
        List<LogDTO> log = mapper.readValue(logJson, mapper.getTypeFactory().constructCollectionType(List.class, LogDTO.class));
        return log;
    }

    public String configdtoToString(ConfigDTO configDTO) throws JsonProcessingException {
        return mapper.writeValueAsString(configDTO);
    }
}
