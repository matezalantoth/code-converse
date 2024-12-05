package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.answer.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {
    Optional<Answer> getAnswerById(UUID id);
}
