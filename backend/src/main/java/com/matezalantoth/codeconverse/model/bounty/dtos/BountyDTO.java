package com.matezalantoth.codeconverse.model.bounty.dtos;

import java.util.Date;
import java.util.UUID;

public record BountyDTO(UUID id, int value, Date expiresAt) {
}
