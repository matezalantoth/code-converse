package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.reputation.Reputation;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.repository.BountyRepository;
import com.matezalantoth.codeconverse.repository.ReputationRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Transactional
@Service
public class ReputationService {

    private final ReputationRepository reputationRepository;
    private final BountyRepository bountyRepository;

    public ReputationService(ReputationRepository reputationRepository, BountyRepository bountyRepository) {
        this.reputationRepository = reputationRepository;
        this.bountyRepository = bountyRepository;
    }

    public void handleAnswerAccept(UserEntity user, Question question){
        var message = "Your answer was accepted!";
        var repGained = 25;
        var bounty = question.getActiveBounty();
        if(bounty.isPresent()) {
            message = "Your answer was accepted on a bountied question!";
            repGained = bounty.get().getBountyValue();
            var managedBounty = bountyRepository.getBountyById(bounty.get().getId()).orElseThrow(() -> new NotFoundException("Bounty of id: " + bounty.get().getId()));
            managedBounty.setActive(false);
        }
        buildAndSaveNewReputation(message, repGained, false, user, question.getId());
    }

    public void handleNewAnswer(UserEntity user, Question question){
        var message = "You posted an answer!";
        var repGained = 10;
        var bounty = question.getActiveBounty();
        if(bounty.isPresent()) {
            message = "You posted an answer to a bountied question!";
            repGained +=  bounty.get().getBountyValue() / 8;
            if(question.shouldBeCharged()) {
                var managedBounty = bountyRepository.getBountyById(bounty.get().getId()).orElseThrow(() -> new NotFoundException("Bounty of id: " + bounty.get().getId()));
                managedBounty.setActive(false);
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
