package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    Optional<Question> getQuestionById(UUID id);

    List<Question> getQuestionsOrderByPostedAt(Date postedAt);

    @Query("SELECT count(1) FROM Question q LEFT JOIN Answer a ON q.id = a.question.id where a.id is null")
    long findQuestionsWithNoAnswersCount();

    @Query("SELECT q FROM Question q LEFT JOIN Answer a ON q.id = a.question.id where a.id is null")
    List<Question> findQuestionsWithNoAnswers();

    @Query("SELECT count(1) FROM Question q LEFT JOIN Bounty b ON q.id = b.question.id where b.active is true")
    long findQuestionsWithBountiesCount();

    @Query("SELECT q FROM Question q LEFT JOIN Bounty b ON q.id = b.question.id where b.active is true")
    List<Question> findQuestionsWithBounties();

}
