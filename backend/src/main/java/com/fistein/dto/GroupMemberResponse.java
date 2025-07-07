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
public class GroupMemberResponse {
    private Long id;
    private UserResponse user;
    private LocalDateTime joinedAt;
    private Boolean isAdmin;
    private Boolean isActive;
}