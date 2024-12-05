package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.tag.dtos.AutocompleteResult;
import com.matezalantoth.codeconverse.model.tag.dtos.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagWithoutQuestionDTO;
import com.matezalantoth.codeconverse.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagDTO> createTag(@RequestBody NewTagDTO newTagDTO){
       return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(newTagDTO));
    }

    @GetMapping
    public ResponseEntity<TagWithoutQuestionDTO> getTag(@RequestParam UUID id){
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<List<AutocompleteResult>> getMatchingTags(@RequestParam String substring){
        return ResponseEntity.ok(tagService.getMatchingTags(substring.toLowerCase()));
    }
}
