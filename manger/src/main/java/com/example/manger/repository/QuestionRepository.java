package com.example.manger.repository;

import com.example.manger.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Page<Question> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isActive = true AND " +
           "(q.title LIKE %:keyword% OR q.content LIKE %:keyword% OR q.tags LIKE %:keyword%)")
    Page<Question> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isActive = true AND q.type = :type")
    Page<Question> findByType(@Param("type") Question.QuestionType type, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isActive = true AND q.difficulty = :difficulty")
    Page<Question> findByDifficulty(@Param("difficulty") Question.DifficultyLevel difficulty, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.isActive = true AND q.knowledgePointId = :knowledgePointId")
    Page<Question> findByKnowledgePointId(@Param("knowledgePointId") Long knowledgePointId, Pageable pageable);
    
    @Query("SELECT q FROM Question q " +
           "WHERE q.isActive = true AND q.createdBy = :createdBy")
    Page<Question> findByCreatedBy(@Param("createdBy") Long createdBy, Pageable pageable);
    
    @Query("SELECT q FROM Question q " +
           "WHERE q.isActive = true AND " +
           "(:type IS NULL OR q.type = :type) AND " +
           "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
           "(:knowledgePointId IS NULL OR q.knowledgePointId = :knowledgePointId) AND " +
           "(:keyword IS NULL OR q.title LIKE %:keyword% OR q.content LIKE %:keyword% OR q.tags LIKE %:keyword%) AND " +
           "(:createdBy IS NULL OR q.createdBy = :createdBy)")
    Page<Question> findByFilters(@Param("type") Question.QuestionType type,
                                @Param("difficulty") Question.DifficultyLevel difficulty,
                                @Param("knowledgePointId") Long knowledgePointId,
                                @Param("keyword") String keyword,
                                @Param("createdBy") Long createdBy,
                                Pageable pageable);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.isActive = true AND q.type = :type")
    long countByType(@Param("type") Question.QuestionType type);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.isActive = true AND q.difficulty = :difficulty")
    long countByDifficulty(@Param("difficulty") Question.DifficultyLevel difficulty);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.isActive = true AND q.createdBy = :createdBy")
    long countByCreatedBy(@Param("createdBy") Long createdBy);
    
    /**
     * 根据题型和课程ID查询题目（通过关联表）
     */
    @Query("SELECT q FROM Question q JOIN QuestionCourse qc ON q.id = qc.questionId WHERE q.isActive = true AND q.type = :type AND qc.courseId = :courseId AND qc.isActive = true")
    List<Question> findByTypeAndCourseIdAndIsActiveTrue(@Param("type") Question.QuestionType type, @Param("courseId") Long courseId);
    
    /**
     * 根据课程ID查询题目（通过关联表）
     */
    @Query("SELECT q FROM Question q JOIN QuestionCourse qc ON q.id = qc.questionId WHERE q.isActive = true AND qc.courseId = :courseId AND qc.isActive = true")
    List<Question> findByCourseIdAndIsActiveTrue(@Param("courseId") Long courseId);
    
    /**
     * 根据课程ID和难度查询题目（通过关联表）
     */
    @Query("SELECT q FROM Question q JOIN QuestionCourse qc ON q.id = qc.questionId WHERE q.isActive = true AND qc.courseId = :courseId AND q.difficulty = :difficulty AND qc.isActive = true")
    List<Question> findByCourseIdAndDifficultyAndIsActiveTrue(@Param("courseId") Long courseId, @Param("difficulty") Question.DifficultyLevel difficulty);
    
    /**
     * 根据课程ID、题型和难度查询题目（通过关联表）
     */
    @Query("SELECT q FROM Question q JOIN QuestionCourse qc ON q.id = qc.questionId WHERE q.isActive = true AND qc.courseId = :courseId AND q.type = :type AND q.difficulty = :difficulty AND qc.isActive = true")
    List<Question> findByCourseIdAndTypeAndDifficultyAndIsActiveTrue(@Param("courseId") Long courseId, @Param("type") Question.QuestionType type, @Param("difficulty") Question.DifficultyLevel difficulty);
}
