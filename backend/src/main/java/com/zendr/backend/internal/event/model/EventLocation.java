package com.zendr.backend.internal.event.model;

import com.mongodb.client.model.geojson.GeoJsonObjectType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLocation {
    
    @NotNull(message = "El tipo de coordenadas no puede ser nulo")
    private GeoJsonObjectType coordsType;
    
    @NotNull(message = "Las coordenadas no pueden ser nulas")
    private Coordinates coords;
    
    public EventLocation(String coordsType, double[] coords) {
        
        if (coords == null || coords.length != 2) {
            throw new IllegalArgumentException(
                    "Las coordenadas deben tener exactamente 2 elementos"
            );
        }
        
        this.coordsType = GeoJsonObjectType.valueOf(coordsType);
        this.coords = new Coordinates(
                coords[0],
                coords[1]
        );
    }
    
    public record Coordinates(
            double longitud,
            double latitud
    ){
    }
}