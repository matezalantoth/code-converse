package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.bounty.Bounty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BountyRepository extends JpaRepository<Bounty, UUID> {
    Optional<Bounty> getBountyById(UUID id);
}
