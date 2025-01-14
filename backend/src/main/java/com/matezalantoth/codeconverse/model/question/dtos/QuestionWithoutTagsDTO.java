package com.matezalantoth.codeconverse.model.question.dtos;


import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record QuestionWithoutTagsDTO(UUID id, String title, String content, String posterName, Date postedAt,
                                     boolean hasAccepted, Set<AnswerDTO> answers, int resultsScore) {
}
