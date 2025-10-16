package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.CourseRequest;
import com.example.manger.dto.CourseResponse;
import com.example.manger.dto.PageResponse;
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
    private final JwtUtil jwtUtil;
    
    /**
     * 创建课程
     */
    @PostMapping
    @Operation(summary = "创建课程", description = "创建新的课程")
    public ApiResponse<CourseResponse> createCourse(@Valid @RequestBody CourseRequest request) {
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
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            HttpServletRequest request) {
        
        // 获取当前用户ID
        Long teacherId = getCurrentUserId(request);
        
        PageResponse<CourseResponse> response = courseService.getCoursesWithPagination(teacherId, page, size, keyword);
        return ApiResponse.success("获取课程列表成功", response);
    }
    
    /**
     * 获取教师的所有课程
     */
    @GetMapping
    @Operation(summary = "获取所有课程", description = "获取当前教师的所有课程")
    public ApiResponse<List<CourseResponse>> getCoursesByTeacher(HttpServletRequest request) {
        Long teacherId = getCurrentUserId(request);
        List<CourseResponse> response = courseService.getCoursesByTeacher(teacherId);
        return ApiResponse.success("获取课程列表成功", response);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("无法获取用户信息");
    }
}
