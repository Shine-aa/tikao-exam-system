package com.example.manger.repository;

import com.example.manger.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生考试记录Repository
 */
@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {
    
    /**
     * 根据考试ID和学生ID查询考试记录
     */
    Optional<StudentExam> findByExamIdAndStudentIdAndIsActiveTrue(Long examId, Long studentId);
    
    /**
     * 根据考试ID查询所有学生考试记录
     */
    List<StudentExam> findByExamIdAndIsActiveTrueOrderByCreatedAtDesc(Long examId);
    
    /**
     * 根据考试ID查询所有学生考试记录（不分页）
     */
    List<StudentExam> findByExamIdAndIsActiveTrue(Long examId);
    
    /**
     * 根据学生ID查询考试记录
     */
    List<StudentExam> findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(Long studentId);
    
    /**
     * 根据考试ID统计参与学生数量
     */
    long countByExamIdAndIsActiveTrue(Long examId);
    
    /**
     * 根据考试ID统计已提交的学生数量
     */
    long countByExamIdAndStatusAndIsActiveTrue(Long examId, StudentExam.StudentExamStatus status);
    
    /**
     * 根据考试ID和状态列表统计学生数量
     */
    long countByExamIdAndStatusInAndIsActiveTrue(Long examId, List<StudentExam.StudentExamStatus> statuses);
    
    /**
     * 查询学生在指定考试中的尝试次数
     */
    @Query("SELECT MAX(se.attemptNumber) FROM StudentExam se WHERE se.examId = :examId AND se.studentId = :studentId AND se.isActive = true")
    Integer findMaxAttemptNumberByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);
    
    /**
     * 查询考试中学生的最高分
     */
    @Query("SELECT MAX(se.totalScore) FROM StudentExam se WHERE se.examId = :examId AND se.studentId = :studentId AND se.isActive = true")
    Double findMaxScoreByExamIdAndStudentId(@Param("examId") Long examId, @Param("studentId") Long studentId);
}
