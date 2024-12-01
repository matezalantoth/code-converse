package com.matezalantoth.codeconverse.model.post;

import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
public class Question{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    private UUID questionId;

    @Getter
    @Setter
    @ManyToOne
    private UserEntity poster;
    @Getter
    @Setter
    private String content;
    @Getter
    @Setter
    private Date postedAt;

    @Getter
    @Setter
    private String title;

    public QuestionDTO dto(){
        return new QuestionDTO(questionId, title, content, poster.getUsername(), postedAt);
    }
}
