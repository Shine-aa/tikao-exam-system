package com.example.manger.repository;

import com.example.manger.entity.QuestionKnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目知识点关联Repository
 */
@Repository
public interface QuestionKnowledgePointRepository extends JpaRepository<QuestionKnowledgePoint, Long> {
    
    /**
     * 根据题目ID查找所有关联的知识点
     */
    List<QuestionKnowledgePoint> findByQuestionIdAndIsActiveTrue(Long questionId);
    
    /**
     * 根据知识点ID查找所有关联的题目
     */
    List<QuestionKnowledgePoint> findByKnowledgePointIdAndIsActiveTrue(Long knowledgePointId);
    
    /**
     * 检查题目和知识点是否已关联
     */
    boolean existsByQuestionIdAndKnowledgePointIdAndIsActiveTrue(Long questionId, Long knowledgePointId);
    
    /**
     * 根据题目ID删除所有关联
     */
    void deleteByQuestionId(Long questionId);
    
    /**
     * 根据知识点ID删除所有关联
     */
    void deleteByKnowledgePointId(Long knowledgePointId);
    
    /**
     * 根据题目ID统计知识点数量
     */
    @Query("SELECT COUNT(qkp) FROM QuestionKnowledgePoint qkp WHERE qkp.questionId = :questionId AND qkp.isActive = true")
    long countByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 根据知识点ID统计题目数量
     */
    @Query("SELECT COUNT(qkp) FROM QuestionKnowledgePoint qkp WHERE qkp.knowledgePointId = :knowledgePointId AND qkp.isActive = true")
    long countByKnowledgePointId(@Param("knowledgePointId") Long knowledgePointId);
    
    /**
     * 根据题目ID列表查找知识点（用于批量查询）
     */
    @Query("SELECT qkp FROM QuestionKnowledgePoint qkp WHERE qkp.questionId IN :questionIds AND qkp.isActive = true")
    List<QuestionKnowledgePoint> findByQuestionIdIn(@Param("questionIds") List<Long> questionIds);
}
