package com.example.manger.controller;

import com.example.manger.service.StudentExamPaperService;
import com.example.manger.common.ApiResponse;
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
public class StudentExamPaperController {
    
    @Autowired
    private StudentExamPaperService studentExamPaperService;
    
    /**
     * 获取学生考试试卷题目
     */
    @GetMapping("/{examId}/paper")
    public ApiResponse<Map<String, Object>> getStudentExamPaper(
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
