package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.question.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.QuestionDTO;
import com.matezalantoth.codeconverse.model.tag.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.TagDTO;
import com.matezalantoth.codeconverse.model.tag.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.user.RegisterRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuestionControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void createQuestion(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("test", "test@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var postQuestionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO("test question", "test content", new HashSet<>()), QuestionDTO.class);
        assert postQuestionRes.getStatusCode().is2xxSuccessful();
        assert postQuestionRes.getBody().postedAt().before(new Date());

    }

    @Test
    void createQuestionWithTag(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("createQuestionWithTag", "createQuestionWithTag@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var tagRes = restTemplate.postForEntity("http://localhost:" + port + "/tag/create", new NewTagDTO("test", "test desc"), TagDTO.class);
        assert tagRes.getStatusCode().is2xxSuccessful();
        var tag = tagRes.getBody();
        Set<TagOfQuestionDTO> tags = new HashSet<>();
        tags.add(new TagOfQuestionDTO(tag.id()));
        var questionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO("test question", "test content", tags), QuestionDTO.class);
        assert questionRes.getStatusCode().is2xxSuccessful();
        assert questionRes.getBody().postedAt().before(new Date());
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
