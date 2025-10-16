package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.ClassCourseRequest;
import com.example.manger.dto.ClassCourseResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.service.ClassCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级课程关联管理控制器
 */
@RestController
@RequestMapping("/api/class-courses")
@Tag(name = "班级课程关联", description = "班级课程关联管理相关接口")
public class ClassCourseController {
    
    @Autowired
    private ClassCourseService classCourseService;
    
    /**
     * 创建班级课程关联
     */
    @PostMapping
    @Operation(summary = "创建班级课程关联", description = "为班级添加课程")
    public ApiResponse<ClassCourseResponse> createClassCourse(@Valid @RequestBody ClassCourseRequest request) {
        ClassCourseResponse response = classCourseService.createClassCourse(request);
        return ApiResponse.success("班级课程关联创建成功", response);
    }
    
    /**
     * 更新班级课程关联
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新班级课程关联", description = "更新指定班级课程关联信息")
    public ApiResponse<ClassCourseResponse> updateClassCourse(
            @Parameter(description = "关联ID") @PathVariable Long id,
            @Valid @RequestBody ClassCourseRequest request) {
        ClassCourseResponse response = classCourseService.updateClassCourse(id, request);
        return ApiResponse.success("班级课程关联更新成功", response);
    }
    
    /**
     * 删除班级课程关联
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级课程关联", description = "删除指定班级课程关联")
    public ApiResponse<Void> deleteClassCourse(@Parameter(description = "关联ID") @PathVariable Long id) {
        classCourseService.deleteClassCourse(id);
        return ApiResponse.success("班级课程关联删除成功", null);
    }
    
    /**
     * 批量删除班级课程关联
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除班级课程关联", description = "批量删除多个班级课程关联")
    public ApiResponse<Void> batchDeleteClassCourses(@RequestBody List<Long> ids) {
        classCourseService.batchDeleteClassCourses(ids);
        return ApiResponse.success("班级课程关联批量删除成功", null);
    }
    
    /**
     * 根据班级ID删除所有关联
     */
    @DeleteMapping("/class/{classId}")
    @Operation(summary = "删除班级所有课程关联", description = "删除指定班级的所有课程关联")
    public ApiResponse<Void> deleteByClassId(@Parameter(description = "班级ID") @PathVariable Long classId) {
        classCourseService.deleteByClassId(classId);
        return ApiResponse.success("班级课程关联删除成功", null);
    }
    
    /**
     * 根据课程ID删除所有关联
     */
    @DeleteMapping("/course/{courseId}")
    @Operation(summary = "删除课程所有班级关联", description = "删除指定课程的所有班级关联")
    public ApiResponse<Void> deleteByCourseId(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        classCourseService.deleteByCourseId(courseId);
        return ApiResponse.success("课程班级关联删除成功", null);
    }
    
    /**
     * 根据ID获取班级课程关联
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取班级课程关联详情", description = "根据ID获取班级课程关联详细信息")
    public ApiResponse<ClassCourseResponse> getClassCourseById(@Parameter(description = "关联ID") @PathVariable Long id) {
        ClassCourseResponse response = classCourseService.getClassCourseById(id);
        return ApiResponse.success("获取班级课程关联详情成功", response);
    }
    
    /**
     * 根据班级ID获取所有关联的课程
     */
    @GetMapping("/class/{classId}")
    @Operation(summary = "获取班级课程列表", description = "根据班级ID获取所有关联的课程")
    public ApiResponse<List<ClassCourseResponse>> getClassCoursesByClassId(@Parameter(description = "班级ID") @PathVariable Long classId) {
        List<ClassCourseResponse> responses = classCourseService.getClassCoursesByClassId(classId);
        return ApiResponse.success("获取班级课程列表成功", responses);
    }
    
    /**
     * 根据课程ID获取所有关联的班级
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "获取课程班级列表", description = "根据课程ID获取所有关联的班级")
    public ApiResponse<List<ClassCourseResponse>> getClassCoursesByCourseId(@Parameter(description = "课程ID") @PathVariable Long courseId) {
        List<ClassCourseResponse> responses = classCourseService.getClassCoursesByCourseId(courseId);
        return ApiResponse.success("获取课程班级列表成功", responses);
    }
    
    /**
     * 分页获取班级课程关联
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取班级课程关联", description = "分页获取班级课程关联列表")
    public ApiResponse<PageResponse<ClassCourseResponse>> getClassCoursesWithPagination(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "班级ID") @RequestParam(required = false) Long classId,
            @Parameter(description = "课程ID") @RequestParam(required = false) Long courseId,
            @Parameter(description = "学期") @RequestParam(required = false) String semester,
            @Parameter(description = "学年") @RequestParam(required = false) String academicYear) {
        PageResponse<ClassCourseResponse> response = classCourseService.getClassCoursesWithPagination(
                page, size, classId, courseId, semester, academicYear);
        return ApiResponse.success("获取班级课程关联分页数据成功", response);
    }
    
    /**
     * 获取所有课程列表（用于添加课程时选择）
     */
    @GetMapping
    @Operation(summary = "获取所有课程列表", description = "获取所有可用课程列表，用于添加课程时选择")
    public ApiResponse<List<ClassCourseResponse>> getAllCourses() {
        List<ClassCourseResponse> responses = classCourseService.getAllCourses();
        return ApiResponse.success("获取所有课程列表成功", responses);
    }
}
