package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.tag.TagWithoutQuestionDTO;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record QuestionDTO(UUID id, String title, String content, String posterName, Date postedAt, Set<TagWithoutQuestionDTO> tags) {
}
