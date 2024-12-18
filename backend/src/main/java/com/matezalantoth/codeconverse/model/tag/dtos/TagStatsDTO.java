package com.matezalantoth.codeconverse.model.tag.dtos;

import java.util.UUID;

public record TagStatsDTO(UUID id, String name, String description, long questionsCount, long questionsCountToday,
                          long questionsCountLastSevenDays) {
}
