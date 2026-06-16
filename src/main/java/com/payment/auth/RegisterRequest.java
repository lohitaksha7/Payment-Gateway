package com.payment.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Must be valid email address.")
    private String email;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, message = "password must be 8 letters.")
    private String password;
}
