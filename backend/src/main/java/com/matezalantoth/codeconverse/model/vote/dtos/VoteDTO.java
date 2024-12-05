package com.matezalantoth.codeconverse.model.vote.dtos;

import com.matezalantoth.codeconverse.model.vote.VoteType;

import java.util.UUID;

public record VoteDTO(UUID id, VoteType type, UUID answerId, UUID userId) {
}
