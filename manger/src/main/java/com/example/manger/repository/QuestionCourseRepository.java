package com.example.manger.repository;

import com.example.manger.entity.QuestionCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 题目课程关联Repository
 */
@Repository
public interface QuestionCourseRepository extends JpaRepository<QuestionCourse, Long> {

    //根据课程id和题目id删除关联
    void deleteByQuestionIdAndCourseId(Long questionId, Long courseId);

    // 按课程ID列表查询关联的题目（只取活跃的）
    List<QuestionCourse> findByCourseIdInAndIsActiveTrue(List<Long> courseIds);
    
    /**
     * 根据题目ID查找关联的课程
     */
    @Query("SELECT qc.courseId FROM QuestionCourse qc " +
           "WHERE qc.questionId = :questionId " +
           "AND qc.isActive = true")
    Long findByQuestionIdAndIsActiveTrue(Long questionId);
    
    /**
     * 根据课程ID查找所有关联的题目
     */
    List<QuestionCourse> findByCourseIdAndIsActiveTrue(Long courseId);
    
    /**
     * 检查题目和课程是否已关联
     */
    boolean existsByQuestionIdAndCourseIdAndIsActiveTrue(Long questionId, Long courseId);
    
    /**
     * 根据题目ID和课程ID查找关联
     */
    QuestionCourse findByQuestionIdAndCourseId(Long questionId, Long courseId);
    
    /**
     * 根据题目ID删除所有关联
     */
    void deleteByQuestionId(Long questionId);
    
    /**
     * 根据课程ID删除所有关联
     */
    void deleteByCourseId(Long courseId);
    
    /**
     * 分页查找题目课程关联
     */
    @Query("SELECT qc FROM QuestionCourse qc " +
           "WHERE qc.isActive = true " +
           "AND (:questionId IS NULL OR qc.questionId = :questionId) " +
           "AND (:courseId IS NULL OR qc.courseId = :courseId)")
    Page<QuestionCourse> findByFilters(@Param("questionId") Long questionId,
                                       @Param("courseId") Long courseId,
                                       Pageable pageable);
    
    /**
     * 根据题目ID统计课程数量
     */
    @Query("SELECT COUNT(qc) FROM QuestionCourse qc WHERE qc.questionId = :questionId AND qc.isActive = true")
    long countByQuestionId(@Param("questionId") Long questionId);
    
    /**
     * 根据课程ID统计题目数量
     */
    @Query("SELECT COUNT(qc) FROM QuestionCourse qc WHERE qc.courseId = :courseId AND qc.isActive = true")
    long countByCourseId(@Param("courseId") Long courseId);
}

