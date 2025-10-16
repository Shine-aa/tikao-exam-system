package com.example.manger.repository;

import com.example.manger.entity.StudentClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学生班级关联数据访问层
 */
@Repository
public interface StudentClassRepository extends JpaRepository<StudentClass, Long> {
    
    /**
     * 根据学生ID查找班级关联
     */
    List<StudentClass> findByStudentIdAndIsActiveTrue(Long studentId);
    
    /**
     * 根据班级ID查找学生关联
     */
    List<StudentClass> findByClassIdAndIsActiveTrue(Long classId);
    
    /**
     * 根据学生ID和班级ID查找关联
     */
    Optional<StudentClass> findByStudentIdAndClassId(Long studentId, Long classId);
    
    /**
     * 检查学生是否已在班级中
     */
    boolean existsByStudentIdAndClassIdAndIsActiveTrue(Long studentId, Long classId);
    
    /**
     * 检查班级和学生是否已关联
     */
    boolean existsByClassIdAndStudentId(Long classId, Long studentId);
    
    /**
     * 根据班级ID和学生ID删除关联
     */
    void deleteByClassIdAndStudentId(Long classId, Long studentId);
    
    /**
     * 统计班级学生数量
     */
    long countByClassIdAndIsActiveTrue(Long classId);
    
    /**
     * 统计学生加入的班级数量
     */
    long countByStudentIdAndIsActiveTrue(Long studentId);
    
    /**
     * 软删除学生班级关联
     */
    @Modifying
    @Query("UPDATE StudentClass sc SET sc.isActive = false WHERE sc.studentId = :studentId AND sc.classId = :classId")
    void removeStudentFromClass(@Param("studentId") Long studentId, @Param("classId") Long classId);
    
    /**
     * 根据班级ID删除所有学生关联
     */
    @Modifying
    @Query("UPDATE StudentClass sc SET sc.isActive = false WHERE sc.classId = :classId")
    void removeAllStudentsFromClass(@Param("classId") Long classId);
    
    /**
     * 根据学生ID删除所有班级关联
     */
    @Modifying
    @Query("UPDATE StudentClass sc SET sc.isActive = false WHERE sc.studentId = :studentId")
    void removeStudentFromAllClasses(@Param("studentId") Long studentId);
}
