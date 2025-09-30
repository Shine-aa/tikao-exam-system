package com.example.manger.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStatsResponse {
    
    private Long totalUsers;
    private Long totalRoles;
    private Long totalPermissions;
    private Long activeUsers;
    private List<UserResponse> recentUsers;
}
