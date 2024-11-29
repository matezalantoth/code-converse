package com.matezalantoth.codeconverse.model.user;

public record RegisterRequestDTO(String username, String email, String password) {

    public LoginRequestDTO toLoginRequest(){
        return new LoginRequestDTO(email, password);
    }
}
