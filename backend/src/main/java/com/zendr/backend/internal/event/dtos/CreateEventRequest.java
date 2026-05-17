package com.zendr.backend.internal.event.dtos;

import com.zendr.backend.internal.event.model.EventCapacity;
import com.zendr.backend.internal.event.model.EventLocation;
import com.zendr.backend.internal.event.model.EventPriceDetails;
import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;


import java.time.Duration;
import java.time.Instant;

public record CreateEventRequest(
    
    @NotBlank(message = "El nombre del evento no puede estar vacío")
    String name,
    
    @NotBlank(message = "El nombre común del lugar no puede estar vacío")
    String placeCommonName,
    
    @NotBlank(message = "La dirección del evento no puede estar vacía")
    String address,
    
    @NotBlank(message = "La ciudad no puede estar vacía")
    String city,
    
    @NotBlank(message = "La región/provincia no puede estar vacía")
    String region,
    
    @NotBlank(message = "El código de país no puede estar vacío")
    String country,
    
    @NotBlank(message = "El código postal no puede estar vacío")
    String zip,
    
    @NotBlank(message = "La descripción del evento no puede estar vacía")
    String description,
    
    @NotBlank(message = "El ID del monitor no puede estar vacío")
    String userId,
    
    @NotBlank(message = "El ID de la disciplina no puede estar vacío")
    String disciplineId,
    
    @NotNull(message = "El nivel de la disciplina no puede estar vacío")
    String level,
    
    @NotNull(message = "La fecha y hora de inicio del evento no puede estar vacío")
    Instant startsAt,
    
    @NotNull
    @NotNull(message = "La duración no puede estar vacía")
    Duration duration,
    
    @Valid
    EventPriceDetails priceDetails,
    
    @Valid
    EventCapacity capacity,
    
    Double longitud,
    
    Double latitud
) {

}
