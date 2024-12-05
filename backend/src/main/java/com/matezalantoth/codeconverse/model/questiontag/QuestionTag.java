package com.matezalantoth.codeconverse.model.questiontag;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.tag.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
public class QuestionTag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;
}
