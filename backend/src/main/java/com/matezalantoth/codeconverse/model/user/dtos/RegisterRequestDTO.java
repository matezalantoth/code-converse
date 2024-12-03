package com.matezalantoth.codeconverse.model.user.dtos;

public record RegisterRequestDTO(String username, String email, String password) {

    public LoginRequestDTO toLoginRequest(){
        return new LoginRequestDTO(email, password);
    }
}
