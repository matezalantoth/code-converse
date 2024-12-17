package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.bounty.dtos.NewBountyDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.repository.BountyRepository;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Transactional
@Service
public class BountyService {

    private final BountyRepository bountyRepository;
    private final QuestionRepository questionRepository;
    private final ReputationService reputationService;

    public BountyService(BountyRepository bountyRepository, QuestionRepository questionRepository, ReputationService reputationService) {
        this.bountyRepository = bountyRepository;
        this.questionRepository = questionRepository;
        this.reputationService = reputationService;
    }

    public QuestionDTO addBountyToQuestion(NewBountyDTO newBounty, UUID questionId) throws BadRequestException {
        var question = questionRepository.getQuestionsById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        if (question.hasActiveBounty()){
            throw new BadRequestException("This question already has an active bounty!");
        }
        var bounty = new Bounty();
        bounty.setBountyValue(newBounty.value());
        bounty.setSetAt(new Date());
        int hours = newBounty.value() / 10;
        bounty.setExpiresAt(Date.from(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusHours(hours).atZone(ZoneId.systemDefault()).toInstant()));
        bounty.setQuestion(question);
        bounty.setActive(true);
        bountyRepository.save(bounty);
        question.getBounties().add(bounty);
        reputationService.chargeUserForBounty(question.getPoster(), bounty);
        return question.dto();
    }
}
