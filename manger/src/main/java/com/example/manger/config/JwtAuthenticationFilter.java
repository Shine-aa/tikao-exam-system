package com.example.manger.config;

import com.example.manger.entity.User;
import com.example.manger.repository.UserRepository;
import com.example.manger.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author：李正阳，李子政
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // 定义需要跳过JWT校验的接口白名单（和SecurityConfig的permitAll保持一致）
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/auth/**",
            "/api/code/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        logger.info("JWT Filter - Request URI: " + requestUri);

        // 关键：如果是白名单接口，直接放行，不做JWT校验
        if (isWhiteListed(requestUri)) {
            logger.info("JWT Filter - Skipping white-listed URI: " + requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");
        logger.info("JWT Filter - Request URI: " + request.getRequestURI());
        logger.info("JWT Filter - Authorization Header: " + authorizationHeader);

        // 1. 无令牌或非Bearer格式：直接返回401
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("JWT Filter - No valid Authorization header");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "未提供有效的令牌");
            return; // 终止过滤器链，不再继续执行
        }

        String token = authorizationHeader.substring(7);
        logger.info("JWT Filter - Token found, validating...");

        try {
            // 2. 令牌验证失败
            if (!jwtUtil.checkToken(token)) {
                logger.warn("JWT Filter - Token validation failed");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "令牌无效");
                return;
            }

            logger.info("JWT Filter - Token is valid");
            Long userId = jwtUtil.getUserIdFromToken(token);

            User user = userRepository.findByIdWithRoles(userId).orElse(null);
            // 3. 用户不存在或未激活
            if (user == null || !user.getIsActive()) {
                logger.warn("JWT Filter - User not found or inactive");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "用户不存在或已停用");
                return;
            }

            // 以下是正常的权限设置逻辑（不变）
            logger.info("JWT Filter - User found and active: " + user.getUsername());

            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            // 获取用户角色
            Set<SimpleGrantedAuthority> roleAuthorities = user.getRoles().stream()
                    .filter(role -> role.getIsActive())
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()))
                    .collect(Collectors.toSet());
            authorities.addAll(roleAuthorities);

            // 获取用户权限
            Set<SimpleGrantedAuthority> permissionAuthorities = user.getRoles().stream()
                    .filter(role -> role.getIsActive())
                    .flatMap(role -> role.getPermissions().stream())
                    .filter(permission -> permission.getIsActive())
                    .map(permission -> new SimpleGrantedAuthority(permission.getPermissionCode()))
                    .collect(Collectors.toSet());
            authorities.addAll(permissionAuthorities);

            logger.info("JWT Filter - User authorities: " + authorities);
            logger.info("JWT Filter - User roles from database: " + user.getRoles().stream()
                    .map(role -> role.getRoleCode())
                    .collect(Collectors.toList()));
            logger.info("JWT Filter - User permissions from database: " + user.getRoles().stream()
                    .filter(role -> role.getIsActive())
                    .flatMap(role -> role.getPermissions().stream())
                    .filter(permission -> permission.getIsActive())
                    .map(permission -> permission.getPermissionCode())
                    .collect(Collectors.toList()));

            // 如果没有角色，给一个默认角色
            if (authorities.isEmpty()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                logger.info("JWT Filter - No roles found, adding default ROLE_USER");
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("JWT Filter - Authentication set successfully");

        } catch (Exception e) {
            // 4. 令牌验证过程中异常（如过期、签名错误等）
            logger.error("JWT Filter - Token validation error: " + e.getMessage(), e);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "令牌验证失败：" + e.getMessage());
            return;
        }

        // 所有验证通过，继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

    // 校验请求URI是否在白名单中（支持通配符**）
    private boolean isWhiteListed(String requestUri) {
        return WHITE_LIST.stream().anyMatch(pattern -> {
            // 处理**通配符：将/api/auth/**转换为正则表达式
            String regex = pattern.replace("**", ".*");
            return requestUri.matches(regex);
        });
    }

    // 封装401响应的工具方法
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        // 返回JSON格式的错误信息（方便前端解析）
        response.getWriter().write("{\"code\":" + status + ",\"message\":\"" + message + "\"}");
    }
}
