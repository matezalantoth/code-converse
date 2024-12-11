package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.question.dtos.FullQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionWithoutTagsDTO;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Question{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Getter
    @Setter
    private Date postedAt;

    @Getter
    @Setter
    private String title;


    @Getter
    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers;


    @Getter
    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;

    @Getter
    @Setter
    @ManyToOne
    private UserEntity poster;

    public int calculateImpressions(){
        return answers.stream().mapToInt(Answer::calculateVoteValue).sum() + (answers.size() * 10);
    }

    public FullQuestionDTO fullDto(){
        return new FullQuestionDTO(id, title, content, poster.getUsername(), postedAt,answers.stream().mapToInt(a -> a.getVotes().size()).sum(), answers.stream().map(Answer::dto).collect(Collectors.toSet()), answers.stream().anyMatch(Answer::isAccepted), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()));
    }

    public QuestionDTO dto(){
        return new QuestionDTO(id, title, content, poster.getUsername(), postedAt,answers.stream().mapToInt(a -> a.getVotes().size()).sum() ,answers.size(), answers.stream().anyMatch(Answer::isAccepted), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()));
    }

    public QuestionWithoutTagsDTO dtoNoTags(){
        return new QuestionWithoutTagsDTO(id, title, content, poster.getUsername(), postedAt, answers.stream().anyMatch(Answer::isAccepted), answers.stream().map(Answer::dto).collect(Collectors.toSet()));
    }
}
