package com.matezalantoth.codeconverse.model.view;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.view.dtos.ViewDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
public class View {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @ManyToOne
    @Nullable
    @Setter
    public UserEntity viewer;

    @ManyToOne
    @Setter
    public Question question;

    public Set<Tag> getTags() {
        return question.getQuestionTags().stream().map(QuestionTag::getTag).collect(Collectors.toSet());
    }

    public ViewDTO dto() {
        return new ViewDTO(viewer != null ? viewer.dto() : null, question.dto());
    }
}
