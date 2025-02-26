package org.shoplify.frontend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ChatRequestDTO {
    private String message;
    private List<String> previousMessages;
}