package org.shoplify.frontend.dtos;

import lombok.Data;

@Data
public class LoginUserRequestDTO {
    private String email;
    private String password;
}