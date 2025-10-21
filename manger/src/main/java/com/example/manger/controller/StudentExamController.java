package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.ExamResponse;
import com.example.manger.service.StudentExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 学生端考试控制器
 */
@RestController
@RequestMapping("/api/student/exams")
@CrossOrigin
@Tag(name = "学生考试", description = "学生端考试相关接口，包括获取考试列表、开始考试、提交答案等")
public class StudentExamController {

    @Autowired
    private StudentExamService studentExamService;

    /**
     * 获取学生的考试列表（按状态分类）
     */
    @Operation(summary = "获取学生考试列表", description = "获取学生的考试列表，按状态分类（未开始、进行中、已结束）")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Map<String, Object>> getStudentExams(HttpServletRequest request) {
        try {
            Map<String, Object> response = studentExamService.getStudentExamsByStatus(request);
            return ApiResponse.success("获取考试列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取考试列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生考试详情
     */
    @GetMapping("/{examId}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<ExamResponse> getStudentExamDetail(@PathVariable Long examId, HttpServletRequest request) {
        try {
            ExamResponse response = studentExamService.getStudentExamDetail(examId, request);
            return ApiResponse.success("获取考试详情成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取考试详情失败: " + e.getMessage());
        }
    }

    /**
     * 开始考试
     */
    @PostMapping("/{examId}/start")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> startStudentExam(@PathVariable Long examId, HttpServletRequest request) {
        try {
            studentExamService.startStudentExam(examId, request);
            return ApiResponse.success("考试已开始", null);
        } catch (Exception e) {
            return ApiResponse.error("开始考试失败: " + e.getMessage());
        }
    }

    /**
     * 提交考试答案
     */
    @PostMapping("/{examId}/submit")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<String> submitStudentExam(@PathVariable Long examId, @RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        try {
            studentExamService.submitStudentExam(examId, requestData, request);
            return ApiResponse.success("考试提交成功", null);
        } catch (Exception e) {
            return ApiResponse.error("考试提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取学生考试统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Map<String, Object>> getStudentExamStats(HttpServletRequest request) {
        try {
            Map<String, Object> response = studentExamService.getStudentExamStats(request);
            return ApiResponse.success("获取考试统计成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取考试统计失败: " + e.getMessage());
        }
    }
}
