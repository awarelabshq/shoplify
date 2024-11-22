package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetailedCartCheckoutItemDTO {
    private ProductItemDTO item;
    @JsonProperty("total_cost")
    private Float totalCost;
}