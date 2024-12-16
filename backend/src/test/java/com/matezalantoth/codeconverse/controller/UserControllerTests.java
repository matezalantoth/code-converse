package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void RegisterAndLoginTest(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("RegisterAndLoginTest", "RegisterAndLoginTest@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        System.out.println(jwt);
        assert jwt != null;
    }


    @Test
    void ProfileTest(){
        var res = restTemplate.postForEntity("http://localhost:" + port + "/user/register", new RegisterRequestDTO("ProfileTest", "ProfileTest@gmail.com", "admin123!!"), JwtResponse.class);
        assert res.getStatusCode().is2xxSuccessful();
        String jwt = res.getBody().jwt();
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("Authorization", "Bearer " + jwt);
                    return execution.execute(request, body);
                }));
        var res2 = restTemplate.getForEntity("http://localhost:" + port + "/user/profile", UserDTO.class);
        assert res2.getStatusCode().is2xxSuccessful();
    }
}
