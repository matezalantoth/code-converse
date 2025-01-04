package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.bounty.dtos.NewBountyDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.service.BountyService;
import com.matezalantoth.codeconverse.service.QuestionService;
import com.matezalantoth.codeconverse.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bounty")
@PreAuthorize("hasRole('USER')")
public class BountyController {

    private final BountyService bountyService;
    private final UserService userService;
    private final QuestionService questionService;

    public BountyController(BountyService bountyService, UserService userService, QuestionService questionService) {
        this.bountyService = bountyService;
        this.userService = userService;
        this.questionService = questionService;
    }

    @PostMapping("/create")
    @PreAuthorize("@questionService.isOwner(#questionId, authentication.principal.username)")
    public ResponseEntity<QuestionDTO> addBountyToQuestion(@RequestParam UUID questionId, @RequestBody NewBountyDTO newBountyDTO) throws BadRequestException {
        if (questionService.hasAccepted(questionId)) {
            throw new BadRequestException("You cannot create a bounty when you have already accepted an answer!");
        }
        questionService.checkAndHandleExpiredBounties();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userService.canUserAffordBountyByUsername(userDetails.getUsername(), newBountyDTO.value())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(bountyService.addBountyToQuestion(newBountyDTO, questionId));
        }
        ;
        throw new BadRequestException("You cannot afford this bounty!");
    }

}
