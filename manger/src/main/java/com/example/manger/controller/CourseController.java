package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.*;
import com.example.manger.service.CourseService;
import com.example.manger.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程管理控制器
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程管理相关接口")
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "创建新的课程")
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
        //会自动将创建者添加至该课程的教师列表。
        CourseResponse response = courseService.createCourse(request);
        return ApiResponse.success("课程创建成功", response);
    }
    
    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新课程", description = "更新指定ID的课程信息")
    public ApiResponse<CourseResponse> updateCourse(
            @Parameter(description = "课程ID") @PathVariable Long id,
            @Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.updateCourse(id, request);
        return ApiResponse.success("课程更新成功", response);
    }
    
    /**
     * 删除课程
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除课程", description = "删除指定ID的课程")
    public ApiResponse<Void> deleteCourse(@Parameter(description = "课程ID") @PathVariable Long id) {
        courseService.deleteCourse(id);
        return ApiResponse.success("课程删除成功", null);
    }
    
    /**
     * 根据ID获取课程
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取课程详情", description = "根据ID获取课程详细信息")
    public ApiResponse<CourseResponse> getCourseById(@Parameter(description = "课程ID") @PathVariable Long id) {
        CourseResponse response = courseService.getCourseById(id);
        return ApiResponse.success("获取课程成功", response);
    }
    
    /**
     * 分页获取教师的课程
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取课程", description = "分页获取当前教师的课程列表")
    public ApiResponse<PageResponse<CourseResponse>> getCoursesWithPagination(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        // 获取当前用户ID
        Long teacherId = getCurrentUserId();
        
        PageResponse<CourseResponse> response = courseService.getCoursesWithPagination(teacherId, page, size, keyword);
        return ApiResponse.success("获取课程列表成功", response);
    }
    
    /**
     * 获取教师的所有课程
     */
    @GetMapping
    @Operation(summary = "获取所有课程", description = "获取当前教师的所有课程")
    public ApiResponse<List<CourseResponse>> getCoursesByTeacher() {
        Long teacherId = getCurrentUserId();
        List<CourseResponse> response = courseService.getCoursesByTeacher(teacherId);
        return ApiResponse.success("获取课程列表成功", response);
    }

    /**
     * 获取课程教师授权信息（已授权教师 + 所有教师）
     */
    @GetMapping("/{courseId}/authorized-teachers")
    @Operation(summary = "获取课程教师授权信息", description = "返回课程已授权的教师列表和系统中所有教师列表")
    public ApiResponse<CourseTeacherAuthDTO> getAuthorizedTeachers(
            @Parameter(description = "课程ID") @PathVariable Long courseId) {
        CourseTeacherAuthDTO authInfo = courseService.getCourseTeacherAuthInfo(courseId);
        return ApiResponse.success("获取教师授权信息成功", authInfo);
    }

    /**
     * 授权教师到课程
     */
    @PostMapping("/{courseId}/authorize-teachers")
    @Operation(summary = "授权教师到课程", description = "批量授权教师管理指定课程")
    public ApiResponse<Void> authorizeTeachers(
            @Parameter(description = "课程ID") @PathVariable Long courseId,
            @Parameter(description = "教师ID列表") @RequestBody AuthorizeTeacherRequest request) {
        courseService.authorizeTeachers(courseId, request.getTeacherIds());
        return ApiResponse.success("教师授权成功", null);
    }


    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return BaseContext.getCurrentId();
    }
}
