package com.matezalantoth.codeconverse.model.user;

import com.matezalantoth.codeconverse.model.post.Question;
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
    @Column(name="user_id")
    private UUID userId;
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
    @OneToMany
    private Set<Question> questions;
    @Setter
    @Getter
    private Date createdAt;
    @ElementCollection
    @Setter
    @Getter
    private Set<Role> roles;

    public UserDTO dto(){
        return new UserDTO(userId, username, questions.stream().map(Question::dto).collect(Collectors.toSet()));
    }
}
