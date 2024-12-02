package com.matezalantoth.codeconverse.model.answer.dtos;

import java.util.UUID;

public record AnswerDTO(UUID id, String content, String posterUsername, UUID questionId, boolean accepted, int votes) {
}
