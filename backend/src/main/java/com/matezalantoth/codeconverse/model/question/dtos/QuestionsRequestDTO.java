package com.matezalantoth.codeconverse.model.question.dtos;

import com.matezalantoth.codeconverse.model.question.QuestionFilter;

//minus one then multiply by ten
public record QuestionsRequestDTO(int startIndex, QuestionFilter filter) {
}
