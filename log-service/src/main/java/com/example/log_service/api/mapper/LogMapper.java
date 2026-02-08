package com.example.log_service.api.mapper;

import com.example.log_service.api.dto.LogBackendRequest;
import com.example.log_service.api.dto.LogFrontendRequest;
import com.example.log_service.api.dto.LogStringFormat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface LogMapper {
    LogStringFormat toStringFormat(LogFrontendRequest rawLog);

    @Mapping(target = "log", expression = "java(mapToStringFormat(rawLog))")
    LogStringFormat toStringFormat(LogBackendRequest rawLog);

    default String mapToStringFormat(LogBackendRequest rawLog) {
        return rawLog.toString();
    }
}
