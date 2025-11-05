package com.example.manger.config;

import com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository;
import com.alibaba.cloud.ai.memory.redis.RedissonRedisChatMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class MemoryConfig {

    private static final Logger log = LoggerFactory.getLogger(MemoryConfig.class);

    @Value("${spring.ai.memory.type:in-memory}")
    private String memoryType;

    // Redis config values (optional)
    @Value("${spring.ai.memory.redis.host:localhost}")
    private String redisHost;
    @Value("${spring.ai.memory.redis.port:6379}")
    private int redisPort;
    @Value("${spring.ai.memory.redis.password:}")
    private String redisPassword;
    @Value("${spring.ai.memory.redis.timeout:5000}")
    private int redisTimeout;

    // JDBC (MySQL) memory repository configuration
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.enabled:false}")
    private boolean jdbcEnabled;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.jdbc-url:}")
    private String mysqlJdbcUrl;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.username:}")
    private String mysqlUsername;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.password:}")
    private String mysqlPassword;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String mysqlDriverClassName;

    @Bean
    public MessageWindowChatMemory messageWindowChatMemory() {
        // 1) JDBC (MySQL) repository if explicitly enabled
        if (jdbcEnabled && mysqlJdbcUrl != null && !mysqlJdbcUrl.isEmpty()) {
            try {
                DriverManagerDataSource dataSource = new DriverManagerDataSource();
                dataSource.setDriverClassName(mysqlDriverClassName);
                dataSource.setUrl(mysqlJdbcUrl);
                dataSource.setUsername(mysqlUsername);
                dataSource.setPassword(mysqlPassword);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                MysqlChatMemoryRepository repo = MysqlChatMemoryRepository.mysqlBuilder()
                        .jdbcTemplate(jdbcTemplate)
                        .build();
                log.info("Using MySQL ChatMemoryRepository, url={}", mysqlJdbcUrl);
                return MessageWindowChatMemory.builder()
                        .chatMemoryRepository(repo)
                        .maxMessages(100)
                        .build();
            } catch (Exception ex) {
                // If JDBC setup fails, log to console and fall back to other types
                log.error("Failed to create MysqlChatMemoryRepository, fallback to other memory. Error: {}", ex.getMessage(), ex);
            }
        }

        // 2) Redis-backed memory
        if ("redis".equalsIgnoreCase(memoryType)) {
            RedissonRedisChatMemoryRepository redisRepo = RedissonRedisChatMemoryRepository.builder()
                    .host(redisHost)
                    .port(redisPort)
                    .timeout(redisTimeout)
                    .build();
            log.info("Using Redis ChatMemoryRepository, host={} port={}", redisHost, redisPort);
            return MessageWindowChatMemory.builder()
                    .chatMemoryRepository(redisRepo)
                    .maxMessages(100)
                    .build();
        }

        // 3) default to in-memory per-user memory
        InMemoryChatMemoryRepository inMemoryRepo = new InMemoryChatMemoryRepository();
        log.info("Using InMemory ChatMemoryRepository");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(inMemoryRepo)
                .maxMessages(100)
                .build();
    }
}
