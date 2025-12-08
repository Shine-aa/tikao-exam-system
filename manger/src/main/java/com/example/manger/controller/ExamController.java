package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.ExamRequest;
import com.example.manger.dto.ExamResponse;
import com.example.manger.dto.ExamUpdateRequest;
import com.example.manger.dto.PageResponse;
import com.example.manger.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 考试控制器
 */
@RestController
@RequestMapping("/api/exams")
@CrossOrigin
@Tag(name = "考试管理", description = "考试创建、编辑、删除、开始、结束等管理接口")
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    /**
     * Author：李正阳，李子政
     * 创建考试
     */
    @Operation(summary = "创建考试", description = "老师或管理员创建新的考试")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "考试创建成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> createExam(
            @Parameter(description = "考试创建请求", required = true) 
            @Valid @RequestBody ExamRequest request) {
        try {
            examService.createExam(request);
            return ApiResponse.success("考试创建成功");
        } catch (Exception e) {
            return ApiResponse.error("考试创建失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 分页查询考试列表
     */
    @Operation(summary = "分页查询考试列表", description = "获取考试列表，支持分页和关键词搜索")
    @GetMapping("/page")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ExamResponse>> getExamsWithPagination(
            @Parameter(description = "页码，从1开始", example = "1") 
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") 
            @RequestParam(required = false) String keyword,
            @Parameter(description = "考试状态")
            @RequestParam(required = false) String status,
            HttpServletRequest httpRequest) {
        try {
            PageResponse<ExamResponse> exams = examService.getExamsWithPagination(page, size, keyword,status, httpRequest);
            return ApiResponse.success("获取考试列表成功", exams);
        } catch (Exception e) {
            return ApiResponse.error("获取考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 根据ID获取考试详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取考试详情", description = "根据考试ID获取考试详细信息")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<ExamResponse> getExamById(@PathVariable Long id) {
        try {
            ExamResponse exam = examService.getExamById(id);
            return ApiResponse.success("获取考试详情成功", exam);
        } catch (Exception e) {
            return ApiResponse.error("获取考试详情失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 更新考试
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新考试", description = "更新指定考试的信息")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<ExamResponse> updateExam(@PathVariable Long id, @Valid @RequestBody ExamUpdateRequest request) {
        try {
            ExamResponse exam = examService.updateExam(id, request);
            return ApiResponse.success("考试更新成功", exam);
        } catch (Exception e) {
            return ApiResponse.error("考试更新失败: " + e.getMessage());
        }
    }

    /**
     * Author：李子政
     * 修改 答完是否可查看答案字段
     */
    @PutMapping("/{id}/allow-review")
    public ApiResponse<String> updateAnswerViewable(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            examService.updateAnswerViewable(id, (Boolean) request.get("allowReview"));
            return ApiResponse.success("更新成功", null);
        } catch (Exception e) {
            return ApiResponse.error("更新失败: " + e.getMessage());
        }
    }


    /**
     * Author：李正阳
     * 删除考试
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除考试", description = "删除指定ID的考试")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> deleteExam(@PathVariable Long id) {
        try {
            examService.deleteExam(id);
            return ApiResponse.success("考试删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error("考试删除失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 开始考试
     */
    @PostMapping("/{id}/start")
    @Operation(summary = "开始考试", description = "将考试状态设置为进行中")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> startExam(@PathVariable Long id) {
        try {
            examService.startExam(id);
            return ApiResponse.success("考试已开始", null);
        } catch (Exception e) {
            return ApiResponse.error("开始考试失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 结束考试
     */
    @PostMapping("/{id}/end")
    @Operation(summary = "结束考试", description = "将考试状态设置为已结束")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> endExam(@PathVariable Long id) {
        try {
            examService.endExam(id);
            return ApiResponse.success("考试已结束", null);
        } catch (Exception e) {
            return ApiResponse.error("结束考试失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳
     * 获取考试学生列表
     */
    @GetMapping("/{id}/students")
    @Operation(summary = "获取考试学生列表", description = "获取指定考试的所有学生列表")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getExamStudents(@PathVariable Long id) {
        try {
            List<Map<String, Object>> students = examService.getExamStudents(id);
            return ApiResponse.success("获取学生列表成功", students);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李子政
     * 获取判卷考试列表
     */
    @Operation(summary = "获取判卷考试列表", description = "获取需要判卷的考试列表")
    @Tag(name = "考试判卷")
    @GetMapping("/grading")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<Map<String, Object>>> getGradingExams(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String status) {
        try {
            PageResponse<Map<String, Object>> response = examService.getGradingExams(page, size, keyword, classId, status);
            return ApiResponse.success("获取判卷考试列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取判卷考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李子政，李正阳
     * 获取考试学生列表（用于判卷）
     */
    @GetMapping("/{id}/students/grading")
    @Operation(summary = "获取考试学生列表（判卷）", description = "获取指定考试的学生列表，用于判卷，支持分页和筛选")
    @Tag(name = "考试判卷")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<Map<String, Object>>> getExamStudentsForGrading(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String gradingStatus) {
        try {
            PageResponse<Map<String, Object>> response = examService.getExamStudentsForGrading(id, page, size, keyword, status, gradingStatus);
            return ApiResponse.success("获取学生列表成功", response);
        } catch (Exception e) {
            return ApiResponse.error("获取学生列表失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李子政，李正阳
     * 获取学生答案
     */
    @GetMapping("/{examId}/students/{studentId}/answers")
    @Operation(summary = "获取学生答案", description = "获取指定学生在指定考试中的答案，包含题目、学生答案和参考答案")
    @Tag(name = "考试判卷")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getStudentAnswers(@PathVariable Long examId, @PathVariable Long studentId, HttpServletRequest request) {
        try {
            Map<String, Object> answers = examService.getStudentAnswers(examId, studentId, request);
            return ApiResponse.success("获取学生答案成功", answers);
        } catch (Exception e) {
            return ApiResponse.error("获取学生答案失败: " + e.getMessage());
        }
    }

    /**
     * Author：李正阳，李子政
     * 获取学生获取自己答案
     */
    @GetMapping("/{examId}/students/answers")
    @Operation(summary = "获取学生答案", description = "获取指定学生在指定考试中的答案，包含题目、学生答案和参考答案")
    @Tag(name = "学生查看自己答案")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Map<String, Object>> getOwnAnswers(@PathVariable Long examId, HttpServletRequest request) {
        long studentId = BaseContext.getCurrentId();
        try {
            Map<String, Object> answers = examService.getStudentAnswers(examId, studentId, request);
            return ApiResponse.success("获取学生答案成功", answers);
        } catch (Exception e) {
            return ApiResponse.error("获取学生答案失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 保存判卷结果
     */
    @PostMapping("/grading/save")
    @Operation(summary = "保存判卷结果", description = "保存判卷结果（临时保存，不提交）")
    @Tag(name = "考试判卷")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> saveGradingResult(@RequestBody Map<String, Object> gradingData) {
        try {
            examService.saveGradingResult(gradingData);
            return ApiResponse.success("判卷结果保存成功", null);
        } catch (Exception e) {
            return ApiResponse.error("保存判卷结果失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 提交判卷结果
     */
    @PostMapping("/grading/submit")
    @Operation(summary = "提交判卷结果", description = "提交判卷结果（最终提交，会计算总分并更新学生成绩）")
    @Tag(name = "考试判卷")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<String> submitGradingResult(@RequestBody Map<String, Object> gradingData, HttpServletRequest request) {
        try {
            examService.submitGradingResult(gradingData, request);
            return ApiResponse.success("判卷结果提交成功", null);
        } catch (Exception e) {
            return ApiResponse.error("提交判卷结果失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，李子政
     * 获取学生考试结果（仅显示分数，不显示题目内容）
     */
    @Operation(summary = "获取学生考试结果", description = "学生查看考试结果，仅显示分数不显示题目内容")
    @Tag(name = "考试结果")
    @GetMapping("/{examId}/student-result")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getStudentExamResult(
            @Parameter(description = "考试ID", required = true) 
            @PathVariable Long examId, 
            HttpServletRequest request) {
        try {
            Map<String, Object> result = examService.getStudentExamResult(examId);
            return ApiResponse.success("获取考试结果成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取考试结果失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取老师端仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    @Operation(summary = "获取老师端仪表盘统计数据", description = "获取老师端仪表盘的各种统计数据（题库数量、考试数量、学生数量等）")
    @Tag(name = "仪表盘")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        try {
            Map<String, Object> stats = examService.getDashboardStats();
            return ApiResponse.success("获取仪表盘统计数据成功", stats);
        } catch (Exception e) {
            return ApiResponse.error("获取仪表盘统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取老师端最近活动
     */
    @GetMapping("/dashboard/activities")
    @Operation(summary = "获取老师端最近活动", description = "获取老师端最近的考试活动记录")
    @Tag(name = "仪表盘")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<List<Map<String, Object>>> getRecentActivities(HttpServletRequest request) {
        try {
            List<Map<String, Object>> activities = examService.getRecentActivities(request);
            return ApiResponse.success("获取最近活动成功", activities);
        } catch (Exception e) {
            return ApiResponse.error("获取最近活动失败: " + e.getMessage());
        }
    }
}
