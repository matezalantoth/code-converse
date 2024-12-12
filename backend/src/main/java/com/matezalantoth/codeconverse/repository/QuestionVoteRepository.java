package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionVoteRepository extends JpaRepository<QuestionVote, UUID> {
    void removeQuestionVoteByVoteId(UUID voteId);
}
