package com.matezalantoth.codeconverse.model.user;

import com.matezalantoth.codeconverse.model.answer.AnswerDTO;
import com.matezalantoth.codeconverse.model.question.QuestionDTO;

import java.util.Set;
import java.util.UUID;

public record UserDTO(UUID id, String username, Set<QuestionDTO> questions, Set<AnswerDTO> answers) {
}
