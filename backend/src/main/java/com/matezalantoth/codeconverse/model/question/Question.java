package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.bounty.dtos.BountyDTO;
import com.matezalantoth.codeconverse.model.question.dtos.FullQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionWithoutTagsDTO;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.view.View;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public UUID id;

    @Setter
    @Column(columnDefinition = "TEXT")
    public String content;

    @Setter
    public Date postedAt;

    @Setter
    public String title;


    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Answer> answers;

    @Setter
    @OneToMany(mappedBy = "question")
    public Set<Bounty> bounties;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<QuestionTag> questionTags;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<View> views;

    @Setter
    @ManyToOne
    public UserEntity poster;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    public Set<QuestionVote> votes;

    @Setter
    @Transient
    public int resultsScore = 0;

    public int calculateVoteValue() {
        return votes.stream().mapToInt(v -> v.getType().equals(VoteType.UPVOTE) ? 1 : -1).sum();
    }

    public int calculateImpressions() {
        return answers.stream().mapToInt(Answer::calculateVoteValue).sum() + (answers.size() * 10);
    }

    public boolean hasActiveBounty() {
        return getActiveBounty().isPresent();
    }

    public Optional<Bounty> getActiveBounty() {
        return bounties.stream().filter(Bounty::isActive).findFirst();
    }

    public boolean shouldBeCharged() {
        if (!hasActiveBounty()) {
            return false;
        }
        if (hasAccepted()) {
            return true;
        }
        var bounty = getActiveBounty().get();
        return answers.stream().filter(a -> a.getPostedAt().after(bounty.getSetAt())).count() >= 8;
    }

    public boolean hasAccepted() {
        return answers.stream().anyMatch(Answer::isAccepted);
    }

    public FullQuestionDTO fullDto() {
        var optBounty = getActiveBounty();
        BountyDTO finalBounty = null;
        if (optBounty.isPresent()) {
            finalBounty = optBounty.get().dto();
        }
        return new FullQuestionDTO(id, title, content, poster.getUsername(), postedAt, calculateVoteValue(), answers.stream().map(Answer::dto).collect(Collectors.toSet()), hasAccepted(), questionTags.stream().map(t -> t.getTag().dto()).collect(Collectors.toSet()), poster.getTrueReputation(), poster.getTotalReputation(), finalBounty, resultsScore);
    }

    public QuestionDTO dto() {
        var optBounty = getActiveBounty();
        BountyDTO finalBounty = null;
        if (optBounty.isPresent()) {
            finalBounty = optBounty.get().dto();
        }
        return new QuestionDTO(id, title, content, poster.getUsername(), poster.getTrueReputation(), postedAt, calculateVoteValue(), answers.size(), hasAccepted(), questionTags.stream().map(t -> t.getTag().dto()).collect(Collectors.toSet()), finalBounty, views.size(), resultsScore);
    }

    public QuestionWithoutTagsDTO dtoNoTags() {
        return new QuestionWithoutTagsDTO(id, title, content, poster.getUsername(), postedAt, hasAccepted(), answers.stream().map(Answer::dto).collect(Collectors.toSet()), resultsScore);
    }
}
