package org.shoplify.frontend.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateUserResponseDTO {
    @JsonProperty("user_id")
    private String userId;
}