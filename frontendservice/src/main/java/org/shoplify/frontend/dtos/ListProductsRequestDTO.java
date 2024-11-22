package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListProductsRequestDTO {
    private String category;
    @JsonProperty("num_results")
    private Integer numResults;
    @JsonProperty("user_country")
    private String userCountry;
}