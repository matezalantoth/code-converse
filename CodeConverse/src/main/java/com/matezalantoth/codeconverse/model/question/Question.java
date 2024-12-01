package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
public class Question{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "question_id")
    @Getter
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
    @OneToMany
    @Getter
    @Setter
    private Set<Answer> answers;

    public QuestionDTO dto(){
        return new QuestionDTO(questionId, title, content, poster.getUsername(), postedAt);
    }
}
