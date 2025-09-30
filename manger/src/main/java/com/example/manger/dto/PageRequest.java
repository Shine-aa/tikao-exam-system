package com.example.manger.dto;

import lombok.Data;

/**
 * 分页请求DTO
 */
@Data
public class PageRequest {
    private Integer page = 1;        // 页码，从1开始
    private Integer size = 10;       // 每页大小
    private String sortBy;           // 排序字段
    private String sortOrder = "asc"; // 排序方向：asc/desc
}
