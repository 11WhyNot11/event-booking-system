package com.arthur.event.application;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.api.dto.EventResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventResponseDto create(EventRequestDto dto);
    EventResponseDto getById(Long id);
    List<EventResponseDto> getAllEvents();
    Page<EventResponseDto> list(String q,
                                String location,
                                LocalDateTime dateFrom,
                                LocalDateTime dateTo,
                                Pageable pageable);
    EventResponseDto update(Long id, EventRequestDto dto);
    void delete(Long id);
}
