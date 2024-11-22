package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ListCartResponseDTO {
    @JsonProperty("checkout_items")
    private List<DetailedCartCheckoutItemDTO> checkoutItems;
    @JsonProperty("sum_cost")
    private Float sumCost;
}