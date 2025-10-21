package com.example.manger.repository;

import com.example.manger.entity.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    
    List<QuestionAnswer> findByQuestionIdOrderBySortOrder(Long questionId);
    
    @Query("SELECT qa FROM QuestionAnswer qa WHERE qa.questionId = :questionId AND qa.isPrimary = true")
    List<QuestionAnswer> findPrimaryAnswersByQuestionId(@Param("questionId") Long questionId);
    
    void deleteByQuestionId(Long questionId);
    
    List<QuestionAnswer> findByQuestionIdIn(List<Long> questionIds);
}
