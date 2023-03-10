package com.github.heberleandro.trelleibackend.controller.auth;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First Name is mandatory.")
    private String firstname;
    @NotBlank(message = "Last Name is mandatory.")
    private String lastname;
    @NotBlank(message = "Email is mandatory.")
    @Email(message = "Enter a valid email.")
    private String email;
    @NotBlank(message = "Password is mandatory.")
    private String password;
}
