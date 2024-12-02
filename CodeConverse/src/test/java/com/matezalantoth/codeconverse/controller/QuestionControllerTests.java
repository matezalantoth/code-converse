package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.question.dtos.NewQuestionDTO;
import com.matezalantoth.codeconverse.model.question.dtos.QuestionDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.NewTagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagDTO;
import com.matezalantoth.codeconverse.model.tag.dtos.TagOfQuestionDTO;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@ActiveProfiles("test")
@Transactional
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
        restTemplate.patchForObject("http://localhost:" + port + "/user/make-admin", null, Void.class);
        var tagRes = restTemplate.postForEntity("http://localhost:" + port + "/tag/create", new NewTagDTO("test", "test desc"), TagDTO.class);
        assert tagRes.getStatusCode().is2xxSuccessful();
        var tag = tagRes.getBody();
        Set<TagOfQuestionDTO> tags = new HashSet<>();
        tags.add(new TagOfQuestionDTO(tag.id()));
        var questionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO("test question", "test content", tags), QuestionDTO.class);
        assert questionRes.getStatusCode().is2xxSuccessful();
        assert questionRes.getBody().postedAt().before(new Date());
    }

    @Test
    void deleteQuestion(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("deleteQuestion", "deleteQuestion@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var questionRes = restTemplate.postForEntity("http://localhost:" + port + "/question/create", new NewQuestionDTO("test question", "test content", new HashSet<>()), QuestionDTO.class);
        assert questionRes.getStatusCode().is2xxSuccessful();
        var questionId = questionRes.getBody().id();
        restTemplate.delete("http://localhost:" + port + "/question/delete" + questionId);
        var checkRes = restTemplate.getForEntity("http://localhost:" + port + "/user/profile", UserDTO.class);
        assert checkRes.getStatusCode().is2xxSuccessful();
        assert Objects.requireNonNull(checkRes.getBody()).answers().isEmpty();
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
