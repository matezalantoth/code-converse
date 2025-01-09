package com.matezalantoth.codeconverse.model.question.dtos;

import com.matezalantoth.codeconverse.model.bounty.dtos.BountyDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public record QuestionDTO(UUID id, String title, String content, String posterName, int posterRep, Date postedAt,
                          int votes,
                          int answerCount, boolean hasAccepted, Set<TagDTO> tags, BountyDTO bounty, int views) {
}
