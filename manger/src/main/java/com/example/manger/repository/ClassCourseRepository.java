package com.example.manger.repository;

import com.example.manger.entity.ClassCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级课程关联Repository
 */
@Repository
public interface ClassCourseRepository extends JpaRepository<ClassCourse, Long> {
    // 1. 根据班级ID列表查询有效的班级-课程关联
    List<ClassCourse> findByClassIdInAndIsActiveTrue(List<Long> classIds);

    // 2. 根据班级ID列表 + 课程ID列表查询有效的班级-课程关联
    // 根据班级ID列表 + 课程ID查询有效班级-课程关联
    List<ClassCourse> findByClassIdInAndCourseIdAndIsActiveTrue(List<Long> classIds, Long courseId);

    
    /**
     * 根据班级ID查找所有关联的课程
     */
    List<ClassCourse> findByClassIdAndIsActiveTrue(Long classId);
    
    /**
     * 根据课程ID查找所有关联的班级
     */
    List<ClassCourse> findByCourseIdAndIsActiveTrue(Long courseId);
    
    /**
     * 检查班级和课程是否已关联
     */
    boolean existsByClassIdAndCourseIdAndIsActiveTrue(Long classId, Long courseId);
    
    /**
     * 根据班级ID和课程ID查找关联
     */
    Optional<ClassCourse> findByClassIdAndCourseId(Long classId, Long courseId);
    
    /**
     * 根据班级ID删除所有关联
     */
    void deleteByClassId(Long classId);
    
    /**
     * 根据课程ID删除所有关联
     */
    void deleteByCourseId(Long courseId);
    
    /**
     * 分页查找班级课程关联
     */
    @Query("SELECT cc FROM ClassCourse cc " +
           "WHERE cc.isActive = true " +
           "AND (:classId IS NULL OR cc.classId = :classId) " +
           "AND (:courseId IS NULL OR cc.courseId = :courseId) " +
           "AND (:semester IS NULL OR cc.semester = :semester) " +
           "AND (:academicYear IS NULL OR cc.academicYear = :academicYear)")
    Page<ClassCourse> findByFilters(@Param("classId") Long classId,
                                   @Param("courseId") Long courseId,
                                   @Param("semester") String semester,
                                   @Param("academicYear") String academicYear,
                                   Pageable pageable);
    
    /**
     * 根据班级ID统计课程数量
     */
    @Query("SELECT COUNT(cc) FROM ClassCourse cc WHERE cc.classId = :classId AND cc.isActive = true")
    long countByClassId(@Param("classId") Long classId);
    
    /**
     * 根据课程ID统计班级数量
     */
    @Query("SELECT COUNT(cc) FROM ClassCourse cc WHERE cc.courseId = :courseId AND cc.isActive = true")
    long countByCourseId(@Param("courseId") Long courseId);
}
