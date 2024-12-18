package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.question.QuestionFilter;
import com.matezalantoth.codeconverse.model.tag.dtos.*;
import com.matezalantoth.codeconverse.service.TagService;
import org.apache.coyote.BadRequestException;
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
    public ResponseEntity<TagPageDTO> getTag(@RequestParam UUID id, @RequestParam QuestionFilter filter) throws BadRequestException {
        switch (filter) {
            case NEWEST -> {
                return ResponseEntity.ok(tagService.getTagWithNewestQuestions(id));
            }
            case BOUNTIED -> {
                return ResponseEntity.ok(tagService.getTagWithBountiedQuestions(id));
            }
            case UNANSWERED -> {
                return ResponseEntity.ok(tagService.getTagWithUnansweredQuestions(id));
            }
            default -> throw new BadRequestException("Your request is misconfigured");

        }
    }

    @GetMapping("/all")
    public ResponseEntity<TagsPageDTO> getTags(@RequestParam int startIndex) {
        return ResponseEntity.ok(tagService.getTags(startIndex));
    }

    @PostMapping("/autocomplete")
    public ResponseEntity<List<AutocompleteResult>> getMatchingTags(@RequestParam String substring, @RequestBody Set<AutocompleteResult> tags) {
        return ResponseEntity.ok(tagService.getMatchingTags(substring.toLowerCase(), tags));
    }
}
