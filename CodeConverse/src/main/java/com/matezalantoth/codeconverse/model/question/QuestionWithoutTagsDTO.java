package com.matezalantoth.codeconverse.model.question;


import java.util.Date;
import java.util.UUID;

public record QuestionWithoutTagsDTO(UUID id, String title, String content, String posterName, Date postedA)  {
}
