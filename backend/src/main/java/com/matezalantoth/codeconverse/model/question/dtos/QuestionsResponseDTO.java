package com.matezalantoth.codeconverse.model.question.dtos;

import java.util.Set;

public record QuestionsResponseDTO(PaginationDTO pagination, Set<QuestionDTO> questions, long count, long bountyCount) {
}
