package com.matezalantoth.codeconverse.model.answer.dtos;

import java.util.Date;
import java.util.UUID;

public record AnswerDTO(UUID id, String content, String posterUsername, int posterReputation, UUID questionId,
                        boolean accepted, int votes, Date postedAt) {
}
