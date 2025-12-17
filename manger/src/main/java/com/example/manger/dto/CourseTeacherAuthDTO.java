package com.example.manger.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程教师授权信息包装类")
public class CourseTeacherAuthDTO {
    @Schema(description = "已授权的教师列表")
    private List<TeacherResponse> authorizedTeachers;
    
    @Schema(description = "系统中所有教师列表")
    private List<TeacherResponse> allTeachers;
}