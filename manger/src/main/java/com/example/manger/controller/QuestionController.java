package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.PageResponse;
import com.example.manger.dto.QuestionRequest;
import com.example.manger.dto.QuestionResponse;
import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionCourse;
import com.example.manger.repository.QuestionCourseRepository;
import com.example.manger.service.QuestionImportService;
import com.example.manger.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "题库管理", description = "题目管理相关接口")
public class QuestionController {
    
    private final QuestionService questionService;
    private final QuestionImportService questionImportService;
    private final QuestionCourseRepository questionCourseRepository;
    
    @PostMapping
    @Operation(summary = "创建题目", description = "创建新的题目")
    public ApiResponse<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.createQuestion(request, userId);
        return ApiResponse.success("题目创建成功", response);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "更新题目", description = "更新指定题目")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.updateQuestion(id, request, userId);
        return ApiResponse.success("题目更新成功", response);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除题目", description = "删除指定题目")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long id) {
        Long userId = getCurrentUserId();
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
            @Parameter(description = "所属课程（题库）") @RequestParam(defaultValue = "-1") long courseId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Long userId = getCurrentUserId();
        PageResponse<QuestionResponse> response = questionService.getQuestionsWithPagination(
            type, difficulty, courseId,keyword, page, size, sortBy, sortDir, userId);
        return ApiResponse.success("获取题目列表成功", response);
    }
    
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除题目", description = "批量删除多个题目")
    public ApiResponse<Void> batchDeleteQuestions(
            @RequestBody List<Long> ids) {
        Long userId = getCurrentUserId();
        questionService.batchDeleteQuestions(ids, userId);
        return ApiResponse.success("批量删除成功", null);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "获取题目统计", description = "获取题目统计信息")
    public ApiResponse<QuestionService.QuestionStatistics> getQuestionStatistics() {
        Long userId = getCurrentUserId();
        QuestionService.QuestionStatistics statistics = questionService.getQuestionStatistics(userId);
        return ApiResponse.success("获取统计信息成功", statistics);
    }
    
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程获取题目列表", description = "获取指定课程的所有题目")
    public ApiResponse<List<QuestionResponse>> getQuestionsByCourse(@PathVariable Long courseId) {
        List<QuestionResponse> questions = questionService.getQuestionsByCourseId(courseId);
        return ApiResponse.success("获取题目列表成功", questions);
    }
    
    @PostMapping("/import")
    @Operation(summary = "导入题库", description = "从Excel文件导入题目")
    public ApiResponse<Map<String, Object>> importQuestions(
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "课程id") @RequestParam(defaultValue = "1") Long courseId) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            if (!file.getOriginalFilename().endsWith(".xlsx") && !file.getOriginalFilename().endsWith(".xls")) {
                return ApiResponse.error("只支持Excel文件格式(.xlsx, .xls)");
            }
            
            Long userId = getCurrentUserId();
            Map<String, Object> result = questionImportService.importQuestions(file,courseId, userId);
            
            return ApiResponse.success("导入完成", result);
            
        } catch (Exception e) {
            return ApiResponse.error("导入失败: " + e.getMessage());
        }
    }
    
    private Long getCurrentUserId() {
        return BaseContext.getCurrentId();
    }
}
