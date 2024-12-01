package com.matezalantoth.codeconverse.model.tag;

import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tag_id")
    @Getter
    private UUID tagId;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @OneToMany
    @Getter
    @Setter
    private Set<QuestionTag> questionTags;

    public TagDTO dto(){
        return new TagDTO(tagId, name, description, questionTags.stream().map(q -> q.getQuestion().dto()).collect(Collectors.toSet()));
    }

    public TagWithoutQuestionDTO dtoNoQuestions(){
        return new TagWithoutQuestionDTO(tagId, name, description);
    }
}
