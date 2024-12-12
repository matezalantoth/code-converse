package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.question.dtos.FullQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionWithoutTagsDTO;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
public class Question{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Setter
    private Date postedAt;

    @Setter
    private String title;


    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers;


    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;

    @Setter
    @ManyToOne
    private UserEntity poster;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private Set<QuestionVote> votes;



    public int calculateImpressions(){
        return answers.stream().mapToInt(Answer::calculateVoteValue).sum() + (answers.size() * 10);
    }

    public FullQuestionDTO fullDto(){
        return new FullQuestionDTO(id, title, content, poster.getUsername(), postedAt, votes.size(), answers.stream().map(Answer::dto).collect(Collectors.toSet()), answers.stream().anyMatch(Answer::isAccepted), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()));
    }

    public QuestionDTO dto(){
        return new QuestionDTO(id, title, content, poster.getUsername(), postedAt, votes.size() ,answers.size(), answers.stream().anyMatch(Answer::isAccepted), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()));
    }

    public QuestionWithoutTagsDTO dtoNoTags(){
        return new QuestionWithoutTagsDTO(id, title, content, poster.getUsername(), postedAt, answers.stream().anyMatch(Answer::isAccepted), answers.stream().map(Answer::dto).collect(Collectors.toSet()));
    }
}
