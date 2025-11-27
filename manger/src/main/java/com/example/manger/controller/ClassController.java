package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.ClassRequest;
import com.example.manger.dto.ClassResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 班级管理控制器
 */
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@Tag(name = "班级管理", description = "班级管理相关接口")
public class ClassController {
    
    private final ClassService classService;
    
    /**
     * Author：李正阳，郭依林
     * 创建班级
     */
    @PostMapping
    @Operation(summary = "创建班级", description = "创建新的班级")
    public ApiResponse<ClassResponse> createClass(@Valid @RequestBody ClassRequest request) {
        request.setTeacherId(BaseContext.getCurrentId());
        ClassResponse response = classService.createClass(request);
        return ApiResponse.success("班级创建成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 更新班级
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新班级", description = "更新指定ID的班级信息")
    public ApiResponse<ClassResponse> updateClass(
            @Parameter(description = "班级ID") @PathVariable Long id,
            @Valid @RequestBody ClassRequest request) {
        ClassResponse response = classService.updateClass(id, request);
        return ApiResponse.success("班级更新成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 删除班级
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除班级", description = "删除指定ID的班级")
    public ApiResponse<Void> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        classService.deleteClass(id);
        return ApiResponse.success("班级删除成功", null);
    }
    
    /**
     * Author：李正阳，郭依林
     * 根据ID获取班级
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取班级详情", description = "根据ID获取班级详细信息")
    public ApiResponse<ClassResponse> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        ClassResponse response = classService.getClassById(id);
        return ApiResponse.success("获取班级成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 分页获取教师的班级
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取班级", description = "分页获取当前教师的班级列表")
    public ApiResponse<PageResponse<ClassResponse>> getClassesWithPagination(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "专业ID") @RequestParam(required = false) Long majorId,
            @Parameter(description = "年级") @RequestParam(required = false) String grade) {
        
        Long teacherId = getCurrentUserId();
        PageResponse<ClassResponse> response = classService.getClassesWithPagination(page, size, teacherId, majorId, grade, keyword);
        return ApiResponse.success("获取班级列表成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 获取教师的所有班级
     */
    @GetMapping
    @Operation(summary = "获取所有班级", description = "获取当前教师的所有班级")
    public ApiResponse<List<ClassResponse>> getClassesByTeacher() {
        Long teacherId = getCurrentUserId();
        List<ClassResponse> response = classService.getClassesByTeacherId(teacherId);
        return ApiResponse.success("获取班级列表成功", response);
    }

    /**
     * Author：李正阳，郭依林
     * 获取教师的所有班级
     */
    @GetMapping("/getAll")
    @Operation(summary = "获取所有班级", description = "获取所有班级")
    public ApiResponse<List<ClassResponse>> getAllClasses() {
        List<ClassResponse> response = classService.getAllClasses();
        return ApiResponse.success("获取班级列表成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 根据专业ID获取班级
     */
    @GetMapping("/major/{majorId}")
    @Operation(summary = "根据专业获取班级", description = "根据专业ID获取该专业下的所有班级")
    public ApiResponse<List<ClassResponse>> getClassesByMajor(
            @Parameter(description = "专业ID") @PathVariable Long majorId) {
        List<ClassResponse> response = classService.getClassesByMajorId(majorId);
        return ApiResponse.success("获取班级列表成功", response);
    }
    
    /**
     * Author：李正阳，郭依林
     * 批量删除班级
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除班级", description = "根据班级ID列表批量删除班级")
    public ApiResponse<Void> batchDeleteClasses(@RequestBody List<Long> ids) {
        classService.batchDeleteClasses(ids);
        return ApiResponse.success("批量删除成功", null);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return BaseContext.getCurrentId();
    }
}
