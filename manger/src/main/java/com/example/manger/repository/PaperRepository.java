package com.example.manger.repository;

import com.example.manger.entity.Paper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 试卷数据访问层
 */
@Repository
public interface PaperRepository extends JpaRepository<Paper, Long> {
    
    /**
     * 根据教师ID分页查询试卷
     */
    Page<Paper> findByTeacherIdAndIsActiveTrue(Long teacherId, Pageable pageable);
    
    /**
     * 根据班级ID查询试卷
     */
    List<Paper> findByClassIdAndIsActiveTrue(Long classId);
    
    /**
     * 根据课程ID查询试卷
     */
    List<Paper> findByCourseIdAndIsActiveTrue(Long courseId);
    
    /**
     * 根据试卷代码查询
     */
    Optional<Paper> findByPaperCodeAndIsActiveTrue(String paperCode);
    
    /**
     * 根据教师ID和关键词分页查询
     */
    @Query("SELECT p FROM Paper p WHERE p.teacherId = :teacherId AND " +
           "(p.paperName LIKE %:keyword% OR p.paperCode LIKE %:keyword%) AND p.isActive = true")
    Page<Paper> findByTeacherIdAndKeyword(@Param("teacherId") Long teacherId, 
                                         @Param("keyword") String keyword, 
                                         Pageable pageable);
    
    /**
     * 根据班级ID和课程ID查询试卷
     */
    List<Paper> findByClassIdAndCourseIdAndIsActiveTrue(Long classId, Long courseId);
    
    /**
     * 统计教师创建的试卷数量
     */
    long countByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 分页查询所有活跃的试卷
     */
    Page<Paper> findByIsActiveTrue(Pageable pageable);
    
    /**
     * 根据关键词分页查询试卷
     */
    @Query("SELECT p FROM Paper p WHERE p.isActive = true AND " +
           "(p.paperName LIKE %:keyword% OR p.paperCode LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Paper> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
