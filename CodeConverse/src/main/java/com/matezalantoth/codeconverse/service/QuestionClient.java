package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.question.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.question.QuestionDTO;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

@Service
public class QuestionClient {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionClient(QuestionRepository questionRepository, UserRepository userRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
    }

    public QuestionDTO getQuestionById(UUID id){
       return questionRepository.getQuestionByQuestionId(id).orElseThrow(() -> new NotFoundException("question of id: " + id)).dto();
    }

    public QuestionDTO createQuestion(NewQuestionDTO newQuestion, String posterUsername){
        Question question = new Question();
        question.setTitle(newQuestion.title());
        question.setContent(newQuestion.content());
        question.setPostedAt(new Date());
        question.setAnswers(new HashSet<>());
        var poster = userRepository.getUserEntityByUsername(posterUsername).orElseThrow(() -> new NotFoundException("user of username: " + posterUsername));
        question.setPoster(poster);
        questionRepository.save(question);
        var oldQuestions = new HashSet<>(poster.getQuestions());
        oldQuestions.add(question);
        poster.setQuestions(oldQuestions);
        userRepository.save(poster);
        return question.dto();
    }
}
