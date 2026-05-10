package com.zendr.backend.internal.device.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "devices")
public class Device {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotBlank(message = "El ID de usuario no puede estar vacío")
    private String userId;
    
    @NotBlank(message = "La plataforma del dispositivo es obligatoria")
    private String platform;
    
    @NotBlank(message = "El modelo del dispositivo no es obligatorio")
    private String deviceModel;
    
    @NotBlank(message = "La dirección ip de origen es obligatoria")
    private String originIpAddres;
    
    @Builder.Default
    String fcmToken = null;
    
    @Builder.Default
    boolean pushPermisions = false;
}
