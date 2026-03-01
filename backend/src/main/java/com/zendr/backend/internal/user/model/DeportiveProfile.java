package com.zendr.backend.internal.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeportiveProfile {

    private Map<String, String> favDisciplines;
    private String previousInjuries;
}
