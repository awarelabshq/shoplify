package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductItemDTO {
    private String name;
    private String description;
    private Float price;

    @JsonProperty("image_url")
    private String imageUrl;
    private Long id;
}