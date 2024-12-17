package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.AnswerUpdatesDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.service.AnswerService;
import com.matezalantoth.codeconverse.service.QuestionService;
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

    private final AnswerService answerService;
    private final QuestionService questionService;

    public AnswerController(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AnswerDTO> createAnswer(@RequestBody NewAnswerDTO newAnswer, @RequestParam UUID questionId){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        questionService.checkAndHandleExpiredBounties();
        var res = answerService.createAnswer(newAnswer, questionId, username);
        questionService.removeBountyIfNoLongerEligible(questionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/vote")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AnswerDTO> voteOnAnswer(@RequestBody NewVoteDTO newVote, @RequestParam UUID answerId){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return ResponseEntity.ok(answerService.addVote(answerId, username, newVote));
    }

    @PatchMapping("/accept")
    @PreAuthorize("hasRole('ADMIN') or @questionService.isOwner(#questionId, authentication.principal.username)")
    public ResponseEntity<AnswerDTO> acceptAnswer(@RequestParam UUID questionId, @RequestParam UUID answerId){
        questionService.checkAndHandleExpiredBounties();
        var res = answerService.accept(answerId);
        questionService.removeBountyIfNoLongerEligible(questionId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwner(#answerId, authentication.principal.username)")
    public ResponseEntity<AnswerDTO> updateAnswer(@RequestParam UUID answerId, AnswerUpdatesDTO updates) {
       return ResponseEntity.ok(answerService.updateAnswer(answerId, updates));
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN') or @answerService.isOwner(#answerId, authentication.principal.username)")
    public ResponseEntity<Void> deleteAnswer(@RequestParam UUID answerId){
        answerService.deleteAnswer(answerId, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.noContent().build();
    }
}
