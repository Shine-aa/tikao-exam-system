package com.example.manger.controller;

import com.example.manger.service.StudentExamPaperService;
import com.example.manger.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 学生考试试卷控制器
 */
@RestController
@RequestMapping("/api/student/exam")
@PreAuthorize("hasRole('USER')")
@Tag(name = "学生考试", description = "学生端考试相关接口，包括获取考试列表、开始考试、提交答案等")
public class StudentExamPaperController {
    
    @Autowired
    private StudentExamPaperService studentExamPaperService;
    
    /**
     * Author：李正阳
     * 获取学生考试试卷题目
     */
    @Operation(summary = "获取学生考试试卷", description = "获取学生的考试试卷，包含题目和选项（已乱序）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "考试不存在")
    })
    @GetMapping("/{examId}/paper")
    public ApiResponse<Map<String, Object>> getStudentExamPaper(
            @Parameter(description = "考试ID", required = true) 
            @PathVariable Long examId,
            HttpServletRequest request) {
        try {
            Map<String, Object> paperData = studentExamPaperService.getStudentExamPaper(examId, request);
            return ApiResponse.success("获取试卷成功", paperData);
        } catch (Exception e) {
            return ApiResponse.error("获取试卷失败: " + e.getMessage());
        }
    }
}
