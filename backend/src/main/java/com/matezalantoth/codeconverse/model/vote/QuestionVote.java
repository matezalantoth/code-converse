package com.matezalantoth.codeconverse.model.vote;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.dtos.SlimVoteDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Entity
public class QuestionVote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID voteId;

    @Setter
    private VoteType type;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity voter;

    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public SlimVoteDTO slimDto(){
        return new SlimVoteDTO(question.getId(), type);
    }

}
