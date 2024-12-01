package com.matezalantoth.codeconverse.model.tag;

import com.matezalantoth.codeconverse.model.question.QuestionDTO;

import java.util.Set;
import java.util.UUID;

public record TagDTO(UUID id, String name, String description, Set<QuestionDTO> questions) {
}
