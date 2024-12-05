package com.matezalantoth.codeconverse.model.answer;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.Vote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    private boolean accepted;

    @Getter
    @Setter
    private Date postedAt;

    @Getter
    @Setter
    @ManyToOne
    private UserEntity poster;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Getter
    @Setter
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes;


    public int calculateVoteValue(){
        return votes.stream().mapToInt(v -> v.getType().equals(VoteType.UPVOTE) ? 1 : -1).sum();
    }

    public AnswerDTO dto(){
        return new AnswerDTO(id, content, poster.getUsername(), question.getId(), accepted, calculateVoteValue());
    }

}
