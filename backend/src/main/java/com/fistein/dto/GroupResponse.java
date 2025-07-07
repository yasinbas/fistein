package com.fistein.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private UserResponse createdBy;
    private LocalDateTime createdAt;
    private Integer memberCount;
    private Boolean isActive;
    private Boolean isUserAdmin;
}