package com.example.springai.controller;

import com.example.springai.dto.PaperGenerationRequest;
import com.example.springai.service.SmartExamService;
import com.example.springai.service.TaiKaoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/smart-exam")
@CrossOrigin(origins = "*") // Allow all origins for demo purposes
public class SmartExamController {

    @Autowired
    private SmartExamService smartExamService;

    @Autowired
    private TaiKaoApiService taiKaoApiService;

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
     * 从 Authorization header 中提取 token
     */
    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
