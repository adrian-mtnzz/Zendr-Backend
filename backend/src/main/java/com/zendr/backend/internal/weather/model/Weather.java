package com.zendr.backend.internal.weather.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "weather")
public class Weather {
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotNull(message = "La condición meteorológica es obligatoria")
    private String description;
    
    @NotNull(message = "El icono del clima es obligatorio")
    private String iconUrl;
    
    @NotNull(message = "Es obligatorío definir si las condiciones meteorológicas son aptas para realizar el evento")
    private boolean isAptOutdoors;
    
    @NotNull(message = "La fecha de última actualización es obligatoria")
    private Instant lastUpdate;
}
