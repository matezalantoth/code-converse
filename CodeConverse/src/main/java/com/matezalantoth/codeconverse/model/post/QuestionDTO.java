package com.matezalantoth.codeconverse.model.post;

import java.util.Date;
import java.util.UUID;

public record QuestionDTO(UUID id, String title, String content, String posterName, Date postedAt) {
}
