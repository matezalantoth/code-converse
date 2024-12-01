package com.matezalantoth.codeconverse.model.user;

import com.matezalantoth.codeconverse.model.post.QuestionDTO;

import java.util.Set;
import java.util.UUID;

public record UserDTO(UUID id, String username, Set<QuestionDTO> questions) {
}
