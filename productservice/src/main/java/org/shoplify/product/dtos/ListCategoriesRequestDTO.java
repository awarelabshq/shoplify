package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListCategoriesRequestDTO {
    @JsonProperty("user_id")
    private Long userId;
}