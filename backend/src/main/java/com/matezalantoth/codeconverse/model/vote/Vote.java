package com.matezalantoth.codeconverse.model.vote;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.dtos.SlimVoteDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.VoteDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID voteId;

    @Getter
    @Setter
    private VoteType type;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity voter;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;


    public VoteDTO dto(){
        return new VoteDTO(voteId, type, answer.getId(), voter.getId());
    }

    public SlimVoteDTO slimDto(){
        return new SlimVoteDTO(answer.getId(), type);
    }

}
