package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.reputation.Reputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReputationRepository extends JpaRepository<Reputation, UUID> {
}
