package com.example.manger.controller;

import com.example.manger.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用API控制器
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    /**
     * 获取客户端IP地址
     */
    @GetMapping("/client-ip")
    public ApiResponse<String> getClientIP(HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        return ApiResponse.success(ipAddress);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = null;
        
        // 按优先级顺序尝试获取IP
        String[] headerNames = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String headerName : headerNames) {
            ip = request.getHeader(headerName);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 检查是否包含多个IP（通过逗号分隔）
                if (ip.contains(",")) {
                    return "用户IP异常";
                }
                break;
            }
        }
        
        // 如果以上都没获取到，使用RemoteAddr
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip != null ? ip : "unknown";
    }
}
