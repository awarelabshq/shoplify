package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long quantity;
    @JsonProperty("product_id")
    private Long productId;
}