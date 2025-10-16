package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 专业响应DTO
 */
@Data
public class MajorResponse {
    
    private Long id;
    
    private String majorCode;
    
    private String majorName;
    
    private String description;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
