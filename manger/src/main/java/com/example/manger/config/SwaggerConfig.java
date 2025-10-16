package com.example.manger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("钛考在线考试系统API")
                .version("1.0.0")
                .description("钛考在线考试系统的RESTful API文档，包含用户认证、考试管理、题库管理、权限管理等功能")
                .contact(new Contact()
                    .name("开发团队")
                    .email("dev@example.com")
                    .url("https://example.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("开发环境"),
                new Server().url("https://api.example.com").description("生产环境")
            ))
            .tags(List.of(
                new Tag().name("认证管理").description("用户登录、注册、验证码等认证相关接口"),
                new Tag().name("用户管理").description("用户信息管理、个人资料等接口"),
                new Tag().name("用户工具").description("用户名和邮箱可用性检查等工具接口"),
                new Tag().name("角色管理").description("角色信息管理相关接口"),
                new Tag().name("权限管理").description("权限信息管理相关接口"),
                new Tag().name("菜单管理").description("菜单管理相关接口"),
                new Tag().name("题库管理").description("题库管理相关接口"),
                new Tag().name("课程管理").description("课程管理相关接口"),
                new Tag().name("班级管理").description("班级管理相关接口"),
                new Tag().name("专业管理").description("专业管理相关接口"),
                new Tag().name("班级课程关联").description("班级课程关联管理相关接口"),
                new Tag().name("仪表盘").description("系统仪表盘统计数据相关接口")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
            .components(new Components()
                .addSecuritySchemes("Bearer Authentication", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("请输入JWT Token，格式：Bearer {token}")));
    }
}
