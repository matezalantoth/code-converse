package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.post.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.post.Question;
import com.matezalantoth.codeconverse.model.post.QuestionDTO;
import com.matezalantoth.codeconverse.service.QuestionClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionClient questionClient;

    public QuestionController(QuestionClient questionClient) {
        this.questionClient = questionClient;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody NewQuestionDTO newQuestion){
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(questionClient.createQuestion(newQuestion, username));
    }

    @GetMapping
    public ResponseEntity<QuestionDTO> getQuestionById(@RequestParam UUID id){
       return ResponseEntity.ok(questionClient.getQuestionById(id));
    }
}
