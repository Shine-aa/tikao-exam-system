package com.example.manger.repository;

import com.example.manger.entity.GradingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 判卷结果数据访问层
 */
@Repository
public interface GradingResultRepository extends JpaRepository<GradingResult, Long> {
    
    /**
     * 根据考试ID和学生ID查找判卷结果
     */
    Optional<GradingResult> findByExamIdAndStudentId(Long examId, Long studentId);
    
    /**
     * 根据学生考试记录ID查找判卷结果
     */
    Optional<GradingResult> findByStudentExamId(Long studentExamId);
    
    /**
     * 根据考试ID查找所有判卷结果
     */
    List<GradingResult> findByExamIdOrderByStudentName(Long examId);
    
    /**
     * 根据考试ID统计判卷完成数量
     */
    long countByExamIdAndIsGradingCompletedTrue(Long examId);
    
    /**
     * 根据考试ID统计总判卷数量
     */
    long countByExamId(Long examId);
    
    /**
     * 根据判卷人ID查找判卷结果
     */
    List<GradingResult> findByGradedByOrderByGradedAtDesc(Long gradedBy);
    
    /**
     * 查找未完成的判卷结果
     */
    List<GradingResult> findByIsGradingCompletedFalseOrderByCreatedAtAsc();
    
    /**
     * 根据考试ID和学生ID列表查找判卷结果
     */
    @Query("SELECT gr FROM GradingResult gr WHERE gr.examId = :examId AND gr.studentId IN :studentIds")
    List<GradingResult> findByExamIdAndStudentIdIn(@Param("examId") Long examId, @Param("studentIds") List<Long> studentIds);
}