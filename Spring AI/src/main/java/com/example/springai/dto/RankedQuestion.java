package com.example.springai.dto;

import lombok.Data;

/**
 * 排序后的题目
 */
@Data
public class RankedQuestion {
    /**
     * 题目 ID
     */
    private Long questionId;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 题目类型
     */
    private String type;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 排名（1 = 最高优先级）
     */
    private Integer rank;

    /**
     * AI 推荐理由
     */
    private String reason;

    /**
     * 优先级：high/medium/low
     */
    private String priority;

    /**
     * Neo4j 原始权重
     */
    private Double neo4jWeight;
}
