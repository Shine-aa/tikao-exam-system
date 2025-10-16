package com.example.manger.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 班级课程关联实体类
 */
@Entity
@Table(name = "class_courses")
@Data
public class ClassCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "class_id", nullable = false)
    private Long classId;
    
    @Column(name = "course_id", nullable = false)
    private Long courseId;
    
    @Column(name = "semester", length = 20)
    private String semester;
    
    @Column(name = "academic_year", length = 10)
    private String academicYear;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
