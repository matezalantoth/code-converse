package com.matezalantoth.codeconverse.model.user;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.question.Question;
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

@Entity
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private String email;

    @Setter
    @Getter
    private String password;

    @Getter
    @Setter
    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions;

    @Setter
    @Getter
    private Date createdAt;


    @Getter
    @Setter
    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers;

    @ElementCollection
    @Setter
    @Getter
    private Set<Role> roles;


    @Setter
    @Getter
    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE)
    private Set<Vote> votes;

    @Setter
    @Getter
    @OneToMany(mappedBy = "voter", cascade = CascadeType.REMOVE)
    private Set<QuestionVote> questionVotes;

    public UserDTO dto(){
        return new UserDTO(id, username, questions.stream().map(Question::dto).collect(Collectors.toSet()), answers.stream().map(Answer::dto).collect(Collectors.toSet()));
    }
}
