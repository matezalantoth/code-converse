package com.matezalantoth.codeconverse.controller;

import com.matezalantoth.codeconverse.model.jwt.JwtResponse;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationDTO;
import com.matezalantoth.codeconverse.model.user.dtos.LoginRequestDTO;
import com.matezalantoth.codeconverse.model.user.dtos.RegisterRequestDTO;
import com.matezalantoth.codeconverse.model.reputation.dtos.ReputationValueDTO;
import com.matezalantoth.codeconverse.model.user.dtos.UserDTO;
import com.matezalantoth.codeconverse.model.vote.dtos.UserVotesDTO;
import com.matezalantoth.codeconverse.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAndLogin(@RequestBody RegisterRequestDTO newUser) throws BadRequestException {
        userService.createUser(newUser);
        var jwt = userService.loginUser(newUser.toLoginRequest());
        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(jwt));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDTO> getProfileInfo(){
        var user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(userService.getUserByUsername(user.getUsername()));
    }

    @GetMapping("/rep-val")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReputationValueDTO> getReputationValue(){
        var user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(userService.getReputationValueByUsername(user.getUsername()));
    }

    @GetMapping("/rep")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Set<ReputationDTO>> getReputation(){
        var user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok().body(userService.getReputationByUsername(user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) throws BadRequestException {
        var jwt = userService.loginUser(req);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PatchMapping("/make-admin")
    public ResponseEntity<Void> makeAdmin(){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        userService.makeAdmin(username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/votes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserVotesDTO> getVotes(){
        var username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return ResponseEntity.ok().body(userService.getVotesByUsername(username));
    }
}
