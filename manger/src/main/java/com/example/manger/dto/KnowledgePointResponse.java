package com.example.manger.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class KnowledgePointResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String parentName;
    private Integer level;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 子知识点
    private List<KnowledgePointResponse> children;
}
