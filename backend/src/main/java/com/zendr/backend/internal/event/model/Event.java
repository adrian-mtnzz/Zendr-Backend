package com.zendr.backend.internal.event.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotBlank(message = "El nombre del evento no puede estar vacío")
    private String name;
    
    @NotBlank(message = "El nombre común del lugar no puede estar vacío")
    private String placeCommonName;
    
    @NotBlank(message = "La dirección del evento no puede estar vacía")
    private String address;
    
    @NotBlank(message = "La ciudad no puede estar vacía")
    private String city;
    
    @NotBlank(message = "La región/provincia no puede estar vacía")
    private String region;
    
    @NotBlank(message = "El país no puede estar vacío")
    private String country;
    
    @NotBlank(message = "El código postal no puede estar vacío")
    private String zip;
    
    @NotBlank(message = "La descripción del evento no puede estar vacía")
    private String description;
    
    @NotBlank(message = "El ID de usuario no puede estar vacío")
    private String monitorId;
    
    @NotBlank(message = "El ID de la disciplina no puede estar vacío")
    private String disciplineId;
    
    @NotBlank(message = "El ID del clima no puede estar vacío")
    private String weatherId;
    
    @NotBlank(message = "El ID de la lista de espera no puede estar vacío")
    private String waitListId;
    
    @Setter(AccessLevel.NONE)
    @NotNull(message = "La fecha y hora de inicio del evento no puede estar vacío")
    private Instant startsAt;
    
    @Setter(AccessLevel.NONE)
    @NotNull(message = "La duración no puede estar vacía")
    private Duration duration;
    
    @Setter(AccessLevel.NONE)
    private Instant endsAt;
    
    @Valid
    private EventLocation location;
    
    @Valid
    private EventPriceDetails priceDetails;
    
    @Valid
    private EventCapacity capacity;
    
    @Setter(AccessLevel.NONE)
    private String search;
    
    @Builder
    public Event(
            String id, String name, String placeCommonName, String address, String description,
            String monitorId, String disciplineId, String weatherId, String waitListId,
            Instant startsAt, Duration duration, EventLocation location,
            EventPriceDetails priceDetails, EventCapacity capacity, String search) {
       
        this.id = id;
        this.name = name;
        this.placeCommonName = placeCommonName;
        this.address = address;
        this.description = description;
        this.monitorId = monitorId;
        this.disciplineId = disciplineId;
        this.weatherId = weatherId;
        this.waitListId = waitListId;
        this.startsAt=  startsAt;
        this.duration = duration;
        this.endsAt = startsAt.plus(duration);
        this.location = location;
        this.priceDetails = priceDetails;
        this.capacity = capacity;
        this.search = this.name+" "+this.placeCommonName+" "+this.address+" "+this.city+" "+this.region;
    }
}

