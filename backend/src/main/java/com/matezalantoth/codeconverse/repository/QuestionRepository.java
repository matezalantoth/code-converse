package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>  {
    Optional<Question> getQuestionsById(UUID id);

    List<Question> getQuestionsOrderByPostedAt(Date postedAt);
}
