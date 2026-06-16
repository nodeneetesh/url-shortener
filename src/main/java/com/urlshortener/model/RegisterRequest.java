package com.urlshortener.model;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    private String email;

    private String password;
}