package com.matezalantoth.codeconverse.model.question;

import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.question.dtos.FullQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionWithoutTagsDTO;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Entity
public class Question{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String content;

    @Setter
    private Date postedAt;

    @Setter
    private String title;


    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers;

    @Setter
    @OneToOne
    @Nullable
    private Bounty bounty;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuestionTag> questionTags;

    @Setter
    @ManyToOne
    private UserEntity poster;

    @Setter
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private Set<QuestionVote> votes;

    public int calculateVoteValue(){
        return votes.stream().mapToInt(v -> v.getType().equals(VoteType.UPVOTE) ? 1 : -1).sum();
    }

    public int calculateImpressions(){
        return answers.stream().mapToInt(Answer::calculateVoteValue).sum() + (answers.size() * 10);
    }


    private void resetBountyIfExpired(){
        if(bounty != null) {
            if(bounty.getExpiresAt().before(new Date())) {
                if(!shouldBeCharged()){
                    poster.refund(bounty.getId());
                }
                setBounty(null);
            }
        }
    }

    public boolean hasActiveBounty(){
        resetBountyIfExpired();
        return bounty != null;
    }

    public boolean shouldBeCharged(){
        return answers.stream().filter(a -> a.getPostedAt().after(bounty.getSetAt())).count() >= 8 || hasAccepted();
    }

    public boolean hasAccepted(){
        return answers.stream().anyMatch(Answer::isAccepted);
    }

    public FullQuestionDTO fullDto(){
        resetBountyIfExpired();
        return new FullQuestionDTO(id, title, content, poster.getUsername(), postedAt, calculateVoteValue(), answers.stream().map(Answer::dto).collect(Collectors.toSet()), hasAccepted(), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()));
    }

    public QuestionDTO dto(){
        resetBountyIfExpired();
        return new QuestionDTO(id, title, content, poster.getUsername(), postedAt, calculateVoteValue() ,answers.size(), hasAccepted(), questionTags.stream().map(t -> t.getTag().dtoNoQuestions()).collect(Collectors.toSet()), bounty != null ? bounty.dto() : null);
    }

    public QuestionWithoutTagsDTO dtoNoTags(){
        resetBountyIfExpired();
        return new QuestionWithoutTagsDTO(id, title, content, poster.getUsername(), postedAt, hasAccepted(), answers.stream().map(Answer::dto).collect(Collectors.toSet()));
    }
}
