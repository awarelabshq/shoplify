package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginUserResponseDTO {
    @JsonProperty("user_id")
    private Long userId;
    private String token;
    private LoginStatus status;

    public enum LoginStatus {
        UNKNOWN_LOGIN_STATUS,
        SUCCESS,
        DENIED_RISK_SUSPENDED
    }
}