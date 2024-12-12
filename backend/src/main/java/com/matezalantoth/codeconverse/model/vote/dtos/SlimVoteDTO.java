package com.matezalantoth.codeconverse.model.vote.dtos;

import com.matezalantoth.codeconverse.model.vote.VoteType;

import java.util.UUID;

public record SlimVoteDTO(UUID id, VoteType voteType) {
}
