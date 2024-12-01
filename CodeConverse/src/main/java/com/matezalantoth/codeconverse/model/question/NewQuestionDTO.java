package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.tag.TagOfQuestionDTO;

import java.util.Set;

public record NewQuestionDTO(String title, String content, Set<TagOfQuestionDTO> tags) {
}
