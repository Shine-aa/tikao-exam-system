package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.ExamRequest;
import com.example.manger.dto.ExamResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.service.ExamService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 考试控制器
 */
@RestController
@RequestMapping("/api/exams")
@CrossOrigin
public class ExamController {
    
    @Autowired
    private ExamService examService;
    
    /**
     * 创建考试
     */
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<ExamResponse> createExam(@Valid @RequestBody ExamRequest request, HttpServletRequest httpRequest) {
        try {
            ExamResponse exam = examService.createExam(request, httpRequest);
            return ApiResponse.success("考试创建成功", exam);
        } catch (Exception e) {
            return ApiResponse.error("考试创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 分页查询考试列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ExamResponse>> getExamsWithPagination(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            HttpServletRequest httpRequest) {
        try {
            PageResponse<ExamResponse> exams = examService.getExamsWithPagination(page, size, keyword, httpRequest);
            return ApiResponse.success("获取考试列表成功", exams);
        } catch (Exception e) {
            return ApiResponse.error("获取考试列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取考试详情
     */
    @GetMapping("/{id}")
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
     * 更新考试
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<ExamResponse> updateExam(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        try {
            ExamResponse exam = examService.updateExam(id, request);
            return ApiResponse.success("考试更新成功", exam);
        } catch (Exception e) {
            return ApiResponse.error("考试更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除考试
     */
    @DeleteMapping("/{id}")
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
     * 开始考试
     */
    @PostMapping("/{id}/start")
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
     * 结束考试
     */
    @PostMapping("/{id}/end")
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
     * 获取考试学生列表
     */
    @GetMapping("/{id}/students")
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
     * 获取判卷考试列表
     */
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
     * 获取考试学生列表（用于判卷）
     */
    @GetMapping("/{id}/students/grading")
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
     * 获取学生答案
     */
    @GetMapping("/{examId}/students/{studentId}/answers")
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
     * 保存判卷结果
     */
    @PostMapping("/grading/save")
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
     * 提交判卷结果
     */
    @PostMapping("/grading/submit")
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
     * 获取学生考试结果（仅显示分数，不显示题目内容）
     */
    @GetMapping("/{examId}/student-result")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getStudentExamResult(@PathVariable Long examId, HttpServletRequest request) {
        try {
            Map<String, Object> result = examService.getStudentExamResult(examId, request);
            return ApiResponse.success("获取考试结果成功", result);
        } catch (Exception e) {
            return ApiResponse.error("获取考试结果失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取老师端仪表盘统计数据
     */
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ApiResponse<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        try {
            Map<String, Object> stats = examService.getDashboardStats(request);
            return ApiResponse.success("获取仪表盘统计数据成功", stats);
        } catch (Exception e) {
            return ApiResponse.error("获取仪表盘统计数据失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取老师端最近活动
     */
    @GetMapping("/dashboard/activities")
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
