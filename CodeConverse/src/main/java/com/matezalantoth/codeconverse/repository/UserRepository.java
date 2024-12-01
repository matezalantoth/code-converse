package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.post.Question;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> getUserEntityByUsernameIgnoreCaseOrEmail(String username, String email);

    Optional<UserEntity> getUserEntityByEmail(String email);

    @Query("SELECT ue FROM UserEntity ue " +
            "LEFT JOIN FETCH ue.questions q " +
            "LEFT JOIN FETCH ue.roles ur " +
            "WHERE ue.username = :username")
    Optional<UserEntity> getUserEntityWithRolesAndQuestionsByUsername(@Param("username") String username);

    Optional<UserEntity> getUserEntityByUserId(UUID userId);

    Optional<UserEntity> getUserEntityByUsername(String username);
}
