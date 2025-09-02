package com.arthur.event.application;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.api.dto.EventResponseDto;
import com.arthur.event.domain.Event;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventRequestDto eventRequestDto);
    EventResponseDto toResponseDto(Event event);
    List<EventResponseDto> toResponseDtoList(List<Event> events);
    void updateEntityFromDto(EventRequestDto dto, @MappingTarget Event entity);
}
