package com.example.springai.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartExamService {

    @Autowired
    private Neo4jClient neo4jClient;

    /**
     * 智能组卷结果
     */
    @Data
    public static class SmartExamResult {
        private List<Map<String, Object>> questions;
        private Map<String, Integer> questionTypeDistribution;
        private Map<String, Integer> difficultyDistribution;
        private int totalQuestions;
    }

    /**
     * 智能组卷核心方法
     * 
     * @param studentId     学生ID
     * @param questionCount 需要生成的题目数量
     * @return 推荐的题目列表及统计信息
     */
    public SmartExamResult generatePaperForStudent(Long studentId, int questionCount) {
        String cypherQuery = """
                    // 1. 查找所有题目，并尝试关联学生的薄弱点
                    MATCH (q:Question)

                    // 2. 尝试匹配直接薄弱点
                    OPTIONAL MATCH (s:Student {id: $studentId})-[r:WRONG_ON]->(weak_kp:KnowledgePoint)<-[:TESTS]-(q)

                    // 3. 尝试匹配薄弱点的前驱知识点
                    OPTIONAL MATCH (weak_kp)-[:PRECEDES]->(pre_kp:KnowledgePoint)<-[:TESTS]-(q)

                    // 4. 计算权重：直接薄弱点 > 前驱薄弱点 > 随机补充
                    WITH q,
                         CASE
                            WHEN weak_kp IS NOT NULL THEN 10
                            WHEN pre_kp IS NOT NULL THEN 5
                            ELSE 0
                         END AS weight,
                         coalesce(weak_kp.name, pre_kp.name, '智能补充') as reason

                    // 5. 按题目去重（因为一个题目可能对应多个知识点），取最大权重
                    WITH q, max(weight) as final_weight, head(collect(reason)) as final_reason

                    // 6. 排序：优先推荐权重高的，同权重下随机排序
                    RETURN q.id AS id, q.content AS content, q.type AS type, q.difficulty AS difficulty, final_reason AS reason
                    ORDER BY final_weight DESC, rand()
                    LIMIT $limit
                """;

        Collection<Map<String, Object>> rawQuestions = neo4jClient.query(cypherQuery)
                .bind(studentId).to("studentId")
                .bind(questionCount).to("limit")
                .fetch()
                .all();

        List<Map<String, Object>> questions = new ArrayList<>(rawQuestions);

        // 计算题型分布
        Map<String, Integer> typeDistribution = questions.stream()
                .collect(Collectors.groupingBy(
                        q -> (String) q.get("type"),
                        Collectors.summingInt(q -> 1)));

        // 计算难度分布
        Map<String, Integer> difficultyDistribution = questions.stream()
                .collect(Collectors.groupingBy(
                        q -> (String) q.get("difficulty"),
                        Collectors.summingInt(q -> 1)));

        SmartExamResult result = new SmartExamResult();
        result.setQuestions(questions);
        result.setQuestionTypeDistribution(typeDistribution);
        result.setDifficultyDistribution(difficultyDistribution);
        result.setTotalQuestions(questions.size());

        return result;
    }
}
