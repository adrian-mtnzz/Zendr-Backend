package com.zendr.backend.internal.user.model;

import com.zendr.backend.internal.user.model.enums.FavDisciplinesCurrentLevel;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FavDisciplines {
    @NotNull(message = "La disciplina no puede ser nula")
    private String disciplineId;
    private FavDisciplinesCurrentLevel currentLevel;
}
