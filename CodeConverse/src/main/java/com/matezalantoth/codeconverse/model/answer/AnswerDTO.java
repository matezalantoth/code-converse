package com.matezalantoth.codeconverse.model.answer;

import java.util.UUID;

public record AnswerDTO(String content, String posterUsername, UUID questionId) {
}
