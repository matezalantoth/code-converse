package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.question.dtos.*;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody NewQuestionDTO newQuestion){
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(newQuestion, username));
    }

    @GetMapping
    public ResponseEntity<FullQuestionDTO> getQuestionById(@RequestParam UUID id){
       return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/isOwner")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwner(#questionId, authentication.principal.username)")
    public ResponseEntity<Boolean> isOwner(@RequestParam UUID questionId){
        return ResponseEntity.ok(true);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<QuestionDTO> updateQuestionById(@RequestParam UUID id, @RequestBody QuestionUpdatesDTO updates){
        return ResponseEntity.ok(questionService.updateQuestion(updates, id));
    }

    @PatchMapping("/add-tags")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<QuestionDTO> addTagToQuestion(@RequestParam UUID id, @RequestBody Set<TagOfQuestionDTO> tags){
        return ResponseEntity.ok(questionService.addTags(id, tags));
    }

    @PatchMapping("/viewed")
    public ResponseEntity<Void> logView(@RequestParam UUID id){
        questionService.logViewById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwner(#id, authentication.principal.username)")
    public ResponseEntity<Void> deleteQuestion(@RequestParam UUID id){
        questionService.deleteQuestion(id, ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/vote")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<QuestionDTO> voteOnAnswer(@RequestBody NewVoteDTO newVote, @RequestParam UUID questionId){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return ResponseEntity.ok(questionService.addVote(questionId, username, newVote));
    }

    @PostMapping("/main-questions")
    public ResponseEntity<MainPageResponseDTO> getMainPageQuestions(@RequestBody MainPageRequestDTO req){
       return ResponseEntity.ok(questionService.getMainPageQuestions(req));
    }
}
