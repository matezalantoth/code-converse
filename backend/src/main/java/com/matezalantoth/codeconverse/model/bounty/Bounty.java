package com.matezalantoth.codeconverse.model.bounty;

import com.matezalantoth.codeconverse.model.bounty.dtos.BountyDTO;
import com.matezalantoth.codeconverse.model.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
public class Bounty {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @Setter
    private Question question;

    @Setter
    private int bountyValue;

    @Setter
    private Date expiresAt;

    @Setter
    private Date setAt;

    @Setter
    private boolean active;

    public BountyDTO dto(){
        return new BountyDTO(id, bountyValue, expiresAt);
    }
}
