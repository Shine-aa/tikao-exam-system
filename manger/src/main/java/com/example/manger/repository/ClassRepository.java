package com.example.manger.repository;

import com.example.manger.entity.Class;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 班级数据访问层
 */
@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    
    /**
     * 根据班级代码查找班级
     */
    Optional<Class> findByClassCode(String classCode);
    
    /**
     * 检查班级代码是否存在
     */
    boolean existsByClassCode(String classCode);
    
    /**
     * 根据教师ID查找班级
     */
    List<Class> findByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 根据教师ID分页查找班级
     */
    Page<Class> findByTeacherIdAndIsActiveTrue(Long teacherId, Pageable pageable);
    
    /**
     * 根据专业ID查找班级
     */
    List<Class> findByMajorIdAndIsActiveTrue(Long majorId);
    
    /**
     * 根据专业ID分页查找班级
     */
    Page<Class> findByMajorIdAndIsActiveTrue(Long majorId, Pageable pageable);
    
    /**
     * 根据年级查找班级
     */
    List<Class> findByGradeAndIsActiveTrue(String grade);
    
    /**
     * 根据年级分页查找班级
     */
    Page<Class> findByGradeAndIsActiveTrue(String grade, Pageable pageable);
    
    /**
     * 根据专业ID和年级查找班级
     */
    List<Class> findByMajorIdAndGradeAndIsActiveTrue(Long majorId, String grade);
    
    /**
     * 根据专业ID和年级分页查找班级
     */
    Page<Class> findByMajorIdAndGradeAndIsActiveTrue(Long majorId, String grade, Pageable pageable);
    
    /**
     * 统计教师班级数量
     */
    long countByTeacherIdAndIsActiveTrue(Long teacherId);
    
    /**
     * 统计专业下的班级数量
     */
    long countByMajorIdAndIsActiveTrue(Long majorId);
    
    /**
     * 统计年级下的班级数量
     */
    long countByGradeAndIsActiveTrue(String grade);
    
    /**
     * 查找所有启用的班级
     */
    List<Class> findByIsActiveTrue();
    
    /**
     * 分页查找所有启用的班级
     */
    Page<Class> findByIsActiveTrue(Pageable pageable);
    
    /**
     * 根据班级名称模糊查询
     */
    @Query("SELECT c FROM Class c WHERE c.className LIKE %:keyword% AND c.isActive = true")
    List<Class> findByClassNameContaining(@Param("keyword") String keyword);
    
    /**
     * 根据教师ID和关键词分页查询
     */
    @Query("SELECT c FROM Class c WHERE c.teacherId = :teacherId AND " +
           "(c.className LIKE %:keyword% OR c.classCode LIKE %:keyword%) AND c.isActive = true")
    Page<Class> findByTeacherIdAndKeyword(@Param("teacherId") Long teacherId, 
                                         @Param("keyword") String keyword, 
                                         Pageable pageable);
    
    /**
     * 更新班级学生数量
     */
    @Modifying
    @Query("UPDATE Class c SET c.currentStudents = :count WHERE c.id = :classId")
    void updateCurrentStudents(@Param("classId") Long classId, @Param("count") Integer count);
}
