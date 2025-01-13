package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.question.QuestionFilter;
import com.matezalantoth.codeconverse.model.question.dtos.*;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.service.NotificationService;
import com.matezalantoth.codeconverse.service.QuestionService;
import jakarta.annotation.Nullable;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final NotificationService notificationService;

    public QuestionController(QuestionService questionService, NotificationService notificationService) {
        this.questionService = questionService;
        this.notificationService = notificationService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody NewQuestionDTO newQuestion) {
        var userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var username = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.createQuestion(newQuestion, username));
    }

    @GetMapping
    public ResponseEntity<FullQuestionDTO> getQuestionById(@RequestParam UUID id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/isOwner")
    @PreAuthorize("hasRole('ADMIN') or (authentication.principal instanceof T(org.springframework.security.core.userdetails.UserDetails) and @questionService.isOwner(#questionId, authentication.principal.username))")
    public ResponseEntity<Boolean> isOwner(@RequestParam UUID questionId) {
        return ResponseEntity.ok(true);
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN') or (authentication.principal instanceof T(org.springframework.security.core.userdetails.UserDetails) and @questionService.isOwner(#id, authentication.principal.username))")
    public ResponseEntity<QuestionDTO> updateQuestionById(@RequestParam UUID id, @RequestBody QuestionUpdatesDTO updates) {
        return ResponseEntity.ok(questionService.updateQuestion(updates, id));
    }

    @PatchMapping("/add-tags")
    @PreAuthorize("hasRole('ADMIN') or (authentication.principal instanceof T(org.springframework.security.core.userdetails.UserDetails) and @questionService.isOwner(#id, authentication.principal.username))")
    public ResponseEntity<QuestionDTO> addTagToQuestion(@RequestParam UUID id, @RequestBody Set<TagOfQuestionDTO> tags) {
        return ResponseEntity.ok(questionService.addTags(id, tags));
    }

    @PatchMapping("/viewed")
    public ResponseEntity<Void> logView(@RequestParam UUID id) {
        questionService.logViewById(id, getUserDetailsIfAvailable());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN') or (authentication.principal instanceof T(org.springframework.security.core.userdetails.UserDetails) and @questionService.isOwner(#id, authentication.principal.username))")
    public ResponseEntity<Void> deleteQuestion(@RequestParam UUID id) {
        questionService.deleteQuestion(id, ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/vote")
    @PreAuthorize("hasRole('USER') and not @questionService.isOwner(#questionId, authentication.principal.username) and authentication.principal instanceof T(org.springframework.security.core.userdetails.UserDetails)")
    public ResponseEntity<QuestionDTO> voteOnAnswer(@RequestBody NewVoteDTO newVote, @RequestParam UUID questionId) {
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        notificationService.notifyQuestionOwnerOnVote(questionId, newVote.type());
        return ResponseEntity.ok(questionService.addVote(questionId, username, newVote));
    }

    @GetMapping("/questions")
    public ResponseEntity<QuestionsResponseDTO> getQuestions(@RequestParam QuestionFilter filter, @RequestParam int startIndex) throws BadRequestException {
        switch (filter) {
            case PERSONALISED -> {
                return ResponseEntity.ok(questionService.getPersonalisedQuestions(new QuestionsRequestDTO(startIndex, filter), getUserDetailsIfAvailable()));
            }
            case NEWEST -> {
                return ResponseEntity.ok(questionService.getNewestQuestions(new QuestionsRequestDTO(startIndex, filter)));
            }
            case UNANSWERED -> {
                return ResponseEntity.ok(questionService.getUnansweredQuestions(new QuestionsRequestDTO(startIndex, filter)));
            }
            case BOUNTIED -> {
                return ResponseEntity.ok(questionService.getBountiedQuestions(new QuestionsRequestDTO(startIndex, filter)));
            }
            default -> throw new BadRequestException("Your request is misconfigured");
        }
    }

    private UserDetails getUserDetailsIfAvailable() {
        UserDetails user = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        if (authentication.getPrincipal() != "anonymousUser") {
            user = (UserDetails) authentication.getPrincipal();
        }
        return user;
    }
}
