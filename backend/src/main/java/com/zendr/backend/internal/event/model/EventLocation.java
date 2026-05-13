package com.zendr.backend.internal.event.model;

import com.mongodb.client.model.geojson.GeoJsonObjectType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventLocation {
    
    @NotNull(message = "El tipo de coordenadas no puede ser nulo")
    public GeoJsonObjectType coordsType;
    
    @NotNull(message = "Las coordenadas no pueden ser nulas")
    public double[] coords;
    
    @Builder
    public EventLocation(String coordsType, double[] coords) {
        
        if (coords == null || coords.length != 3) {
            throw new IllegalArgumentException(
                    "Las coordenadas deben tener exactamente 3 elementos"
            );
        }
        
        this.coordsType = GeoJsonObjectType.valueOf(coordsType);
        this.coords = coords.clone();
    }
}
