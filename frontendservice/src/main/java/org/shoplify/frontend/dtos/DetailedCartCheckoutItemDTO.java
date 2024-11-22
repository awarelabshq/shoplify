package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetailedCartCheckoutItemDTO {
    private ProductItemDTO item;
    @JsonProperty("total_cost")
    private Float totalCost;
}