package com.matezalantoth.codeconverse.model.tag;

import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagWithoutQuestionDTO;
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
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;

    public TagDTO dto(){
        return new TagDTO(id, name, description, questionTags.stream().map(q -> q.getQuestion().dto()).collect(Collectors.toSet()));
    }

    public TagWithoutQuestionDTO dtoNoQuestions(){
        return new TagWithoutQuestionDTO(id, name, description);
    }
}
