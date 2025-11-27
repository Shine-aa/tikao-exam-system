package com.example.springai.tool;

import com.example.springai.service.SmartExamService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class SmartExamTool {

    private final SmartExamService smartExamService;

    public SmartExamTool(SmartExamService smartExamService) {
        this.smartExamService = smartExamService;
    }

    @Tool(description = "根据学生的ID，智能生成一套针对其薄弱知识点的练习题。返回题目列表。")
    public String generateExamPaper(
            @ToolParam(description = "学生的ID，默认为2") Long studentId) {

        SmartExamService.SmartExamResult result = smartExamService.generatePaperForStudent(studentId, 5);
        return "生成了 " + result.getTotalQuestions() + " 道题目：" + result.getQuestions().toString();
    }
}
