package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.context.BaseContext;
import com.example.manger.dto.PageResponse;
import com.example.manger.dto.PaperGenerationRequest;
import com.example.manger.dto.PaperManualCreateRequest;
import com.example.manger.dto.PaperResponse;
import com.example.manger.service.PaperGenerationService;
import com.example.manger.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 试卷生成控制器
 */
@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
@Tag(name = "试卷生成", description = "试卷生成和管理相关接口")
public class PaperGenerationController {
    
    private final PaperGenerationService paperGenerationService;
    private final JwtUtil jwtUtil;
    
    /**
     * Author：李正阳，李子政
     * 生成试卷
     */
    @PostMapping("/generate")
    @Operation(summary = "生成试卷", description = "根据配置参数智能生成试卷")
    public ApiResponse<PaperResponse> generatePaper(@Valid @RequestBody PaperGenerationRequest request) {
        Long teacherId = getCurrentUserId();
        PaperResponse response = paperGenerationService.generatePaper(request, teacherId);
        return ApiResponse.success("试卷生成成功", response);
    }

    /**
     * Author：李子政
     * 手动创建试卷（新增接口）
     */
    @PostMapping("/manual/create")
    @Operation(summary = "手动创建试卷", description = "手动选择题目组卷，支持自定义题目顺序和分值")
    public ApiResponse<PaperResponse> createPaperManual(@Valid @RequestBody PaperManualCreateRequest request) {
        Long teacherId = getCurrentUserId();
        PaperResponse response = paperGenerationService.createPaperManual(request, teacherId);
        return ApiResponse.success("手动组卷成功", response);
    }
    
    /**
     * Author：李正阳，李子政
     * 分页获取试卷列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取试卷", description = "分页获取所有生成的试卷")
    public ApiResponse<PageResponse<PaperResponse>> getPapersWithPagination(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        
        PageResponse<PaperResponse> response = paperGenerationService.getAllPapersWithPagination(page, size, keyword);
        return ApiResponse.success("获取试卷列表成功", response);
    }
    
    /**
     * Author：李正阳，李子政
     * 根据ID获取试卷详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取试卷详情", description = "根据ID获取试卷详细信息")
    public ApiResponse<PaperResponse> getPaperById(@Parameter(description = "试卷ID") @PathVariable Long id) {
        PaperResponse response = paperGenerationService.getPaperById(id);
        return ApiResponse.success("获取试卷成功", response);
    }
    
    /**
     * Author：李正阳
     * 删除试卷
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除试卷", description = "删除指定ID的试卷")
    public ApiResponse<Void> deletePaper(@Parameter(description = "试卷ID") @PathVariable Long id) {
        paperGenerationService.deletePaper(id);
        return ApiResponse.success("试卷删除成功", null);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return BaseContext.getCurrentId();
    }
}
