package org.shoplify.product.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetUserResponseDTO {
    @JsonProperty("user_country")
    private String userCountry;
    private String token;
    private String type;
}