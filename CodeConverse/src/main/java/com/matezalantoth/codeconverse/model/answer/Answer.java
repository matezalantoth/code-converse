package com.matezalantoth.codeconverse.model.answer;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "answer_id")
    private UUID answerId;

    @Getter
    @Setter
    private String content;

    @Getter
    @Setter
    @ManyToOne
    private UserEntity poster;

    @Getter
    @Setter
    @OneToOne
    private Question question;

    @Getter
    @Setter
    private Date postedAt;

    public AnswerDTO dto(){
        return new AnswerDTO(content, poster.getUsername(), question.getQuestionId());
    }

}
