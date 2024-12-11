package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.question.*;
import com.matezalantoth.codeconverse.model.question.dtos.*;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.QuestionTagRepository;
import com.matezalantoth.codeconverse.repository.TagRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
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

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository, TagRepository tagRepository, QuestionTagRepository questionTagRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
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

    public MainPageResponseDTO getMainPageQuestions(MainPageRequestDTO req){
        var questions = questionRepository.findAll(Sort.by(Sort.Direction.DESC, "postedAt"));
        if(questions.isEmpty()) {
            return new MainPageResponseDTO(req.startIndex(), 1, 1, new HashSet<>());
        }
        if(questions.size() == 1) {
            return new MainPageResponseDTO(req.startIndex(), 1, 1, questions.stream().map(Question::dto).collect(Collectors.toSet()));
        }
        var startIndex = (req.startIndex() - 1) * 10;
        var endIndex = questions.size() < startIndex+9 ? questions.size() - 1 : startIndex+9;
        return new MainPageResponseDTO(req.startIndex(), 1, (int) (double) (questions.size() / 10), questions.subList(startIndex, endIndex).stream().map(Question::dto).collect(Collectors.toSet()));
    }
}
