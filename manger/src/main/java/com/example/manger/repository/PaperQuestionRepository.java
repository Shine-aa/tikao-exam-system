package com.example.manger.repository;

import com.example.manger.entity.PaperQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷题目关联数据访问层
 */
@Repository
public interface PaperQuestionRepository extends JpaRepository<PaperQuestion, Long> {
    
    /**
     * 根据试卷ID查询题目列表（按顺序）
     */
    List<PaperQuestion> findByPaperIdOrderByQuestionOrder(Long paperId);
    
    /**
     * 根据试卷ID删除所有题目
     */
    void deleteByPaperId(Long paperId);
    
    /**
     * 根据试卷ID统计题目数量
     */
    long countByPaperId(Long paperId);
    
    /**
     * 根据试卷ID和题目ID查询
     */
    @Query("SELECT pq FROM PaperQuestion pq WHERE pq.paperId = :paperId AND pq.questionId = :questionId")
    List<PaperQuestion> findByPaperIdAndQuestionId(@Param("paperId") Long paperId, @Param("questionId") Long questionId);
    
    /**
     * 根据试卷ID查询题目ID列表
     */
    @Query("SELECT pq.questionId FROM PaperQuestion pq WHERE pq.paperId = :paperId ORDER BY pq.questionOrder")
    List<Long> findQuestionIdsByPaperId(@Param("paperId") Long paperId);
}
