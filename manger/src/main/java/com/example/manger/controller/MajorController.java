package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.MajorRequest;
import com.example.manger.dto.MajorResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.service.MajorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 专业管理控制器
 */
@RestController
@RequestMapping("/api/majors")
@Tag(name = "专业管理", description = "专业管理相关接口")
public class MajorController {
    
    @Autowired
    private MajorService majorService;
    
    /**
     * 创建专业
     */
    @PostMapping
    @Operation(summary = "创建专业", description = "创建新的专业")
    public ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        MajorResponse response = majorService.createMajor(request);
        return ApiResponse.success("专业创建成功", response);
    }
    
    /**
     * 更新专业
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新专业", description = "更新指定专业信息")
    public ApiResponse<MajorResponse> updateMajor(
            @Parameter(description = "专业ID") @PathVariable Long id,
            @Valid @RequestBody MajorRequest request) {
        MajorResponse response = majorService.updateMajor(id, request);
        return ApiResponse.success("专业更新成功", response);
    }
    
    /**
     * 删除专业
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除专业", description = "删除指定专业")
    public ApiResponse<Void> deleteMajor(@Parameter(description = "专业ID") @PathVariable Long id) {
        majorService.deleteMajor(id);
        return ApiResponse.success("专业删除成功", null);
    }
    
    /**
     * 批量删除专业
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除专业", description = "批量删除多个专业")
    public ApiResponse<Void> batchDeleteMajors(@RequestBody List<Long> ids) {
        majorService.batchDeleteMajors(ids);
        return ApiResponse.success("专业批量删除成功", null);
    }
    
    /**
     * 根据ID获取专业
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取专业详情", description = "根据ID获取专业详细信息")
    public ApiResponse<MajorResponse> getMajorById(@Parameter(description = "专业ID") @PathVariable Long id) {
        MajorResponse response = majorService.getMajorById(id);
        return ApiResponse.success("获取专业详情成功", response);
    }
    
    /**
     * 获取所有专业
     */
    @GetMapping
    @Operation(summary = "获取所有专业", description = "获取所有启用的专业列表")
    public ApiResponse<List<MajorResponse>> getAllMajors() {
        List<MajorResponse> responses = majorService.getAllMajors();
        return ApiResponse.success("获取专业列表成功", responses);
    }
    
    /**
     * 分页获取专业
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取专业", description = "分页获取专业列表")
    public ApiResponse<PageResponse<MajorResponse>> getMajorsWithPagination(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        PageResponse<MajorResponse> response = majorService.getMajorsWithPagination(page, size, keyword);
        return ApiResponse.success("获取专业分页数据成功", response);
    }
}
