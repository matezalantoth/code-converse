package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.answer.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.NewAnswerDTO;
import com.matezalantoth.codeconverse.repository.AnswerRepository;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Service
public class AnswerClient {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public AnswerClient(QuestionRepository questionRepository, UserRepository userRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }

    public AnswerDTO createAnswer(NewAnswerDTO newAnswer, UUID questionId, String posterUsername){
        Answer answer = new Answer();
        answer.setContent(newAnswer.content());
        answer.setPostedAt(new Date());
        var question = questionRepository.getQuestionByQuestionId(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        answer.setQuestion(question);
        var poster = userRepository.getUserEntityByUsername(posterUsername).orElseThrow(() -> new NotFoundException("User of username: " + posterUsername));
        answer.setPoster(poster);
        answerRepository.save(answer);
        var qOldAnswers = new HashSet<>(question.getAnswers());
        qOldAnswers.add(answer);
        question.setAnswers(qOldAnswers);
        questionRepository.save(question);
        var uOldAnswers = new HashSet<>(poster.getAnswers());
        uOldAnswers.add(answer);
        poster.setAnswers(uOldAnswers);
        userRepository.save(poster);
        return answer.dto();
    }
}
