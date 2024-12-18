package com.matezalantoth.codeconverse.model.tag;

import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.question.dtos.PaginationDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionsResponseDTO;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagPageDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagStatsDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private UUID id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;

    public TagDTO dto() {
        return new TagDTO(id, name, description);
    }

    public TagPageDTO pageDto(Set<Question> questions, long count, long bountyCount) {
        return new TagPageDTO(id, name, description, new QuestionsResponseDTO(
                new PaginationDTO(1, 1, questions.size() / 10),
                questions.stream().map(Question::dto).collect(Collectors.toSet()),
                count, bountyCount)
        );
    }

    public TagStatsDTO statsDto() {
        var questions = questionTags.stream().map(QuestionTag::getQuestion).collect(Collectors.toSet());
        Date today = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date lastWeek = Date.from(LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
        return new TagStatsDTO(id, name, description, questions.size(), questions.stream().filter(q -> q.getPostedAt().after(today)).count(), questions.stream().filter(q -> q.getPostedAt().after(lastWeek)).count());
    }
}
