package com.matezalantoth.codeconverse.model.user.dtos;

import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationValueDTO;

public record NavbarReputationDTO(String username, ReputationValueDTO reputationValueDTO) {
}
