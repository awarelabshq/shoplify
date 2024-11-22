package org.shoplify.product.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ListCategoriesResponseDTO {
    @JsonProperty("featured_category")
    private CategoryDTO featuredCategory;
    private List<CategoryDTO> categories;
}