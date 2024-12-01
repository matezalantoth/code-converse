package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.linkEntity.QuestionTag;
import com.matezalantoth.codeconverse.model.question.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.question.QuestionDTO;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.QuestionTagRepository;
import com.matezalantoth.codeconverse.repository.TagRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionClient {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;

    public QuestionClient(QuestionRepository questionRepository, UserRepository userRepository, TagRepository tagRepository, QuestionTagRepository questionTagRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
    }

    public QuestionDTO getQuestionById(UUID id){
       return questionRepository.getQuestionByQuestionId(id).orElseThrow(() -> new NotFoundException("question of id: " + id)).dto();
    }

    @Transactional
    public QuestionDTO createQuestion(NewQuestionDTO newQuestion, String posterUsername){
        Question question = new Question();
        question.setTitle(newQuestion.title());
        question.setContent(newQuestion.content());
        question.setPostedAt(new Date());
        question.setAnswers(new HashSet<>());
        question.setQuestionTags(new HashSet<>());
        var poster = userRepository.getUserEntityByUsername(posterUsername).orElseThrow(() -> new NotFoundException("user of username: " + posterUsername));
        question.setPoster(poster);
        questionRepository.save(question);
        Set<Tag> tags = newQuestion.tags().stream().map(t -> tagRepository.getTagByTagId(t.id()).orElseThrow(() -> new NotFoundException("tag of id: " + t.id()))).collect(Collectors.toSet());
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
}
