package com.matezalantoth.codeconverse.model.question.dtos;

import java.util.Set;

public record MainPageResponseDTO(int currentPage, int minPage, int maxPage, Set<QuestionDTO> questions) {
}
