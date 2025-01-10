package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.answer.Answer;
import com.matezalantoth.codeconverse.model.notification.Notification;
import com.matezalantoth.codeconverse.model.notification.NotificationEvent;
import com.matezalantoth.codeconverse.model.question.Question;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.repository.AnswerRepository;
import com.matezalantoth.codeconverse.repository.NotificationRepository;
import com.matezalantoth.codeconverse.repository.QuestionRepository;
import com.matezalantoth.codeconverse.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final AnswerRepository answerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final QuestionRepository questionRepository;

    public NotificationService(UserRepository userRepository, NotificationRepository notificationRepository, AnswerRepository answerRepository, QuestionRepository questionRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.answerRepository = answerRepository;
        this.questionRepository = questionRepository;
        this.eventPublisher = eventPublisher;
    }

    public void notifyAnswerOwnerOfAccept(UUID answerId, UUID questionId) {
        var tuple = getAnswerAndQuestion(answerId, questionId);
        var title = "You got accepted!";
        var content = "Your answer to question (" + tuple._2().getTitle() + ") has been accepted!";
        createAndAddNotificationToUserAndTriggerMailgun(title, content, "/question?questionId=" + questionId + "&answerId=" + answerId, tuple._1().getPoster().getId());
    }

    public void notifyAnswerOwnerOfVote(UUID answerId, UUID questionId, VoteType vote) {
        var tuple = getAnswerAndQuestion(answerId, questionId);
        var title = "You received a vote!";
        var content = "Your answer to question (" + tuple._2().getTitle() + ") has been " + vote.toString().toLowerCase() + ".";
        createAndAddNotificationToUserAndTriggerMailgun(title, content, "/question?questionId=" + questionId + "&answerId=" + answerId, tuple._1().getPoster().getId());
    }

    public void notifyQuestionOwnerOnVote(UUID questionId, VoteType vote) {
        var question = questionRepository.getQuestionById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        var title = "You received a vote!";
        var content = "Your question (" + question.getTitle() + ") has been " + vote.toString().toLowerCase() + ".";
        createAndAddNotificationToUserAndTriggerMailgun(title, content, "/question?questionId=" + questionId, question.getPoster().getId());

    }

    public void notifyQuestionOwnerOfBountyExpiry(UUID questionId, boolean refund) {
        var question = questionRepository.getQuestionById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        var title = "Bounty expiration";
        var content = "Your question (" + question.getTitle() + ")'s bounty has expired" + (refund ? ", since it seems the bounty was ineffective, we have refunded you spendable reputation points" : "") + " .";
        createAndAddNotificationToUserAndTriggerMailgun(title, content, "/question?questionId=" + questionId, question.getPoster().getId());
    }

    public Tuple<Answer, Question> getAnswerAndQuestion(UUID answerId, UUID questionId) {
        var answer = answerRepository.getAnswerById(answerId).orElseThrow(() -> new NotFoundException("Answer of id: " + answerId));
        var question = questionRepository.getQuestionById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        return new Tuple<>(answer, question);
    }

    public void createAndAddNotificationToUserAndTriggerMailgun(String title, String content, String link, UUID userId) {
        Notification notification = new Notification();
        notification.setHasBeenRead(false);
        notification.setLink(link);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSentAt(new Date());
        var user = userRepository.getUserEntityById(userId).orElseThrow(() -> new NotFoundException("User of id: " + userId));
        notification.setUser(user);
        user.getInbox().add(notification);
        notificationRepository.save(notification);
        eventPublisher.publishEvent(new NotificationEvent(notification.getUser().getEmail(), notification.getTitle(), notification.getContent()));

    }

    public void updateRead(UUID id, boolean read) {
        var notification = notificationRepository.getNotificationById(id).orElseThrow(() -> new NotFoundException("Notification of id: " + id));
        notification.setHasBeenRead(read);
    }

    public void markAllNotificationsAsRead(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        user.getInbox().forEach(notification -> {
            notification.setHasBeenRead(true);
        });
    }
}
