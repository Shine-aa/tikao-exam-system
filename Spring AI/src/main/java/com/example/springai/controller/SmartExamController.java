package com.example.springai.controller;

import com.example.springai.dto.PaperGenerationRequest;
import com.example.springai.dto.RankedQuestion;
import com.example.springai.dto.StudentAnalysisReport;
import com.example.springai.service.AIAnalysisService;
import com.example.springai.service.AIRankingService;
import com.example.springai.service.SmartExamService;
import com.example.springai.service.TaiKaoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/smart-exam")
// CORS 已在 WebConfig 中全局配置，此处不需要重复配置
public class SmartExamController {

    @Autowired
    private SmartExamService smartExamService;

    @Autowired
    private TaiKaoApiService taiKaoApiService;

    @Autowired
    private AIAnalysisService aiAnalysisService;

    @Autowired
    private AIRankingService aiRankingService;

    /**
     * 智能组卷 - 生成完整试卷并保存到数据库
     */
    @PostMapping("/generate")
    public Object generateExam(
            @RequestParam(defaultValue = "2") Long studentId,
            @RequestParam(defaultValue = "20") int count,
            @RequestParam String paperName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "120") Integer durationMinutes,
            @RequestParam(defaultValue = "100") Integer totalPoints,
            @RequestParam(required = false) Long classId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // 1. 调用 Neo4j 获取推荐题目
            SmartExamService.SmartExamResult examResult = smartExamService.generatePaperForStudent(studentId, count);

            // 2. 提取题目 ID 列表
            List<Long> questionIds = examResult.getQuestions().stream()
                    .map(q -> ((Number) q.get("id")).longValue())
                    .collect(Collectors.toList());

            // 3. 构造试卷生成请求
            PaperGenerationRequest request = new PaperGenerationRequest();
            request.setPaperName(paperName);
            request.setDescription("智能组卷 - 基于学生薄弱点生成");
            request.setCourseId(courseId);
            request.setClassId(classId);
            request.setDurationMinutes(durationMinutes);
            request.setTotalQuestions(examResult.getTotalQuestions());
            request.setTotalPoints(totalPoints);
            request.setQuestionTypeDistribution(examResult.getQuestionTypeDistribution());
            request.setDifficultyDistribution(examResult.getDifficultyDistribution());

            // 4. 调用 TaiKao 后端生成试卷
            String token = extractToken(authHeader);
            Map<String, Object> response = taiKaoApiService.generatePaper(request, token);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "生成失败: " + e.getMessage());
        }
    }

    /**
     * AI 增强智能组卷 - 使用 AI 分析学生并二次优化推荐
     */
    @PostMapping("/generate-ai-enhanced")
    public Object generateAIEnhancedExam(
            @RequestParam(defaultValue = "2") Long studentId,
            @RequestParam(defaultValue = "20") int count,
            @RequestParam String paperName,
            @RequestParam Long courseId,
            @RequestParam(defaultValue = "120") Integer durationMinutes,
            @RequestParam(defaultValue = "100") Integer totalPoints,
            @RequestParam(required = false) Long classId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            System.out.println("DEBUG: Received AI Enhanced request for studentId=" + studentId);
            // 1. AI 分析学生考试结果（模拟数据，实际应从数据库获取）
            List<Map<String, Object>> mockExamResults = createMockExamResults(studentId);
            StudentAnalysisReport studentReport = aiAnalysisService.analyzeStudentPerformance(studentId,
                    mockExamResults);

            // 2. Neo4j 推荐题目（获取 2 倍数量，供 AI 筛选）
            SmartExamService.SmartExamResult neo4jResult = smartExamService.generatePaperForStudent(studentId,
                    count * 2);

            // 3. AI 二次优化排序
            List<RankedQuestion> rankedQuestions = aiRankingService.reRankQuestions(
                    studentReport,
                    neo4jResult.getQuestions());

            // 4. 取前 N 道题
            List<RankedQuestion> finalQuestions = rankedQuestions.stream()
                    .limit(count)
                    .collect(Collectors.toList());

            // 5. 提取题目 ID 列表
            List<Long> questionIds = finalQuestions.stream()
                    .map(RankedQuestion::getQuestionId)
                    .collect(Collectors.toList());

            // 6. 构造试卷生成请求
            PaperGenerationRequest request = new PaperGenerationRequest();
            request.setPaperName(paperName + " (AI增强)");
            request.setDescription("AI智能组卷 - 基于学生薄弱点深度分析");
            request.setCourseId(courseId);
            request.setClassId(classId);
            request.setDurationMinutes(durationMinutes);
            request.setTotalQuestions(finalQuestions.size());
            request.setTotalPoints(totalPoints);

            // 7. 调用 TaiKao 后端生成试卷
            String token = extractToken(authHeader);
            Map<String, Object> paperResponse = taiKaoApiService.generatePaper(request, token);

            // 8. 返回增强结果（包含 AI 分析报告和排序理由）
            Map<String, Object> enhancedResponse = new HashMap<>();
            enhancedResponse.put("paper", paperResponse);
            enhancedResponse.put("studentAnalysis", studentReport);
            enhancedResponse.put("rankedQuestions", finalQuestions);
            enhancedResponse.put("aiEnhanced", true);

            return enhancedResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("error", "AI增强组卷失败: " + e.getMessage());
        }
    }

    /**
     * 创建模拟考试结果数据（实际应从数据库查询）
     */
    private List<Map<String, Object>> createMockExamResults(Long studentId) {
        List<Map<String, Object>> results = new ArrayList<>();

        // 模拟一些错题记录
        results.add(Map.of(
                "questionId", 1,
                "score", 0,
                "knowledgePoints", "Java基础,面向对象"));
        results.add(Map.of(
                "questionId", 5,
                "score", 0,
                "knowledgePoints", "Java基础,关键字"));
        results.add(Map.of(
                "questionId", 47,
                "score", 0,
                "knowledgePoints", "数据结构,队列"));

        return results;
    }

    /**
     * 从 Authorization header 中提取 token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
