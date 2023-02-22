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

    @NotBlank(message = "First name is mandatory")
    private String firstname;
    private String lastname;
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Enter a valid email")
    private String email;
    private String password;
}
