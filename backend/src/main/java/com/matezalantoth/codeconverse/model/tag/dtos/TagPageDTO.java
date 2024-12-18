package com.matezalantoth.codeconverse.model.tag.dtos;

import com.matezalantoth.codeconverse.model.question.dtos.PaginationDTO;

import java.util.Set;

public record TagPageDTO(PaginationDTO pagination, Set<TagStatsDTO> tags) {
}
