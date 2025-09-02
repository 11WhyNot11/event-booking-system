package com.arthur.event.application;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.api.dto.EventResponseDto;
import com.arthur.event.domain.Event;
import com.arthur.event.infrastructure.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventResponseDto create(EventRequestDto dto) {
        Event entity = eventMapper.toEntity(dto);
        Event saved = eventRepository.save(entity);
        return eventMapper.toResponseDto(saved);
    }

    @Override
    public EventResponseDto getById(Long id) {
        Event event = getEventOrThrow(id);
        return eventMapper.toResponseDto(event);
    }

    @Override
    public List<EventResponseDto> getAllEvents() {
        return eventMapper.toResponseDtoList(eventRepository.findAll());
    }

    @Override
    @Transactional
    public EventResponseDto update(Long id, EventRequestDto dto) {
        Event event = getEventOrThrow(id);
        eventMapper.updateEntityFromDto(dto, event);
        return eventMapper.toResponseDto(event);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Event event = getEventOrThrow(id);
        eventRepository.deleteById(id);
    }

    private Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event " + id + " not found"));
    }
}
