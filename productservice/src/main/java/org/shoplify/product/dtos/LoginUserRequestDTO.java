package org.shoplify.product.dtos;

import lombok.Data;

@Data
public class LoginUserRequestDTO {
    private String email;
    private String password;
}