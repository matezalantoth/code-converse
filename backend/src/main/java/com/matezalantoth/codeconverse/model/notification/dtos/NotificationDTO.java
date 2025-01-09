package com.matezalantoth.codeconverse.model.notification.dtos;

import java.util.Date;
import java.util.UUID;

public record NotificationDTO(UUID id, UUID receiverId, String content, String link, boolean read, Date sentAt) {
}
