package com.matezalantoth.codeconverse.model.questiontag;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.tag.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "question_tags")
public class QuestionTag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Column(name = "question_tag_id")
    private UUID id;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @ManyToOne
    @Getter
    @Setter
    private Tag tag;
}
