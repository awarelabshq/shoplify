package org.shoplify.product.dtos;

import lombok.Data;

@Data
public class CreateUserRequestDTO {
    private String email;
    private String password;
    private UserType type;

    public enum UserType {
        UNKNOWN_USER_TYPE,
        SELLER,
        NORMAL
    }
}