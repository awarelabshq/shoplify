package org.shoplify.product.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListCartRequestDTO {
    private List<CartItemDTO> items;
}