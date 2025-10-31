package com.example.manger.repository;

import com.example.manger.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问层
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * 根据课程代码查找课程
     */
    Optional<Course> findByCourseCode(String courseCode);
    
    /**
     * 查找所有启用的课程
     */
    List<Course> findByIsActiveTrue();
    
    /**
     * 检查课程代码是否存在
     */
    boolean existsByCourseCode(String courseCode);
    
    /**
     * 根据教师ID查找课程
     */
    List<Course> findByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 根据教师ID分页查找课程
     */
    Page<Course> findByTeacherIdAndIsActiveTrue(Long teacherId, Pageable pageable);
    
    /**
     * 根据学期查找课程
     */
    List<Course> findBySemesterAndIsActiveTrue(String semester);
    
    /**
     * 根据学年查找课程
     */
    List<Course> findByAcademicYearAndIsActiveTrue(String academicYear);
    
    /**
     * 根据教师ID和学期查找课程
     */
    List<Course> findByTeacherIdAndSemesterAndIsActiveTrue(Long teacherId, String semester);
    
    /**
     * 统计教师课程数量
     */
    long countByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 根据课程名称模糊查询
     */
    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:keyword% AND c.isActive = true")
    List<Course> findByCourseNameContaining(@Param("keyword") String keyword);
    
    /**
     * 根据教师ID和关键词分页查询
     */
    @Query("SELECT c FROM Course c WHERE c.teacherId = :teacherId AND " +
           "(c.courseName LIKE %:keyword% OR c.courseCode LIKE %:keyword%) AND c.isActive = true")
    Page<Course> findByTeacherIdAndKeyword(@Param("teacherId") Long teacherId, 
                                          @Param("keyword") String keyword, 
                                          Pageable pageable);
    
    /**
     * 根据课程ID列表查询启用的课程
     */
    List<Course> findByIdInAndIsActiveTrue(List<Long> courseIds);
    
    /**
     * 根据课程ID列表和关键词查询课程
     */
    @Query("SELECT c FROM Course c WHERE c.id IN :courseIds AND " +
           "(c.courseName LIKE %:keyword% OR c.courseCode LIKE %:keyword%) AND c.isActive = true")
    List<Course> findByIdInAndKeyword(@Param("courseIds") List<Long> courseIds, 
                                      @Param("keyword") String keyword);
}
