package com.github.heberleandro.trelleibackend.controller.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Integer Id;
    private String firstName;
    private String lastName;
    private String email;
}
