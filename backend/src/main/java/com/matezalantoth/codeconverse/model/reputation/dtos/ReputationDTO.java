package com.matezalantoth.codeconverse.model.reputation.dtos;

import java.util.Date;
import java.util.UUID;

public record ReputationDTO(String message, int value, Date at, UUID relatedDataId) {
}
