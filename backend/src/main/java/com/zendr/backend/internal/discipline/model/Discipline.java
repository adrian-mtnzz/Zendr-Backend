package com.zendr.backend.internal.discipline.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "disciplines")
public class Discipline {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotNull(message = "El nombre no puede estar vacío")
    private String name;
}
