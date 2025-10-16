package com.example.manger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 学生班级关联请求DTO
 */
@Data
public class StudentClassRequest {
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "班级ID不能为空")
    private Long classId;
}

/**
 * 批量学生班级关联请求DTO
 */
@Data
class BatchStudentClassRequest {
    
    @NotNull(message = "学生ID列表不能为空")
    private List<Long> studentIds;
    
    @NotNull(message = "班级ID不能为空")
    private Long classId;
}
