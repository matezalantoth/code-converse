package com.matezalantoth.codeconverse.model.user;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.reputation.Reputation;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationDTO;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationValueDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import com.matezalantoth.codeconverse.model.vote.Vote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    private String username;

    @Setter
    private String email;

    @Setter
    private String password;

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reputation> reputation;

    @Setter
    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions;

    @Setter
    private Date createdAt;


    @Setter
    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers;

    @ElementCollection
    @Setter
    private Set<Role> roles;

    @Setter
    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE)
    private Set<Vote> votes;

    @Setter
    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE)
    private Set<QuestionVote> questionVotes;

    public int calcTrueRep(){
        return reputation.stream().filter(r -> !r.isPurchase()).mapToInt(Reputation::getReputationValue).sum();
    }

    public int calcTotalRep(){
        return reputation.stream().mapToInt(Reputation::getReputationValue).sum();
    }


    public UserDTO dto(){
        return new UserDTO(id, username, questions.stream().map(Question::dto).collect(Collectors.toSet()), answers.stream().map(Answer::dto).collect(Collectors.toSet()), calcTotalRep(), calcTrueRep());
    }

    public ReputationValueDTO repValDto(){
        return new ReputationValueDTO(calcTotalRep(), calcTrueRep());
    }

    public Set<ReputationDTO> repDto(){
        return reputation.stream().map(Reputation::dto).collect(Collectors.toSet());
    }
}
