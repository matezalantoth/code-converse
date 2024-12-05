package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.vote.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {
    void removeVoteByVoteId(UUID voteId);

    List<Vote> getVoteByVoteId(UUID voteId);
}
