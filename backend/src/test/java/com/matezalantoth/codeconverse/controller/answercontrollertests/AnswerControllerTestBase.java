package com.matezalantoth.codeconverse.controller.answercontrollertests;

import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.question.dtos.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.vote.VoteType;
import com.matezalantoth.codeconverse.model.vote.dtos.NewVoteDTO;
import org.slf4j.event.KeyValuePair;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AnswerControllerTestBase {

    @LocalServerPort
    protected int port;

    protected final TestRestTemplate restTemplate;

    public AnswerControllerTestBase(TestRestTemplate testTemplate){
        this.restTemplate = testTemplate;
    }

    protected UUID createQuestion(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO(UUID.randomUUID().toString(),  UUID.randomUUID() + "@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var postQuestionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO((UUID.randomUUID().toString()),  UUID.randomUUID().toString(), new HashSet<>()), QuestionDTO.class);
        assert postQuestionRes.getStatusCode().is2xxSuccessful();
        return postQuestionRes.getBody().id();
    }

    protected void createAndVote(VoteType type,int changeInOriginal, int timesToVote, boolean resetJwt){
        var questionId = createQuestion();
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=" + questionId, new NewAnswerDTO("test content"), AnswerDTO.class);
        assert postAnswerRes.getStatusCode().is2xxSuccessful();
        var originalVotes = postAnswerRes.getBody().votes();
        for (int i = 1; i <= timesToVote; i++){
            var voteAnswerRes = restTemplate.patchForObject("http://localhost:" + port + "/answer/vote?answerId=" + postAnswerRes.getBody().id(), new NewVoteDTO(type), AnswerDTO.class);
            if(i % 2 == 0) {
                assert voteAnswerRes.votes() == 0;
                continue;
            }
            assert voteAnswerRes.votes() == originalVotes + changeInOriginal;
        }
    }

    protected void setJwt(String jwt){
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "Bearer " + jwt);
                    return execution.execute(request, body);
                }));
    }
}
