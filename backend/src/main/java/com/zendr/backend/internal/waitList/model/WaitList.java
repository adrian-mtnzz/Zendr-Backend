package com.zendr.backend.internal.waitList.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Data
@Getter
@Document(collection = "waitLists")
public class WaitList {
    
    @Id
    @Setter(AccessLevel.NONE)
    private String id;
    
    @Setter(AccessLevel.NONE)
    private List<String> waitingAssistants;
    
    @Setter(AccessLevel.NONE)
    private boolean isFull;
    
    @Builder
    public WaitList() {
        this.waitingAssistants= new ArrayList<>();
        this.isFull=false;
    }
}
