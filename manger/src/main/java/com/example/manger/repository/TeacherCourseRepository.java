package com.example.manger.repository;

import com.example.manger.entity.TeacherCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 教师课程关联Repository
 */
@Repository
public interface TeacherCourseRepository extends JpaRepository<TeacherCourse, Long> {
    
    /**
     * 根据教师ID查找所有关联的课程
     */
    List<TeacherCourse> findByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 根据课程ID查找所有关联的教师
     */
    List<TeacherCourse> findByCourseIdAndIsActiveTrue(Long courseId);
    
    /**
     * 检查教师和课程是否已关联
     */
    boolean existsByTeacherIdAndCourseIdAndIsActiveTrue(Long teacherId, Long courseId);
    
    /**
     * 根据教师ID和课程ID查找关联
     */
    Optional<TeacherCourse> findByTeacherIdAndCourseId(Long teacherId, Long courseId);
    
    /**
     * 根据教师ID删除所有关联
     */
    void deleteByTeacherId(Long teacherId);
    
    /**
     * 根据课程ID删除所有关联
     */
    void deleteByCourseId(Long courseId);
    
    /**
     * 根据教师ID统计课程数量
     */
    @Query("SELECT COUNT(tc) FROM TeacherCourse tc WHERE tc.teacherId = :teacherId AND tc.isActive = true")
    long countByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * 根据课程ID统计教师数量
     */
    @Query("SELECT COUNT(tc) FROM TeacherCourse tc WHERE tc.courseId = :courseId AND tc.isActive = true")
    long countByCourseId(@Param("courseId") Long courseId);
    
    /**
     * 根据课程ID列表查找教师（用于权限控制）
     */
    @Query("SELECT tc.teacherId FROM TeacherCourse tc WHERE tc.courseId IN :courseIds AND tc.isActive = true")
    List<Long> findTeacherIdsByCourseIds(@Param("courseIds") List<Long> courseIds);
}

