package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.AnswerUpdatesDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.model.vote.Vote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.repository.AnswerRepository;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
import com.matezalantoth.codeconverse.repository.VoteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Transactional
@Service
public class AnswerService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final VoteRepository voteRepository;

    public AnswerService(QuestionRepository questionRepository, UserRepository userRepository, AnswerRepository answerRepository, VoteRepository voteRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.voteRepository = voteRepository;
    }

    public AnswerDTO createAnswer(NewAnswerDTO newAnswer, UUID questionId, String posterUsername){
        Answer answer = new Answer();
        answer.setContent(newAnswer.content());
        answer.setPostedAt(new Date());
        answer.setAccepted(false);
        answer.setVotes(new HashSet<>());

        var question = questionRepository.getQuestionsById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        answer.setQuestion(question);
        var poster = userRepository.getUserEntityByUsername(posterUsername).orElseThrow(() -> new NotFoundException("User of username: " + posterUsername));
        answer.setPoster(poster);
        answerRepository.save(answer);

        question.getAnswers().add(answer);
        poster.getAnswers().add(answer);

        return answer.dto();
    }

    public AnswerDTO updateAnswer(UUID answerId, AnswerUpdatesDTO updates){
        var answer = answerRepository.getAnswerById(answerId).orElseThrow(() -> new NotFoundException("Answer of id: " + answerId));
        answer.setContent(updates.content());
        return answer.dto();
    }

    public void deleteAnswer(UUID answerId, String username){
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        var answer = answerRepository.getAnswerById(answerId).orElseThrow(() -> new NotFoundException("Answer of id: " + answerId));
        user.getAnswers().remove(answer);
        userRepository.save(user);
        answerRepository.delete(answer);
    }

    public AnswerDTO addVote(UUID answerId, String voterUsername, NewVoteDTO newVote){

        var answer = answerRepository.getAnswerById(answerId).orElseThrow(() -> new NotFoundException("Answer of id: " + answerId));
        var voter = userRepository.getUserEntityByUsername(voterUsername).orElseThrow(() -> new NotFoundException("User of username: " + voterUsername));

        var existingSameTypeVote = voter
                .getVotes()
                .stream()
                .filter(v ->
                        v.getType().equals(newVote.type()) &&
                                v.getAnswer().getId().equals(answer.getId()))
                .findFirst();
        if(existingSameTypeVote.isPresent()) {
            return removeVote(existingSameTypeVote.get(), answerId);
        }

        var existingDiffTypeVote = voter
                .getVotes()
                .stream()
                .filter(v ->
                        v.getAnswer().getId().equals(answerId))
                .findFirst();
        if(existingDiffTypeVote.isPresent()) {
            return changeVoteType(existingDiffTypeVote.get(), answerId);
        }

        var vote = new Vote();
        vote.setType(newVote.type());
        vote.setVoter(voter);
        vote.setAnswer(answer);

        voteRepository.save(vote);
        answer.getVotes().add(vote);
        voter.getVotes().add(vote);
        return answer.dto();
    }

    private AnswerDTO removeVote(Vote existingVote, UUID relevantAnswerId){
        existingVote.getAnswer().getVotes().remove(existingVote);
        existingVote.getVoter().getVotes().remove(existingVote);
        voteRepository.removeVoteByVoteId(existingVote.getVoteId());
        return answerRepository.getAnswerById(relevantAnswerId).orElseThrow(() -> new NotFoundException("Answer of id: " + relevantAnswerId)).dto();
    }

    private AnswerDTO changeVoteType(Vote vote, UUID relevantAnswerId){
        vote.setType(vote.getType().equals(VoteType.UPVOTE) ? VoteType.DOWNVOTE : VoteType.UPVOTE);
        return answerRepository.getAnswerById(relevantAnswerId).orElseThrow(() -> new NotFoundException("Answer of id: " + relevantAnswerId)).dto();
    }

    public AnswerDTO accept(UUID answerId){
        var answer = answerRepository.getAnswerById(answerId).orElseThrow(() -> new NotFoundException("Answer of id: " + answerId));
        answer.setAccepted(true);
        return answer.dto();
    }

    public boolean isOwner(UUID id, String username){
        var answer = answerRepository.getAnswerById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        return answer.getPoster().getUsername().equalsIgnoreCase(username);
    }
}
