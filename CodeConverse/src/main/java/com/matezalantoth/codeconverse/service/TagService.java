package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.tag.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.tag.TagDTO;
import com.matezalantoth.codeconverse.model.tag.TagWithoutQuestionDTO;
import com.matezalantoth.codeconverse.repository.TagRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.UUID;

@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public TagDTO createTag(NewTagDTO newTag){
        Tag tag = new Tag();
        tag.setName(newTag.name());
        tag.setDescription(newTag.description());
        tag.setQuestionTags(new HashSet<>());
        tagRepository.save(tag);
        return tag.dto();
    }

    public TagWithoutQuestionDTO getTag(UUID id){
        return tagRepository.getTagByTagId(id).orElseThrow(() -> new NotFoundException("tag of id: " + id)).dtoNoQuestions();
    }

}
