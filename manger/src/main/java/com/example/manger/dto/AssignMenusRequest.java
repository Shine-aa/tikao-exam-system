package com.example.manger.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 分配菜单请求DTO
 */
@Data
public class AssignMenusRequest {
    
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    
    private List<Long> menuIds;
}
