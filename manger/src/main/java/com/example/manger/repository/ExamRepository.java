package com.example.manger.repository;

import com.example.manger.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 考试Repository
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long>, JpaSpecificationExecutor<Exam> {
    
    /**
     * 根据教师ID分页查询考试
     */
    Page<Exam> findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(Long teacherId, Pageable pageable);
    
    /**
     * 根据班级ID查询考试
     */
    List<Exam> findByClassIdAndIsActiveTrueOrderByCreatedAtDesc(Long classId);
    
    /**
     * 根据考试代码查询
     */
    Optional<Exam> findByExamCodeAndIsActiveTrue(String examCode);
    
    /**
     * 根据状态查询考试
     */
    List<Exam> findByStatusAndIsActiveTrueOrderByStartTimeAsc(Exam.ExamStatus status);
    
    /**
     * 查询即将开始的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 'SCHEDULED' AND e.startTime <= :currentTime AND e.isActive = true")
    List<Exam> findExamsToStart(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询已结束的考试
     */
    @Query("SELECT e FROM Exam e WHERE e.status = 'ONGOING' AND e.endTime <= :currentTime AND e.isActive = true")
    List<Exam> findExamsToEnd(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 根据关键词搜索考试
     */
    @Query("SELECT e FROM Exam e WHERE e.teacherId = :teacherId AND e.isActive = true " +
           "AND (e.examName LIKE %:keyword% OR e.examCode LIKE %:keyword%) " +
           "ORDER BY e.createdAt DESC")
    Page<Exam> findByKeyword(@Param("teacherId") Long teacherId, @Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 统计教师创建的考试数量
     */
    long countByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 统计班级的考试数量
     */
    long countByClassIdAndIsActiveTrue(Long classId);
    
    /**
     * 根据班级ID列表查询考试
     */
    List<Exam> findByClassIdInAndIsActiveTrueOrderByStartTimeDesc(List<Long> classIds);
    
    /**
     * 根据状态统计考试数量
     */
    long countByStatusAndIsActiveTrue(Exam.ExamStatus status);
    
    /**
     * 根据教师ID查询考试
     */
    List<Exam> findByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 根据教师ID查询考试（按创建时间降序）
     */
    List<Exam> findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(Long teacherId);
}
