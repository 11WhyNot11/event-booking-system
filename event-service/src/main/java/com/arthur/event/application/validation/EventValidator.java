package com.arthur.event.application.validation;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.config.exception.BusinessValidationException;
import org.springframework.stereotype.Component;

@Component
public class EventValidator {

    public void validateStartTimeIsLessThanEndTime(EventRequestDto dto) {
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            if (!dto.getStartTime().isBefore(dto.getEndTime())) {
                throw new BusinessValidationException("startTime must be before endTime");
            }
        }
    }
}
