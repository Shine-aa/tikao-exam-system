package com.example.manger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class KnowledgePointRequest {
    
    @NotBlank(message = "知识点名称不能为空")
    private String name;
    
    private String description;
    
    private Long parentId;
    
    @NotNull(message = "知识点层级不能为空")
    private Integer level = 1;
    
    private Integer sortOrder = 0;
}
