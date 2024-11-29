package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.user.RegisterRequestDTO;
import com.matezalantoth.codeconverse.security.jwt.JwtUtils;
import com.matezalantoth.codeconverse.service.UserClient;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAndLogin(@RequestBody RegisterRequestDTO newUser) throws BadRequestException {
        userClient.createUser(newUser);
        var jwt = userClient.loginUser(newUser.toLoginRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
    }
}
