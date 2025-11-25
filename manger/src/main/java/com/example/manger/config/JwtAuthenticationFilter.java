package com.example.manger.config;

import com.example.manger.entity.User;
import com.example.manger.repository.UserRepository;
import com.example.manger.util.JwtUtil;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authorizationHeader = request.getHeader("Authorization");
        logger.info("JWT Filter - Request URI: " + request.getRequestURI());
        logger.info("JWT Filter - Authorization Header: " + authorizationHeader);
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            logger.info("JWT Filter - Token found, validating...");
            
            try {
                if (jwtUtil.validateToken(token)) {
                    logger.info("JWT Filter - Token is valid");
                    String username = jwtUtil.getUsernameFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    logger.info("JWT Filter - Username: " + username + ", UserId: " + userId);
                    
                    User user = userRepository.findByIdWithRoles(userId).orElse(null);
                    if (user != null && user.getIsActive()) {
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
                    } else {
                        logger.warn("JWT Filter - User not found or inactive");
                    }
                } else {
                    logger.warn("JWT Filter - Token validation failed");
                }
            } catch (Exception e) {
                // Token无效，继续处理请求但不设置认证信息
                logger.error("JWT Filter - Token validation error: " + e.getMessage(), e);
            }
        } else {
            logger.info("JWT Filter - No Authorization header or not Bearer token");
        }
        
        filterChain.doFilter(request, response);
    }
}
