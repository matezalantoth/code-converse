package com.matezalantoth.codeconverse.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Getter
    private String username;
    @Getter
    private String email;
    @Getter
    private String password;
    @Getter
    private Date createdAt;
    @ElementCollection
    @Setter
    @Getter
    private Set<Role> roles;
}
