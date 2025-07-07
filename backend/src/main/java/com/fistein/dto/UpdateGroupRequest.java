package com.fistein.dto;

import lombok.Data;

@Data
public class UpdateGroupRequest {
    private String name;
    private String description;
    private Boolean isActive;
}