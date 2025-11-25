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
                .description("钛考在线考试系统的RESTful API文档，包含用户认证、考试管理、题库管理、权限管理、学生考试、老师判卷等完整功能")
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
                new Tag().name("考试管理").description("考试创建、编辑、删除、开始、结束等管理接口"),
                new Tag().name("学生考试").description("学生端考试相关接口，包括获取考试列表、开始考试、提交答案等"),
                new Tag().name("考试判卷").description("老师端判卷相关接口，包括获取学生答案、保存判卷结果等"),
                new Tag().name("考试结果").description("考试结果查看相关接口，包括学生查看成绩、老师查看统计等"),
                new Tag().name("代码执行").description("代码执行相关接口（使用 Docker 安全执行 Java、Python、C++ 代码）"),
                new Tag().name("仪表盘").description("系统仪表盘统计数据相关接口"),
                new Tag().name("通用工具").description("通用工具接口，如获取客户端IP等")
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
