package org.shoplify.frontend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CategoryDTO {
    private String name;
    private String description;
    @JsonProperty("image_url")
    private String imageUrl;
}