package com.zendr.backend.internal.event.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Data
@NoArgsConstructor
@Document(collection = "events")
public class Event {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotBlank(message = "La imagen del evento no puede estar vacía")
    private String eventImgUrl;
    
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
    private String countryCode;
    
    @NotBlank(message = "El código postal no puede estar vacío")
    private String zip;
    
    @NotBlank(message = "La descripción del evento no puede estar vacía")
    private String description;
    
    @NotBlank(message = "El ID del monitor no puede estar vacío")
    private String monitorId;
    
    @NotBlank(message = "El ID de la disciplina no puede estar vacío")
    private String disciplineId;
    
    @NotNull(message = "El nivel de la disciplina no puede estar vacío")
    private FavDisciplinesCurrentLevel level;
    
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
    
    @NotNull
    private EventStatus status;
    
    @Setter(AccessLevel.NONE)
    private String search;
    
    
    @Getter
    @RequiredArgsConstructor
    public enum EventStatus {
            
            ACTIVE("Activo"),
            ONGOING("En curso"),
            ENDED("Finalizado"),
            CANCELLED("Cancelado");
        
        @JsonValue
        private final String description;
    
    }
    @Builder
    public Event(
            String id, String name, String placeCommonName, String address, String city, String region,
            String countryCode, String zip, String description, String monitorId, String disciplineId,
            String level, String weatherId, String waitListId, Instant startsAt, Duration duration,
            EventLocation location, EventPriceDetails priceDetails, EventCapacity capacity, String eventImgUrl,
            String search) {
       
        this.id = id;
        this.name = name;
        this.placeCommonName = placeCommonName;
        this.address = address;
        this.city = city;
        this.region = region;
        this.countryCode = countryCode;
        this.zip = zip;
        this.description = description;
        this.monitorId = monitorId;
        this.disciplineId = disciplineId;
        this.level = FavDisciplinesCurrentLevel.valueOf(level);
        this.weatherId = weatherId;
        this.waitListId = waitListId;
        this.startsAt=  startsAt;
        this.duration = duration;
        this.endsAt = startsAt.plus(duration);
        this.location = location;
        this.priceDetails = priceDetails;
        this.capacity = capacity;
        this.status = EventStatus.ACTIVE;
        this.eventImgUrl = eventImgUrl;
        this.search = buildSearchField();
    
    }
    
    private String buildSearchField() {
        
        return String.join(" ",
                Objects.toString(name, ""),
                Objects.toString(placeCommonName, ""),
                Objects.toString(address, ""),
                Objects.toString(city, ""),
                Objects.toString(region, ""),
                Objects.toString(countryCode, "")
        ).trim();
    }
}

