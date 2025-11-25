package com.example.springai.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.springai.dto.RankedQuestion;
import com.example.springai.dto.StudentAnalysisReport;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * AI 排序服务
 * 基于学生分析报告，对 Neo4j 推荐的题目进行二次智能排序
 */
@Slf4j
@Service
public class AIRankingService {

    @Autowired
    private DashScopeChatModel chatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AI 二次优化题目排序
     * 
     * @param studentReport        学生分析报告
     * @param neo4jRecommendations Neo4j 推荐的题目列表
     * @return 重新排序后的题目列表
     */
    public List<RankedQuestion> reRankQuestions(
            StudentAnalysisReport studentReport,
            List<Map<String, Object>> neo4jRecommendations) {

        try {
            // 1. 构造 Prompt
            String prompt = buildRankingPrompt(studentReport, neo4jRecommendations);

            // 2. 调用 AI（带超时保护）
            log.info("Calling AI to re-rank {} questions", neo4jRecommendations.size());

            // Use CompletableFuture to add a timeout around the blocking chatModel.call
            CompletableFuture<org.springframework.ai.chat.model.ChatResponse> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return chatModel.call(new org.springframework.ai.chat.prompt.Prompt(prompt));
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

            org.springframework.ai.chat.model.ChatResponse response;
            try {
                // wait up to 15 seconds for the AI response
                response = future.get(15, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                log.warn("AI ranking call timed out after 15 seconds", te);
                future.cancel(true);
                // 降级：使用 Neo4j 原始排序
                return createDefaultRanking(neo4jRecommendations);
            }

            String aiResponse = response.getResult().getOutput().getText();

            log.info("AI Ranking Response: {}", aiResponse);

            // 3. 解析排序结果
            return parseRankedQuestions(aiResponse, neo4jRecommendations);

        } catch (Exception e) {
            log.error("Failed to re-rank questions with AI", e);
            // 降级：使用 Neo4j 原始排序
            return createDefaultRanking(neo4jRecommendations);
        }
    }

    /**
     * 构造排序 Prompt
     */
    private String buildRankingPrompt(StudentAnalysisReport report, List<Map<String, Object>> questions) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位教育专家。根据学生的薄弱点分析，请重新排序以下题目，优先推荐最能帮助学生的题目。\n\n");

        sb.append("学生薄弱点：\n");
        if (report.getWeakPoints() != null) {
            for (String point : report.getWeakPoints()) {
                sb.append("- ").append(point).append("\n");
            }
        }

        sb.append("\n错误模式：\n");
        if (report.getErrorPatterns() != null) {
            for (String pattern : report.getErrorPatterns()) {
                sb.append("- ").append(pattern).append("\n");
            }
        }

        sb.append("\n候选题目列表：\n");
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);
            sb.append(i + 1).append(". ID=").append(q.get("id"))
                    .append(", 内容=\"").append(truncate((String) q.get("content"), 50)).append("\"")
                    .append(", 难度=").append(q.get("difficulty"))
                    .append("\n");
        }

        sb.append("\n请返回 JSON 数组，格式如下：\n");
        sb.append("[\n");
        sb.append("  {\"questionId\": 1, \"rank\": 1, \"reason\": \"推荐理由\", \"priority\": \"high\"},\n");
        sb.append("  {\"questionId\": 2, \"rank\": 2, \"reason\": \"推荐理由\", \"priority\": \"medium\"}\n");
        sb.append("]\n");
        sb.append("\n注意：rank 从 1 开始，数字越小优先级越高。priority 可选值：high/medium/low");

        return sb.toString();
    }

    /**
     * 解析 AI 返回的排序结果
     */
    private List<RankedQuestion> parseRankedQuestions(String aiResponse, List<Map<String, Object>> originalQuestions) {
        try {
            // 提取 JSON 数组
            String jsonPart = extractJSONArray(aiResponse);

            // 解析 JSON
            List<Map<String, Object>> rankingList = objectMapper.readValue(
                    jsonPart,
                    new TypeReference<List<Map<String, Object>>>() {
                    });

            // 转换为 RankedQuestion
            List<RankedQuestion> result = new ArrayList<>();
            for (Map<String, Object> item : rankingList) {
                Long questionId = ((Number) item.get("questionId")).longValue();

                // 查找原始题目数据
                Map<String, Object> originalQ = findQuestionById(originalQuestions, questionId);
                if (originalQ == null)
                    continue;

                RankedQuestion rq = new RankedQuestion();
                rq.setQuestionId(questionId);
                rq.setContent((String) originalQ.get("content"));
                rq.setType((String) originalQ.get("type"));
                rq.setDifficulty((String) originalQ.get("difficulty"));
                rq.setRank(((Number) item.get("rank")).intValue());
                rq.setReason((String) item.get("reason"));
                rq.setPriority((String) item.get("priority"));

                result.add(rq);
            }

            // 按 rank 排序
            result.sort((a, b) -> a.getRank().compareTo(b.getRank()));

            return result;

        } catch (Exception e) {
            log.error("Failed to parse AI ranking response", e);
            return createDefaultRanking(originalQuestions);
        }
    }

    /**
     * 从文本中提取 JSON 数组
     */
    private String extractJSONArray(String text) {
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');

        if (start != -1 && end != -1 && start < end) {
            return text.substring(start, end + 1);
        }

        return text;
    }

    /**
     * 根据 ID 查找题目
     */
    private Map<String, Object> findQuestionById(List<Map<String, Object>> questions, Long id) {
        return questions.stream()
                .filter(q -> {
                    Object qId = q.get("id");
                    if (qId instanceof Number) {
                        return ((Number) qId).longValue() == id;
                    }
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    /**
     * 创建默认排序（AI 失败时使用 Neo4j 原始顺序）
     */
    private List<RankedQuestion> createDefaultRanking(List<Map<String, Object>> questions) {
        List<RankedQuestion> result = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> q = questions.get(i);

            RankedQuestion rq = new RankedQuestion();
            rq.setQuestionId(((Number) q.get("id")).longValue());
            rq.setContent((String) q.get("content"));
            rq.setType((String) q.get("type"));
            rq.setDifficulty((String) q.get("difficulty"));
            rq.setRank(i + 1);
            rq.setReason("基于 Neo4j 图关系推荐");
            rq.setPriority("medium");

            result.add(rq);
        }

        return result;
    }

    /**
     * 截断字符串
     */
    private String truncate(String str, int maxLength) {
        if (str == null)
            return "";
        if (str.length() <= maxLength)
            return str;
        return str.substring(0, maxLength) + "...";
    }
}
