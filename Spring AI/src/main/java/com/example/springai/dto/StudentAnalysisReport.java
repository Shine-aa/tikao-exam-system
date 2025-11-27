package com.example.springai.dto;

import lombok.Data;
import java.util.List;

/**
 * 学生分析报告
 */
@Data
public class StudentAnalysisReport {
    /**
     * 薄弱知识点列表
     */
    private List<String> weakPoints;

    /**
     * 错误模式
     */
    private List<String> errorPatterns;

    /**
     * AI 建议
     */
    private List<String> recommendations;

    /**
     * 置信度：high/medium/low
     */
    private String confidenceLevel;

    /**
     * 分析摘要
     */
    private String summary;
}
