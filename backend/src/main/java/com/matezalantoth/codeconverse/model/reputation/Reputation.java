package com.matezalantoth.codeconverse.model.reputation;

import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationDTO;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
public class Reputation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @Setter
    private UserEntity user;

    @Setter
    private String message;

    @Setter
    private int reputationValue;

    @Setter
    private boolean purchase;

    @Setter
    private UUID relatedDataId;

    @Setter
    private Date at;

    public ReputationDTO dto(){
        return new ReputationDTO(message, reputationValue, at);
    }

}
