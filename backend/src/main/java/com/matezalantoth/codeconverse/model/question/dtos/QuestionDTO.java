package com.matezalantoth.codeconverse.model.question.dtos;

import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.bounty.dtos.BountyDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagWithoutQuestionDTO;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record QuestionDTO(UUID id, String title, String content, String posterName, Date postedAt, int votes, int answerCount, boolean hasAccepted, Set<TagWithoutQuestionDTO> tags, BountyDTO bounty) {
}
