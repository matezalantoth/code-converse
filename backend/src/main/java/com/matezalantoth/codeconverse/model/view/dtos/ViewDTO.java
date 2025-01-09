package com.matezalantoth.codeconverse.model.view.dtos;

import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;

public record ViewDTO(UserDTO user, QuestionDTO question) {
}
