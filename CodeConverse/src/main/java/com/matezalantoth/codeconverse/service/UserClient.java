package com.matezalantoth.codeconverse.service;

import com.matezalantoth.codeconverse.model.user.Role;
import com.matezalantoth.codeconverse.model.user.UserEntity;
import com.matezalantoth.codeconverse.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserClient {

    private final UserRepository userRepository;

    public UserClient(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addRoleFor(UserEntity user, Role role){
        var oldRoles = user.getRoles();
        oldRoles.add(role);
        user.setRoles(oldRoles);
        userRepository.save(user);
    }
}
