package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.bounty.Bounty;
import com.matezalantoth.codeconverse.model.question.*;
import com.matezalantoth.codeconverse.model.question.dtos.*;
import com.matezalantoth.codeconverse.model.questiontag.QuestionTag;
import com.matezalantoth.codeconverse.model.tag.Tag;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.view.View;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import com.matezalantoth.codeconverse.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Transactional
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;
    private final QuestionVoteRepository questionVoteRepository;
    private final BountyRepository bountyRepository;
    private final NotificationService notificationService;

    public QuestionService(QuestionRepository questionRepository, UserRepository userRepository, TagRepository tagRepository, QuestionTagRepository questionTagRepository, QuestionVoteRepository questionVoteRepository, BountyRepository bountyRepository, NotificationService notificationService) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.questionTagRepository = questionTagRepository;
        this.questionVoteRepository = questionVoteRepository;
        this.bountyRepository = bountyRepository;
        this.notificationService = notificationService;
    }

    public FullQuestionDTO getQuestionById(UUID id) {
        return questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("question of id: " + id)).fullDto();
    }

    public QuestionDTO createQuestion(NewQuestionDTO newQuestion, String posterUsername) {
        Question question = new Question();
        question.setTitle(newQuestion.title());
        question.setContent(newQuestion.content());
        question.setPostedAt(new Date());
        question.setAnswers(new HashSet<>());
        question.setQuestionTags(new HashSet<>());
        question.setVotes(new HashSet<>());
        question.setBounties(new HashSet<>());
        question.setViews(new HashSet<>());
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

    public QuestionDTO updateQuestion(QuestionUpdatesDTO updates, UUID id) {
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("question of id: " + id));
        question.setTitle(updates.title());
        question.setContent(updates.content());
        questionRepository.save(question);
        return question.dto();
    }

    public boolean isOwner(UUID id, String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + id));
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        return question.getPoster().getUsername().equalsIgnoreCase(user.getUsername());
    }

    public QuestionDTO addVote(UUID questionId, String voterUsername, NewVoteDTO newVote) {

        var question = questionRepository.getQuestionById(questionId).orElseThrow(() -> new NotFoundException("Question of id: " + questionId));
        var voter = userRepository.getUserEntityByUsername(voterUsername).orElseThrow(() -> new NotFoundException("User of username: " + voterUsername));

        var existingSameTypeVote = voter
                .getQuestionVotes()
                .stream()
                .filter(v ->
                        v.getType().equals(newVote.type()) &&
                                v.getQuestion().getId().equals(question.getId()))
                .findFirst();

        if (existingSameTypeVote.isPresent()) {
            return removeVote(existingSameTypeVote.get(), questionId);
        }

        var existingDiffTypeVote = voter
                .getQuestionVotes()
                .stream()
                .filter(v ->
                        v.getQuestion().getId().equals(questionId))
                .findFirst();

        if (existingDiffTypeVote.isPresent()) {
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

    public void getSearchResults() {

    }

    private QuestionDTO removeVote(QuestionVote existingVote, UUID relevantQuestionId) {
        existingVote.getQuestion().getVotes().remove(existingVote);
        existingVote.getVoter().getQuestionVotes().remove(existingVote);
        questionVoteRepository.removeQuestionVoteByVoteId(existingVote.getVoteId());
        return questionRepository.getQuestionById(relevantQuestionId).orElseThrow(() -> new NotFoundException("Question of id: " + relevantQuestionId)).dto();
    }

    private QuestionDTO changeVoteType(QuestionVote vote, UUID relevantQuestionId) {
        vote.setType(vote.getType().equals(VoteType.UPVOTE) ? VoteType.DOWNVOTE : VoteType.UPVOTE);
        return questionRepository.getQuestionById(relevantQuestionId).orElseThrow(() -> new NotFoundException("Question of id: " + relevantQuestionId)).dto();
    }

    public QuestionDTO addTags(UUID id, Set<TagOfQuestionDTO> tagIds) {
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("question of id: " + id));
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

    public void deleteQuestion(UUID id, String username) {
        var user = userRepository.getUserEntityWithRolesAndQuestionsByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        user.getQuestions().remove(question);
        questionRepository.delete(question);
    }

    public boolean hasAccepted(UUID id) {
        return (questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id))).hasAccepted();
    }

    public QuestionsResponseDTO getPersonalisedQuestions(QuestionsRequestDTO req, UserDetails user) {
        Map<Tag, Long> preferredTags = new HashMap<>();
        if (user != null) {
            var managedUser = userRepository.getUserEntityByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException("User of username: " + user.getUsername()));
            preferredTags = managedUser.getPreferredTags();
        }
        var questions = questionRepository.findAll(Sort.by(Sort.Direction.DESC, "postedAt"));
        return makeMainPageResponse(req, scoreBasedOnTags(preferredTags, questions), questionRepository.count());
    }

    public QuestionsResponseDTO getNewestQuestions(QuestionsRequestDTO req) {
        var questions = questionRepository.findAll(Sort.by(Sort.Direction.DESC, "postedAt"));
        return makeMainPageResponse(req, questions, questionRepository.count());
    }

    public QuestionsResponseDTO getUnansweredQuestions(QuestionsRequestDTO req) {
        var questions = questionRepository.findQuestionsWithNoAnswers();
        return makeMainPageResponse(req, questions, questionRepository.findQuestionsWithNoAnswersCount());
    }

    public QuestionsResponseDTO getBountiedQuestions(QuestionsRequestDTO req) {
        var questions = questionRepository.findQuestionsWithBounties();
        return makeMainPageResponse(req, questions, questionRepository.findQuestionsWithBountiesCount());
    }

    public QuestionsResponseDTO makeMainPageResponse(QuestionsRequestDTO req, List<Question> questions, long count) {
        if (questions.isEmpty()) {
            return new QuestionsResponseDTO(new PaginationDTO(req.startIndex(), 1, 1), new HashSet<>(), 0, questionRepository.findQuestionsWithBountiesCount());
        }
        var bountyCount = questionRepository.findQuestionsWithBountiesCount();
        var startIndex = Math.max((req.startIndex() - 1) * 10, 0);
        var totalPages = (int) Math.ceil((double) questions.size() / 10);
        var endIndex = Math.min(startIndex + 10, questions.size());

        if (startIndex >= questions.size()) {
            return new QuestionsResponseDTO(new PaginationDTO(req.startIndex(), 1, totalPages), new HashSet<>(), count, bountyCount);
        }
        var pagination = new PaginationDTO(req.startIndex(),
                1,
                totalPages);

        return new QuestionsResponseDTO(pagination,
                questions.stream().sorted(Comparator.comparingInt(Question::getResultsScore).reversed()).toList().subList(startIndex, endIndex).stream()
                        .map(Question::dto)
                        .collect(Collectors.toSet()),
                count,
                bountyCount
        );
    }

    public void logViewById(UUID id, UserDetails user) {
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        View view = new View();
        if (user == null) {
            view.setViewer(null);
        } else {
            var managedUser = userRepository.getUserEntityByUsername(user.getUsername()).orElseThrow(() -> new NotFoundException("User of username: " + user.getUsername()));
            view.setViewer(managedUser);
            managedUser.getViews().add(view);
        }
        view.setQuestion(question);
        question.getViews().add(view);
    }

    public void removeBountyIfNoLongerEligible(UUID id) {
        var question = questionRepository.getQuestionById(id).orElseThrow(() -> new NotFoundException("Question of id: " + id));
        var optBounty = question.getActiveBounty();
        if (optBounty.isPresent() && question.shouldBeCharged()) {
            var bounty = optBounty.get();
            bounty.setActive(false);
        }
    }

    public void checkAndHandleExpiredBounties() {
        var questions = questionRepository.findAll();

        for (var q : questions) {
            var optBounty = q.getActiveBounty();
            if (optBounty.isEmpty()) {
                continue;
            }
            var bounty = optBounty.get();
            if (!bounty.hasExpired()) {
                continue;
            }

            var managedUser = userRepository
                    .getUserEntityById(q.getPoster().getId()).orElseThrow(() ->
                            new NotFoundException("User of id: " + q.getPoster().getId()));
            var managedBounty = bountyRepository
                    .getBountyById(bounty.getId()).orElseThrow(() ->
                            new NotFoundException("Bounty of id: " + bounty.getId()));

            managedBounty.setActive(false);
            notificationService.notifyQuestionOwnerOfBountyExpiry(q.getId(), !q.shouldBeCharged());
            if (q.shouldBeCharged()) {
                continue;
            }

            managedUser.getReputation().removeIf(r -> r.getRelatedDataId().equals(managedBounty.getId()));
            managedUser.calcTotalRep();
            managedUser.calcTrueRep();
        }
    }

    public void assignAndRenewBounties() {
        var questions = questionRepository.findAll();
        for (var q : questions) {
            if (q.getAnswers().isEmpty() && q.getActiveBounty().isEmpty()) {
                Date lastWeek = Date.from(LocalDateTime.now().minusWeeks(1).atZone(ZoneId.systemDefault()).toInstant());
                if (q.getPostedAt().before(lastWeek)) {
                    int weekDiff = (int) ChronoUnit.WEEKS.between(q.getPostedAt().toInstant(), new Date().toInstant());
                    var bounty = new Bounty();
                    bounty.setQuestion(q);
                    bounty.setBountyValue(50 * weekDiff);
                    bounty.setSetAt(new Date());
                    bounty.setActive(true);
                    bounty.setExpiresAt(Date.from(LocalDateTime.now().plusWeeks(1).atZone(ZoneId.systemDefault()).toInstant()));
                    q.getBounties().add(bounty);
                }
            }
        }
    }

    private List<Question> scoreBasedOnTags(Map<Tag, Long> tags, List<Question> questions) {
        if (!tags.isEmpty()) {
            questions.forEach(q -> {
                var score = 10;
                var matchingTags = 0;
                var questionTags = q.getQuestionTags().stream().map(QuestionTag::getTag).collect(Collectors.toSet());
                for (var entry : tags.entrySet()) {
                    if (questionTags.contains(entry.getKey())) {
                        score += (int) (entry.getValue() * 100);

                        matchingTags++;
                    }
                }
                var total = score * matchingTags;
                q.setResultsScore(total);
            });
        }
        return questions;
    }

    public QuestionsResponseDTO fuzzySearchQuestionsByTagsAndSubstring(QuestionsRequestDTO req, Collection<Tag> tags, String substring) {
        System.out.println(req.startIndex());
        var questions = questionRepository.findAll();
        questions.forEach(q ->
        {
            var total = fuzzySearch(q, q.title, substring, tags) + fuzzySearch(q, q.content, substring, tags);
            q.setResultsScore(total);
        });
        return makeMainPageResponse(req, questions, questions.size());
    }

    private int fuzzySearch(Question question, String content, String substring, Collection<Tag> tags) {
        var substringCharArray = substring.toCharArray();
        AtomicInteger total = new AtomicInteger(0);
        var contentCharArray = content.toCharArray();
        for (int i = 0; i < substringCharArray.length; i++) {
            var startPoint = substringCharArray[i];
            var startPoints = getStartPoints(contentCharArray, startPoint);
            int finalI = i;
            total.updateAndGet(v -> v + startPoints.stream().mapToInt(p -> {
                var currentIndex = finalI;
                var score = 10;
                var chaining = 2;
                for (int j = p + 1; j < contentCharArray.length && currentIndex < substringCharArray.length; j++) {
                    if (contentCharArray[j] == substringCharArray[currentIndex]) {
                        score += chaining;
                        currentIndex++;
                        chaining++;
                        continue;
                    }
                    chaining = 1;
                }
                return score;
            }).sum());
        }
        var questionTags = question.questionTags.stream().map(QuestionTag::getTag).collect(Collectors.toSet());
        tags.forEach(t -> {
            if (questionTags.contains(t)) {
                total.updateAndGet(v -> v * 2);
            }
        });
        if (content.equalsIgnoreCase(substring)) {
            total.updateAndGet(t -> t * 10000);
        } else if (content.toLowerCase().contains(substring.toLowerCase()) || substring.toLowerCase().contains(content.toLowerCase())) {
            total.updateAndGet(t -> t * 2000);
        }
        return total.get();
    }

    private List<Integer> getStartPoints(char[] contentCharArray, char startPoint) {
        List<Integer> validStartPoints = new ArrayList<>();
        for (int i = 0; i < contentCharArray.length; i++) {
            if (contentCharArray[i] == startPoint) {
                validStartPoints.add(i);
            }
        }
        return validStartPoints;
    }


}
