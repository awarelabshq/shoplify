package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetUserRequestDTO {
    @JsonProperty("user_id")
    private Long userId;
}