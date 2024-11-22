package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetShippingCostResponseDTO {
    @JsonProperty("is_supported")
    private Boolean isSupported;
    @JsonProperty("shipping_cost")
    private Float shippingCost;
}