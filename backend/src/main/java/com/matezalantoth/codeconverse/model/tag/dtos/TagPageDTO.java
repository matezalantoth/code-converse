package com.matezalantoth.codeconverse.model.tag.dtos;

import com.matezalantoth.codeconverse.model.question.dtos.QuestionsResponseDTO;

import java.util.UUID;

public record TagPageDTO(UUID id, String name, String description, QuestionsResponseDTO data) {
}
