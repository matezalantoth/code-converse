package com.matezalantoth.codeconverse.repository;

import com.matezalantoth.codeconverse.model.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> getTagById(UUID id);

    Optional<Tag> getTagByName(String name);
}
