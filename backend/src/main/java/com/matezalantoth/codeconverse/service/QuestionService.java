package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.question.*;
import com.matezalantoth.codeconverse.model.question.dtos.*;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import com.matezalantoth.codeconverse.model.vote.Vote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final VoteRepository voteRepository;
    private final QuestionVoteRepository questionVoteRepository;
    private final BountyService bountyService;
    private final BountyRepository bountyRepository;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository, TagRepository tagRepository, QuestionTagRepository questionTagRepository, VoteRepository voteRepository, QuestionVoteRepository questionVoteRepository, BountyService bountyService, BountyRepository bountyRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.voteRepository = voteRepository;
        this.questionVoteRepository = questionVoteRepository;
        this.bountyService = bountyService;
        this.bountyRepository = bountyRepository;
    }

    public FullQuestionDTO getQuestionById(UUID id){
       return questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("question of id: " + id)).fullDto();
    }

    public QuestionDTO createQuestion(NewQuestionDTO newQuestion, String posterUsername){
        Question question = new Question();
        question.setTitle(newQuestion.title());
        question.setContent(newQuestion.content());
        question.setPostedAt(new Date());
        question.setAnswers(new HashSet<>());
        question.setQuestionTags(new HashSet<>());
        question.setVotes(new HashSet<>());
        question.setBounties(new HashSet<>());
        question.setViews(0);
        var poster = userRepository.getUserEntityByUsername(posterUsername).orElseThrow(() -> new NotFoundException("user of username: " + posterUsername));
        question.setPoster(poster);
        questionRepository.save(question);

        Set<Tag> tags = newQuestion.tags().stream().map(t -> tagRepository.getTagById(t.id()).orElseThrow(() -> new NotFoundException("tag of id: " + t.id()))).collect(Collectors.toSet());
        tags.forEach(tag -> {
            QuestionTag questionTag = new QuestionTag();
            questionTag.setTag(tag);
            questionTag.setQuestion(question);
            questionTagRepository.save(questionTag);
            question.getQuestionTags().add(questionTag);
            tag.getQuestionTags().add(questionTag);
        });
        poster.getQuestions().add(question);
        return question.dto();
    }

    public QuestionDTO updateQuestion(QuestionUpdatesDTO updates, UUID id){
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("question of id: " + id));
        question.setTitle(updates.title());
        question.setContent(updates.content());
        questionRepository.save(question);
        return question.dto();
    }

    public boolean isOwner(UUID id, String username){
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        return question.getPoster().getUsername().equalsIgnoreCase(username);
    }

    public QuestionDTO addVote(UUID questionId, String voterUsername, NewVoteDTO newVote){

        var question = questionRepository.getQuestionsById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        var voter = userRepository.getUserEntityByUsername(voterUsername).orElseThrow(() -> new NotFoundException("User of username: " + voterUsername));

        var existingSameTypeVote = voter
                .getQuestionVotes()
                .stream()
                .filter(v ->
                        v.getType().equals(newVote.type()) &&
                                v.getQuestion().getId().equals(question.getId()))
                .findFirst();

        if(existingSameTypeVote.isPresent()) {
            return removeVote(existingSameTypeVote.get(), questionId);
        }

        var existingDiffTypeVote = voter
                .getQuestionVotes()
                .stream()
                .filter(v ->
                        v.getQuestion().getId().equals(questionId))
                .findFirst();

        if(existingDiffTypeVote.isPresent()) {
            return changeVoteType(existingDiffTypeVote.get(), questionId);
        }

        var vote = new QuestionVote();
        vote.setType(newVote.type());
        vote.setVoter(voter);
        vote.setQuestion(question);

        questionVoteRepository.save(vote);
        question.getVotes().add(vote);
        voter.getQuestionVotes().add(vote);
        return question.dto();
    }

    private QuestionDTO removeVote(QuestionVote existingVote, UUID relevantQuestionId){
        existingVote.getQuestion().getVotes().remove(existingVote);
        existingVote.getVoter().getQuestionVotes().remove(existingVote);
        questionVoteRepository.removeQuestionVoteByVoteId(existingVote.getVoteId());
        return questionRepository.getQuestionsById(relevantQuestionId).orElseThrow(() -> new NotFoundException("Question of id: " + relevantQuestionId)).dto();
    }

    private QuestionDTO changeVoteType(QuestionVote vote, UUID relevantQuestionId){
        vote.setType(vote.getType().equals(VoteType.UPVOTE) ? VoteType.DOWNVOTE : VoteType.UPVOTE);
        return questionRepository.getQuestionsById(relevantQuestionId).orElseThrow(() -> new NotFoundException("Question of id: " + relevantQuestionId)).dto();
    }

    public QuestionDTO addTags(UUID id, Set<TagOfQuestionDTO> tagIds){
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("question of id: " + id));
        var tags = tagIds.stream().map(t -> tagRepository.getTagById(t.id()).orElseThrow(() -> new NotFoundException("tag of id: " + t.id()))).collect(Collectors.toSet());
        tags.forEach(t -> {
            QuestionTag qt = new QuestionTag();
            qt.setQuestion(question);
            qt.setTag(t);
            questionTagRepository.save(qt);
            t.getQuestionTags().add(qt);
            question.getQuestionTags().add(qt);
        }
        );
        return question.dto();
    }

    public void deleteQuestion(UUID id, String username){
        var user = userRepository.getUserEntityWithRolesAndQuestionsByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        user.getQuestions().remove(question);
        questionRepository.delete(question);
    }

    public boolean hasAccepted(UUID id){
       return (questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id))).hasAccepted();
    }

    public MainPageResponseDTO getMainPageQuestions(MainPageRequestDTO req) {
        var questions = questionRepository.findAll(Sort.by(Sort.Direction.DESC, "postedAt"));
        if (questions.isEmpty()) {
            return new MainPageResponseDTO(req.startIndex(), 1, 1, new HashSet<>());
        }
        var startIndex = Math.max((req.startIndex() - 1) * 10, 0);
        var totalPages = (int) Math.ceil((double) questions.size() / 10);
        var endIndex = Math.min(startIndex + 10, questions.size());

        if (startIndex >= questions.size()) {
            return new MainPageResponseDTO(req.startIndex(), 1, totalPages, new HashSet<>());
        }
        return new MainPageResponseDTO(
                req.startIndex(),
                1,
                totalPages,
                questions.subList(startIndex, endIndex).stream()
                        .map(Question::dto)
                        .collect(Collectors.toSet())
        );
    }

    public void logViewById(UUID id){
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        question.setViews(question.getViews() +1);
    }

    public void removeBountyIfNoLongerEligible(UUID id){
        var question = questionRepository.getQuestionsById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        var optBounty = question.getActiveBounty();
        if(optBounty.isPresent() && question.shouldBeCharged()) {
                var bounty = optBounty.get();
                bounty.setActive(false);
        }
    }

    public void checkAndHandleExpiredBounties(){
        var questions = questionRepository.findAll();

        for(var q: questions) {
            var optBounty = q.getActiveBounty();
            if(optBounty.isEmpty()){
                continue;
            }
            var bounty = optBounty.get();
            if(!bounty.hasExpired()){
                continue;
            }

            var managedUser = userRepository
                    .getUserEntityById(q.getPoster().getId()).orElseThrow(() ->
                            new NotFoundException("User of id: " + q.getPoster().getId()));
            var managedBounty = bountyRepository
                    .getBountyById(bounty.getId()).orElseThrow(() ->
                            new NotFoundException("Bounty of id: " + bounty.getId()));

            managedBounty.setActive(false);
            if(q.shouldBeCharged()){
                continue;
            }

            managedUser.getReputation().removeIf(r -> r.getRelatedDataId().equals(managedBounty.getId()));
        }
    }

}
