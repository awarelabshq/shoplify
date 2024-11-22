package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetShippingCostRequestDTO {
    private String country;
    @JsonProperty("zip_code")
    private String zipCode;
    @JsonProperty("total_cost")
    private Float totalCost;
}