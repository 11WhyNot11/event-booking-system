package com.arthur.event.api.controller;

import com.arthur.event.api.dto.EventRequestDto;
import com.arthur.event.api.dto.EventResponseDto;
import com.arthur.event.application.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponseDto> create(@RequestBody @Valid EventRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(@PathVariable Long id,
                                                   @RequestBody @Valid EventRequestDto dto) {
        return ResponseEntity.ok(eventService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
