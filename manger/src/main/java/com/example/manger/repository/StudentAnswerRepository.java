package com.example.manger.repository;

import com.example.manger.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生答题记录Repository（整张试卷）
 */
@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    
    /**
     * 根据学生考试记录ID查找答题记录（应该只有一条）
     */
    Optional<StudentAnswer> findByStudentExamId(Long studentExamId);
    
    /**
     * 根据学生考试记录ID列表查找答题记录
     */
    List<StudentAnswer> findByStudentExamIdIn(List<Long> studentExamIds);
    
    /**
     * 删除学生考试记录的答题记录
     */
    void deleteByStudentExamId(Long studentExamId);
    
    /**
     * 查找已评分的答题记录
     */
    List<StudentAnswer> findByIsGradedTrue();
    
    /**
     * 查找未评分的答题记录
     */
    List<StudentAnswer> findByIsGradedFalse();
    
    /**
     * 根据评分人查找答题记录
     */
    List<StudentAnswer> findByGradedBy(Long gradedBy);
    
    /**
     * 统计学生考试记录的总得分
     */
    @Query("SELECT COALESCE(SUM(sa.totalScore), 0) FROM StudentAnswer sa WHERE sa.studentExamId = :studentExamId")
    Double sumTotalScoreByStudentExamId(@Param("studentExamId") Long studentExamId);
    
    /**
     * 统计已评分的答题记录数量
     */
    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.isGraded = true")
    long countGradedAnswers();
    
    /**
     * 统计未评分的答题记录数量
     */
    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.isGraded = false")
    long countUngradedAnswers();
}
