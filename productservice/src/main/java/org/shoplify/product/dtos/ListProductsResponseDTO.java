package org.shoplify.product.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListProductsResponseDTO {
    private List<ProductItemDTO> products;
}