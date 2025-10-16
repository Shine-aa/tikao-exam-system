package com.example.manger.repository;

import com.example.manger.entity.Major;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 专业Repository
 */
@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    
    /**
     * 根据专业代码查找专业
     */
    Optional<Major> findByMajorCode(String majorCode);
    
    /**
     * 检查专业代码是否存在
     */
    boolean existsByMajorCode(String majorCode);
    
    /**
     * 查找所有启用的专业
     */
    List<Major> findByIsActiveTrue();
    
    /**
     * 分页查找所有启用的专业
     */
    Page<Major> findByIsActiveTrue(Pageable pageable);
    
    /**
     * 根据专业名称模糊查询
     */
    @Query("SELECT m FROM Major m WHERE m.isActive = true AND m.majorName LIKE %:name%")
    Page<Major> findByMajorNameContaining(@Param("name") String name, Pageable pageable);
    
    /**
     * 根据专业代码模糊查询
     */
    @Query("SELECT m FROM Major m WHERE m.isActive = true AND m.majorCode LIKE %:code%")
    Page<Major> findByMajorCodeContaining(@Param("code") String code, Pageable pageable);
}
