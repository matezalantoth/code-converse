package com.matezalantoth.codeconverse.model.vote.dtos;

import java.util.Set;

public record UserVotesDTO(Set<SlimVoteDTO> votes) {
}
