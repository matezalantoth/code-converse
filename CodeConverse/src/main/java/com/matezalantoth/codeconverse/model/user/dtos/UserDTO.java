package com.matezalantoth.codeconverse.model.user.dtos;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;

import java.util.Set;
import java.util.UUID;

public record UserDTO(UUID id, String username, Set<QuestionDTO> questions, Set<AnswerDTO> answers) {
}
