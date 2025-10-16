package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 班级实体类（重新设计：按专业班级）
 */
@Entity
@Table(name = "classes")
@Data
public class Class {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_code", unique = true, nullable = false, length = 50)
    private String classCode;
    
    @Column(name = "class_name", nullable = false, length = 100)
    private String className;
    
    @Column(name = "grade", nullable = false, length = 10)
    private String grade;
    
    @Column(name = "major_id", nullable = false)
    private Long majorId;
    
    @Column(name = "class_number", nullable = false)
    private Integer classNumber;
    
    @Column(name = "teacher_id", nullable = false)
    private Long teacherId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "max_students")
    private Integer maxStudents = 50;
    
    @Column(name = "current_students")
    private Integer currentStudents = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
