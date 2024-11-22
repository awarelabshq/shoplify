package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long quantity;
    @JsonProperty("product_id")
    private Long productId;
}