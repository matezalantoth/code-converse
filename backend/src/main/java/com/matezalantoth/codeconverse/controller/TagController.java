package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.tag.dtos.*;
import com.matezalantoth.codeconverse.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
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
    public ResponseEntity<TagDTO> createTag(@RequestBody NewTagDTO newTagDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(newTagDTO));
    }

    @GetMapping
    public ResponseEntity<TagWithoutQuestionDTO> getTag(@RequestParam UUID id) {
        return ResponseEntity.ok(tagService.getTag(id));
    }

    @GetMapping("/all")
    public ResponseEntity<TagPageDTO> getTags(@RequestParam int startIndex) {
        return ResponseEntity.ok(tagService.getTags(startIndex));
    }

    @PostMapping("/autocomplete")
    public ResponseEntity<List<AutocompleteResult>> getMatchingTags(@RequestParam String substring, @RequestBody Set<AutocompleteResult> tags) {
        return ResponseEntity.ok(tagService.getMatchingTags(substring.toLowerCase(), tags));
    }
}
