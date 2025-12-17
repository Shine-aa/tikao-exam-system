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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "题库管理", description = "题目管理相关接口")
public class QuestionController {
    
    private final QuestionService questionService;
    private final QuestionImportService questionImportService;
    private final QuestionCourseRepository questionCourseRepository;

    /**
     * Author：李子政
     * @param request
     * @return
     */
    @PostMapping
    @Operation(summary = "创建题目", description = "创建新的题目")
    public ApiResponse<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.createQuestion(request, userId);
        return ApiResponse.success("题目创建成功", response);
    }

    /**
     * Author：李子政
     * @param id
     * @param request
     * @return
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新题目", description = "更新指定题目")
    public ApiResponse<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.updateQuestion(id, request, userId);
        return ApiResponse.success("题目更新成功", response);
    }

    /**
     * Author：李子政
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除题目", description = "删除指定题目")
    public ApiResponse<Void> deleteQuestion(
            @PathVariable Long id) {
        Long userId = getCurrentUserId();
        questionService.deleteQuestion(id, userId);
        return ApiResponse.success("题目删除成功", null);
    }

    /**
     * Author：李子政
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取题目详情", description = "获取指定题目的详细信息")
    public ApiResponse<QuestionResponse> getQuestionById(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ApiResponse.success("获取题目详情成功", response);
    }

    /**
     * Author：李子政，李正阳
     * @return
     */
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


    /**
     * 手动组卷题目筛选
     * Author：李子政
     * @return
     */
    @GetMapping("/courses/questions")
    @Operation(summary = "手动组卷题目筛选", description = "分页查询题目列表（支持单个/批量题型/难度筛选）")
    public ApiResponse<PageResponse<QuestionResponse>> manual(
            @Parameter(description = "题目类型（单个）") @RequestParam(required = false) Question.QuestionType type,
            @Parameter(description = "难度等级（单个）") @RequestParam(required = false) Question.DifficultyLevel difficulty,
            @Parameter(description = "题目类型（批量，多个用逗号分隔，如：SINGLE_CHOICE,MULTIPLE_CHOICE）") @RequestParam(required = false) String batchTypes,
            @Parameter(description = "难度等级（批量，多个用逗号分隔，如：EASY,MEDIUM）") @RequestParam(required = false) String batchDifficulties,
            @Parameter(description = "所属课程（题库）") @RequestParam(defaultValue = "-1") long courseId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序字段") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "排序方向") @RequestParam(defaultValue = "DESC") String sortDir) {

        Long userId = getCurrentUserId();

        // 解析批量题型参数（字符串转List<QuestionType>）
        List<Question.QuestionType> batchTypeList = null;
        if (batchTypes != null && !batchTypes.trim().isEmpty()) {
            batchTypeList = Arrays.stream(batchTypes.split(","))
                    .map(String::trim)
                    .map(Question.QuestionType::valueOf)
                    .collect(Collectors.toList());
        }

        // 解析批量难度参数（字符串转List<DifficultyLevel>）
        List<Question.DifficultyLevel> batchDifficultyList = null;
        if (batchDifficulties != null && !batchDifficulties.trim().isEmpty()) {
            batchDifficultyList = Arrays.stream(batchDifficulties.split(","))
                    .map(String::trim)
                    .map(Question.DifficultyLevel::valueOf)
                    .collect(Collectors.toList());
        }

        PageResponse<QuestionResponse> response = questionService.getQuestionsMannual(
                type, difficulty, courseId, keyword, page, size, sortBy, sortDir, userId,
                batchTypeList, batchDifficultyList); // 传递批量参数

        return ApiResponse.success("获取题目列表成功", response);
    }


    /**
     * Author：李子政
     * @param ids
     * @return
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除题目", description = "批量删除多个题目")
    public ApiResponse<Void> batchDeleteQuestions(
            @RequestBody List<Long> ids) {
        Long userId = getCurrentUserId();
        questionService.batchDeleteQuestions(ids, userId);
        return ApiResponse.success("批量删除成功", null);
    }

    /**
     * Author：李子政
     * @return
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取题目统计", description = "获取题目统计信息")
    public ApiResponse<QuestionService.QuestionStatistics> getQuestionStatistics() {
        Long userId = getCurrentUserId();
        QuestionService.QuestionStatistics statistics = questionService.getQuestionStatistics(userId);
        return ApiResponse.success("获取统计信息成功", statistics);
    }

    /**
     * Author：李子政，李正阳
     * @param courseId
     * @return
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "根据课程获取题目列表", description = "获取指定课程的所有题目")
    public ApiResponse<List<QuestionResponse>> getQuestionsByCourse(@PathVariable Long courseId) {
        List<QuestionResponse> questions = questionService.getQuestionsByCourseId(courseId);
        return ApiResponse.success("获取题目列表成功", questions);
    }

    /**
     * Author：李正阳
     * @param file
     * @param courseId
     * @return
     */
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
