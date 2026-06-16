package com.payment.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Must be valid email address")
    private String email;

    @NotBlank(message = "Password is necessary.")
    private String password;
}
