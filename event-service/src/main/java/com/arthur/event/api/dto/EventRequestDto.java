package com.arthur.event.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDto {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private String location;
    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;
    @NotNull
    @Future
    private LocalDateTime endTime;
    @Positive
    private Integer capacity;
    @PositiveOrZero
    private BigDecimal price;
}
