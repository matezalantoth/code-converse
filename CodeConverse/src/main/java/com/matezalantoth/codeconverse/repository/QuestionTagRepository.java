package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.linkEntity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, UUID> {
}
