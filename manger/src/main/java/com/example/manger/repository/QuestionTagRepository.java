package com.example.manger.repository;

import com.example.manger.entity.QuestionTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
    
    Page<QuestionTag> findByIsActiveTrue(Pageable pageable);
    
    @Query("SELECT qt FROM QuestionTag qt WHERE qt.isActive = true AND " +
           "(qt.name LIKE %:keyword% OR qt.description LIKE %:keyword%)")
    Page<QuestionTag> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    boolean existsByNameAndIdNot(String name, Long id);
    
    boolean existsByName(String name);
}
