package com.matezalantoth.codeconverse.model.tag.dtos;

import com.matezalantoth.codeconverse.model.question.dtos.PaginationDTO;

import java.util.Set;

public record TagsPageDTO(PaginationDTO pagination, Set<TagStatsDTO> tags) {
}
