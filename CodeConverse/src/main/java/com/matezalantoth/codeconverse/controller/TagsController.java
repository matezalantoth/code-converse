package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.tag.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.TagDTO;
import com.matezalantoth.codeconverse.model.tag.TagWithoutQuestionDTO;
import com.matezalantoth.codeconverse.service.TagClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tag")
public class TagsController {

    private final TagClient tagClient;

    public TagsController(TagClient tagClient) {
        this.tagClient = tagClient;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagDTO> createTag(@RequestBody NewTagDTO newTagDTO){
       return ResponseEntity.status(HttpStatus.CREATED).body(tagClient.createTag(newTagDTO));
    }

    @GetMapping
    public ResponseEntity<TagWithoutQuestionDTO> getTag(@RequestParam UUID id){
        return ResponseEntity.ok(tagClient.getTag(id));
    }
}
