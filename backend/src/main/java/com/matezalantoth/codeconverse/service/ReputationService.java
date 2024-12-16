package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.reputation.Reputation;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.repository.ReputationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Transactional
@Service
public class ReputationService {

    private final ReputationRepository reputationRepository;

    public ReputationService(ReputationRepository reputationRepository) {
        this.reputationRepository = reputationRepository;
    }

    public void handleAnswerAccept(UserEntity user, Question question){
        var message = "Your answer was accepted!";
        var repGained = 25;
        if(question.hasActiveBounty()) {
            message = "Your answer was accepted on a bountied question!";
            repGained = question.getBounty().getBountyValue();
            question.setBounty(null);
        }
        buildAndSaveNewReputation(message, repGained, false, user, question.getId());
    }

    public void handleNewAnswer(UserEntity user, Question question){
        var message = "You posted an answer!";
        var repGained = 10;
        if(question.hasActiveBounty()) {
            message = "You posted an answer to a bountied question!";
            repGained +=  question.getBounty().getBountyValue() / 8;
            if(question.shouldBeCharged()) {
                question.setBounty(null);
            }
        }
        buildAndSaveNewReputation(message, repGained, false, user, question.getId());
    }

    public void chargeUserForBounty(UserEntity user, Bounty bounty){
        buildAndSaveNewReputation("You posted a bounty!", bounty.getBountyValue() * -1, true, user, bounty.getId());
    }

    private void buildAndSaveNewReputation(String message, int value, boolean purchase, UserEntity user, UUID relatedDataId){
        var rep = new Reputation();
        rep.setMessage(message);
        rep.setReputationValue(value);
        rep.setPurchase(purchase);
        rep.setUser(user);
        rep.setAt(new Date());
        rep.setRelatedDataId(relatedDataId);
        reputationRepository.save(rep);
        user.getReputation().add(rep);
    }
}
