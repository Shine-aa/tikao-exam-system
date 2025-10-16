package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.dto.QuestionRequest;
import com.example.manger.dto.QuestionResponse;
import com.example.manger.entity.Question;
import com.example.manger.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "题库管理", description = "题目管理相关接口")
public class QuestionController {
    
    private final QuestionService questionService;
    
    @PostMapping
    @Operation(summary = "创建题目", description = "创建新的题目")
    public ApiResponse<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        QuestionResponse response = questionService.createQuestion(request, userId);
        return ApiResponse.success("题目创建成功", response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新题目", description = "更新指定题目")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        QuestionResponse response = questionService.updateQuestion(id, request, userId);
        return ApiResponse.success("题目更新成功", response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除题目", description = "删除指定题目")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        questionService.deleteQuestion(id, userId);
        return ApiResponse.success("题目删除成功", null);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情", description = "获取指定题目的详细信息")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ApiResponse.success("获取题目详情成功", response);
    }
    
    @GetMapping
    @Operation(summary = "分页查询题目", description = "分页查询题目列表")
    public ApiResponse<PageResponse<QuestionResponse>> getQuestionsWithPagination(
            @Parameter(description = "题目类型") @RequestParam(required = false) Question.QuestionType type,
            @Parameter(description = "难度等级") @RequestParam(required = false) Question.DifficultyLevel difficulty,
            @Parameter(description = "知识点ID") @RequestParam(required = false) Long knowledgePointId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        PageResponse<QuestionResponse> response = questionService.getQuestionsWithPagination(
            type, difficulty, knowledgePointId, keyword, page, size, sortBy, sortDir, userId);
        return ApiResponse.success("获取题目列表成功", response);
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除题目", description = "批量删除多个题目")
    public ApiResponse<Void> batchDeleteQuestions(
            @RequestBody List<Long> ids,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        questionService.batchDeleteQuestions(ids, userId);
        return ApiResponse.success("批量删除成功", null);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取题目统计", description = "获取题目统计信息")
    public ApiResponse<QuestionService.QuestionStatistics> getQuestionStatistics(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        QuestionService.QuestionStatistics statistics = questionService.getQuestionStatistics(userId);
        return ApiResponse.success("获取统计信息成功", statistics);
    }
    
    private Long getCurrentUserId(Authentication authentication) {
        // 从认证信息中获取用户ID
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails = 
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal();
            // 这里需要根据你的用户详情实现来获取用户ID
            // 暂时返回固定值，实际应该从UserDetails中获取
            return 1L;
        }
        return 1L; // 临时返回固定值
    }
}
