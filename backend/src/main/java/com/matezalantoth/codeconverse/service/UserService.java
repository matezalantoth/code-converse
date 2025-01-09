package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.exception.NotFoundException;
import com.matezalantoth.codeconverse.model.notification.Notification;
import com.matezalantoth.codeconverse.model.notification.dtos.NotificationDTO;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationDTO;
import com.matezalantoth.codeconverse.model.user.*;
import com.matezalantoth.codeconverse.model.user.dtos.LoginRequestDTO;
import com.matezalantoth.codeconverse.model.user.dtos.NavbarReputationDTO;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationValueDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import com.matezalantoth.codeconverse.model.vote.QuestionVote;
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
import java.util.Set;
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

    public void addRoleFor(UserEntity user, Role role) {
        var oldRoles = user.getRoles();
        oldRoles.add(role);
        user.setRoles(oldRoles);
        userRepository.save(user);
    }

    public boolean canUserAffordBountyByUsername(String username, int bountyValue) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        return user.calcTotalRep() >= bountyValue;
    }

    public void createUser(RegisterRequestDTO newUser) throws BadRequestException {
        if (userRepository.getUserEntityByUsernameIgnoreCaseOrEmail(newUser.username(), newUser.email()).isPresent()) {
            throw new BadRequestException("Invalid credentials");
        }
        UserEntity user = new UserEntity();
        user.setUsername(newUser.username());
        user.setEmail(newUser.email());
        user.setPassword(passwordEncoder.encode(newUser.password()));
        user.setCreatedAt(new Date());
        user.setRoles(new HashSet<>());
        user.setQuestions(new HashSet<>());
        user.setQuestionVotes(new HashSet<>());
        user.setAnswers(new HashSet<>());
        user.setReputation(new HashSet<>());
        userRepository.save(user);
        addRoleFor(user, Role.ROLE_USER);
    }

    public ReputationValueDTO getReputationValueByUsername(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        return user.repValDto();
    }

    public Set<ReputationDTO> getReputationByUsername(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username));
        return user.repDto();
    }

    public String loginUser(LoginRequestDTO userDetails) throws BadRequestException {
        var optUser = userRepository.getUserEntityByEmail(userDetails.email());
        if (optUser.isEmpty()) {
            throw new BadRequestException("Invalid credentials");
        }
        var user = optUser.get();
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), userDetails.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public void makeAdmin(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("user of username: " + username));
        addRoleFor(user, Role.ROLE_ADMIN);
    }

    public UserDTO getUserByUsername(String username) {
        var optUser = userRepository.getUserEntityWithRolesAndQuestionsByUsername(username);
        if (optUser.isEmpty()) {
            throw new NotFoundException("user");
        }
        var user = optUser.get();
        Hibernate.initialize(user.getRoles());
        return user.dto();
    }

    public Set<NotificationDTO> getInboxByUsername(String username) {
        return userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("User of username: " + username)).getInbox().stream().map(Notification::dto).collect(Collectors.toSet());
    }

    public UserVotesDTO getVotesByUsername(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("user of username: " + username));
        var answerVotes = user.getVotes().stream().map(Vote::slimDto).collect(Collectors.toSet());
        var questionVotes = user.getQuestionVotes().stream().map(QuestionVote::slimDto).collect(Collectors.toSet());
        answerVotes.addAll(questionVotes);
        return new UserVotesDTO(answerVotes);
    }

    public UserDTO getUserById(UUID id) {
        return userRepository.getUserEntityById(id).orElseThrow(() -> new NotFoundException("user of id: " + id)).dto();
    }

    public NavbarReputationDTO getUserReputationForNavbarByUsername(String username) {
        var user = userRepository.getUserEntityByUsername(username).orElseThrow(() -> new NotFoundException("user of username: " + username));
        return new NavbarReputationDTO(user.getUsername(), user.repValDto());
    }
}
