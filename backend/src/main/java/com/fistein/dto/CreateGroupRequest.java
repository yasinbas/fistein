package com.fistein.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateGroupRequest {
    
    @NotBlank(message = "Grup adı boş olamaz")
    private String name;
    
    private String description;
}