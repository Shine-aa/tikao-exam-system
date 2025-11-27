package com.example.springai.config;

import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 * 
 * Spring AI Alibaba 会自动配置 ChatModel Bean
 * 只需在 application.yml 中配置 spring.ai.dashscope.api-key 即可
 * 
 * 如果自动配置不生效,可能需要添加:
 * 
 * @EnableAutoConfiguration
 *                          或检查 application.yml 中的配置是否正确
 */
@Configuration
public class AIConfig {
    // Spring AI Alibaba Starter 会自动配置:
    // - ChatModel (DashScopeChatModel的实现)
    // - ChatClient.Builder
    // - DashScopeChatOptions
}
