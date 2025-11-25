package com.example.springai.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.springai.dto.StudentAnalysisReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 分析服务
 * 使用 Spring AI Alibaba (通义千问) 进行智能分析
 */
@Slf4j
@Service
public class AIAnalysisService {

    @Autowired
    private DashScopeChatModel chatModel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 分析学生考试结果，识别薄弱点
     * 
     * @param studentId   学生 ID
     * @param examResults 学生的考试结果数据
     * @return 学生分析报告
     */
    public StudentAnalysisReport analyzeStudentPerformance(Long studentId, List<Map<String, Object>> examResults) {
        try {
            // 1. 构造 Prompt
            String prompt = buildStudentAnalysisPrompt(studentId, examResults);

            // 2. 调用 AI
            log.info("Calling AI to analyze student {} performance", studentId);
            ChatResponse response = chatModel.call(new Prompt(prompt));
            String aiResponse = response.getResult().getOutput().getText();

            log.info("AI Response: {}", aiResponse);

            // 3. 解析 JSON 响应
            return parseStudentAnalysisReport(aiResponse);

        } catch (Exception e) {
            log.error("Failed to analyze student performance", e);
            // 返回默认报告
            return createDefaultReport();
        }
    }

    /**
     * 构造学生分析 Prompt
     */
    private String buildStudentAnalysisPrompt(Long studentId, List<Map<String, Object>> examResults) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位经验丰富的教育专家。请分析以下学生的考试结果，识别薄弱知识点和学习模式。\n\n");
        sb.append("学生 ID: ").append(studentId).append("\n\n");
        sb.append("考试结果：\n");

        for (Map<String, Object> result : examResults) {
            sb.append("- 题目 ID: ").append(result.get("questionId"))
                    .append(", 得分: ").append(result.get("score"))
                    .append(", 知识点: ").append(result.get("knowledgePoints"))
                    .append("\n");
        }

        sb.append("\n请以 JSON 格式返回分析结果，格式如下：\n");
        sb.append("{\n");
        sb.append("  \"weakPoints\": [\"薄弱知识点1\", \"薄弱知识点2\"],\n");
        sb.append("  \"errorPatterns\": [\"错误模式1\", \"错误模式2\"],\n");
        sb.append("  \"recommendations\": [\"学习建议1\", \"学习建议2\"],\n");
        sb.append("  \"confidenceLevel\": \"high\",\n");
        sb.append("  \"summary\": \"分析摘要\"\n");
        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 解析 AI 返回的 JSON 报告
     */
    private StudentAnalysisReport parseStudentAnalysisReport(String aiResponse) {
        try {
            // 提取 JSON 部分（AI 可能返回额外的文本）
            String jsonPart = extractJSON(aiResponse);

            // 解析 JSON
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(jsonPart, Map.class);

            StudentAnalysisReport report = new StudentAnalysisReport();
            report.setWeakPoints((List<String>) map.get("weakPoints"));
            report.setErrorPatterns((List<String>) map.get("errorPatterns"));
            report.setRecommendations((List<String>) map.get("recommendations"));
            report.setConfidenceLevel((String) map.get("confidenceLevel"));
            report.setSummary((String) map.get("summary"));

            return report;

        } catch (Exception e) {
            log.error("Failed to parse AI response", e);
            return createDefaultReport();
        }
    }

    /**
     * 从 AI 响应中提取 JSON 部分
     */
    private String extractJSON(String text) {
        // 查找第一个 { 和最后一个 }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');

        if (start != -1 && end != -1 && start < end) {
            return text.substring(start, end + 1);
        }

        return text;
    }

    /**
     * 创建默认报告（AI 调用失败时使用）
     */
    private StudentAnalysisReport createDefaultReport() {
        StudentAnalysisReport report = new StudentAnalysisReport();
        report.setWeakPoints(new ArrayList<>());
        report.setErrorPatterns(new ArrayList<>());
        report.setRecommendations(List.of("建议多做练习", "复习基础知识"));
        report.setConfidenceLevel("low");
        report.setSummary("AI 分析暂时不可用，使用默认推荐");
        return report;
    }
}
