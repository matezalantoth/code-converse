package com.matezalantoth.codeconverse.model.question.dtos;

import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;

import java.util.Set;

public record NewQuestionDTO(String title, String content, Set<TagOfQuestionDTO> tags) {
}
