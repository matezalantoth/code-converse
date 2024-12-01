package com.matezalantoth.codeconverse.controller;


import com.matezalantoth.codeconverse.model.answer.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.question.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.QuestionDTO;
import com.matezalantoth.codeconverse.model.user.RegisterRequestDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AnswerControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createAnswerTest(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("tester", "tester@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var postQuestionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO("test question", "test content", new HashSet<>()), QuestionDTO.class);
        assert postQuestionRes.getStatusCode().is2xxSuccessful();
        var questionId = postQuestionRes.getBody().id();
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=" + questionId, new NewAnswerDTO("test content"), AnswerDTO.class);
        assert postAnswerRes.getStatusCode().is2xxSuccessful();
    }

    void setJwt(String jwt){
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "Bearer " + jwt);
                    return execution.execute(request, body);
                }));
    }
}
