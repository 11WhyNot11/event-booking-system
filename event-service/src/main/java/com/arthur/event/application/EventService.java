package com.arthur.event.application;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.api.dto.EventResponseDto;

import java.util.List;

public interface EventService {
    EventResponseDto create(EventRequestDto dto);
    EventResponseDto getById(Long id);
    List<EventResponseDto> getAllEvents();
    EventResponseDto update(Long id, EventRequestDto dto);
    void delete(Long id);
}
