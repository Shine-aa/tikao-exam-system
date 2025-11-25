package com.example.manger.repository;

import com.example.manger.entity.KnowledgePoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {
    
    List<KnowledgePoint> findByParentIdIsNullAndIsActiveTrueOrderBySortOrder();
    
    List<KnowledgePoint> findByParentIdAndIsActiveTrueOrderBySortOrder(Long parentId);
    
    Page<KnowledgePoint> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT kp FROM KnowledgePoint kp WHERE kp.isActive = true AND " +
           "(kp.name LIKE %:keyword% OR kp.description LIKE %:keyword%)")
    Page<KnowledgePoint> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    boolean existsByName(String name);
}
