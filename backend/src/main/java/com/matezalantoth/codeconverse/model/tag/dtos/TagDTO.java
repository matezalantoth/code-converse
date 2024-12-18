package com.matezalantoth.codeconverse.model.tag.dtos;

import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;

import java.util.Set;
import java.util.UUID;

public record TagDTO(UUID id, String name, String description) {
}
