package com.zendr.backend.internal.user.model;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeportiveProfile {
    @Valid
    private List<FavDisciplines> favDisciplines;
    private String previousInjuries;
}
