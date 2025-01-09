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

@Getter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Setter
    private boolean accepted;

    @Setter
    private boolean hasBeenAcceptedBefore;

    @Setter
    private Date postedAt;

    @Setter
    @ManyToOne
    private UserEntity poster;

    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Setter
    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes;


    public int calculateVoteValue() {
        return votes.stream().mapToInt(v -> v.getType().equals(VoteType.UPVOTE) ? 1 : -1).sum();
    }

    public AnswerDTO dto() {
        return new AnswerDTO(id, content, poster.getUsername(), poster.getTotalReputation(), question.getId(), accepted, calculateVoteValue(), postedAt);
    }

}
