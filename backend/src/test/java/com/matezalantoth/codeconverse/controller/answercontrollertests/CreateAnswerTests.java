package com.matezalantoth.codeconverse.controller.answercontrollertests;


import com.matezalantoth.codeconverse.model.answer.dtos.AnswerDTO;
import com.matezalantoth.codeconverse.model.answer.dtos.NewAnswerDTO;
import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreateAnswerTests extends AnswerControllerTestBase {


    public CreateAnswerTests(@Autowired TestRestTemplate testTemplate) {
        super(testTemplate);
    }

    @Test
    void createAnswerTest(){
        var questionId = createQuestion();
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=" + questionId, new NewAnswerDTO("test content"), AnswerDTO.class);
        assert postAnswerRes.getStatusCode().is2xxSuccessful();
    }

    @Test
    void createAnswerWithoutJwtReturnsUnauthorised(){
        var questionId = createQuestion();
        setJwt(null);
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=" + questionId, new NewAnswerDTO("test content"), AnswerDTO.class);
        assert postAnswerRes.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createAnswerWithoutCorrectBodyReturnsBadRequest(){
        var questionId = createQuestion();
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=" + questionId, null, AnswerDTO.class);
        assert postAnswerRes.getStatusCode().isSameCodeAs(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createAnswerWithoutInvalidQuestionIdReturnsNotFound(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("createAnswerWithoutJwtReturnsUnauthorised", "createAnswerWithoutJwtReturnsUnauthorised@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        setJwt(jwt);
        var postAnswerRes = restTemplate.postForEntity("http://localhost:" + port + "/answer/create?questionId=0c1ad9bf-f346-49cc-8af8-5cbdec9a7603", new NewAnswerDTO("test content"), Void.class);
        assert postAnswerRes.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND);
    }

}
