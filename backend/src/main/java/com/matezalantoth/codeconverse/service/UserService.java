package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.user.*;
import com.matezalantoth.codeconverse.model.user.dtos.LoginRequestDTO;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import com.matezalantoth.codeconverse.model.vote.Vote;
import com.matezalantoth.codeconverse.model.vote.dtos.UserVotesDTO;
import com.matezalantoth.codeconverse.repository.UserRepository;
import com.matezalantoth.codeconverse.security.jwt.JwtUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public void addRoleFor(UserEntity user, Role role){
        var oldRoles = user.getRoles();
        oldRoles.add(role);
        user.setRoles(oldRoles);
        userRepository.save(user);
    }

    public void createUser(RegisterRequestDTO newUser) throws BadRequestException {
        if(userRepository.getUserEntityByUsernameIgnoreCaseOrEmail(newUser.username(), newUser.email()).isPresent()) {
            throw new BadRequestException("Invalid credentials");
        }
        UserEntity user = new UserEntity();
        user.setUsername(newUser.username());
        user.setEmail(newUser.email());
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setCreatedAt(new Date());
        user.setRoles(new HashSet<>());
        user.setQuestions(new HashSet<>());
        user.setAnswers(new HashSet<>());
        userRepository.save(user);
        addRoleFor(user, Role.ROLE_USER);
    }

    public String loginUser(LoginRequestDTO userDetails) throws BadRequestException {
        var optUser = userRepository.getUserEntityByEmail(userDetails.email());
        if(optUser.isEmpty()) {
            throw new BadRequestException("Invalid credentials");
        }
        var user = optUser.get();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), userDetails.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public void makeAdmin(String username){
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("user of username: " + username));
        addRoleFor(user, Role.ROLE_ADMIN);
    }

    public UserDTO getUserByUsername(String username){
        var optUser = userRepository.getUserEntityWithRolesAndQuestionsByUsername(username);
        if(optUser.isEmpty()) {
            throw new NotFoundException("user");
        }
        var user = optUser.get();
        Hibernate.initialize(user.getRoles());
        return user.dto();
    }

    public UserVotesDTO getVotesByUsername(String username){
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("user of username: " + username));
        return new UserVotesDTO(user.getVotes().stream().map(Vote::slimDto).collect(Collectors.toSet()));
    }

    public UserDTO getUserById(UUID id){
        return userRepository.getUserEntityById(id).orElseThrow(() -> new NotFoundException("user of id: " + id)).dto();
    }
}
