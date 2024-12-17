package com.matezalantoth.codeconverse.model.question.dtos;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.bounty.dtos.BountyDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagWithoutQuestionDTO;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record FullQuestionDTO(UUID id, String title, String content, String posterName, Date postedAt, int votes, Set<AnswerDTO> answers, boolean hasAccepted, Set<TagWithoutQuestionDTO> tags, int posterTrueRep, int posterRep, BountyDTO bounty) {
}
