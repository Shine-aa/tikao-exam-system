package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import com.example.manger.dto.DashboardStatsResponse;
import com.example.manger.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "仪表盘", description = "系统仪表盘统计数据相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "获取仪表盘统计数据", description = "获取系统仪表盘的各种统计数据")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未授权"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "权限不足")
    })
    public ApiResponse<DashboardStatsResponse> getDashboardStats() {
        try {
            DashboardStatsResponse stats = dashboardService.getDashboardStats();
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
