package com.arthur.event.application.validation;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.config.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validateStartTimeIsLessThanEndTime(EventRequestDto dto) {
        if(!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BusinessValidationException("invalid event time range");
        }
    }
}
