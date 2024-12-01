package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.answer.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.NewAnswerDTO;
import com.matezalantoth.codeconverse.service.AnswerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/answer")
public class AnswerController {

    private final AnswerClient answerClient;

    public AnswerController(AnswerClient answerClient) {
        this.answerClient = answerClient;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody NewAnswerDTO newAnswer, @RequestParam UUID questionId){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(answerClient.createAnswer(newAnswer, questionId, username));
    }
}
