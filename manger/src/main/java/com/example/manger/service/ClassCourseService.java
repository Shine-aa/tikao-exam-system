package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.ClassCourseRequest;
import com.example.manger.dto.ClassCourseResponse;
import com.example.manger.entity.ClassCourse;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.ClassCourseRepository;
import com.example.manger.repository.ClassRepository;
import com.example.manger.repository.CourseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 班级课程关联管理服务
 */
@Service
@Transactional
public class ClassCourseService {
    
    @Autowired
    private ClassCourseRepository classCourseRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    /**
     * 创建班级课程关联
     */
    public ClassCourseResponse createClassCourse(ClassCourseRequest request) {
        // 检查班级是否存在
        if (!classRepository.existsById(request.getClassId())) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在");
        }
        
        // 检查课程是否存在
        if (!courseRepository.existsById(request.getCourseId())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在");
        }
        
        // 检查是否已存在活跃的关联
        if (classCourseRepository.existsByClassIdAndCourseIdAndIsActiveTrue(request.getClassId(), request.getCourseId())) {
            throw new BusinessException(ErrorCode.CLASS_COURSE_EXISTS, "班级课程关联已存在");
        }
        
        // 检查是否存在被软删除的关联，如果存在则重新激活
        Optional<ClassCourse> existingClassCourse = classCourseRepository.findByClassIdAndCourseId(request.getClassId(), request.getCourseId());
        if (existingClassCourse.isPresent()) {
            ClassCourse classCourse = existingClassCourse.get();
            classCourse.setIsActive(true);
            classCourse.setSemester(request.getSemester());
            classCourse.setAcademicYear(request.getAcademicYear());
            classCourse.setCreatedAt(LocalDateTime.now());
            
            ClassCourse savedClassCourse = classCourseRepository.save(classCourse);
            return convertToResponse(savedClassCourse);
        }
        
        // 创建新的关联
        ClassCourse classCourse = new ClassCourse();
        BeanUtils.copyProperties(request, classCourse);
        classCourse.setIsActive(true);
        classCourse.setCreatedAt(LocalDateTime.now());
        
        ClassCourse savedClassCourse = classCourseRepository.save(classCourse);
        return convertToResponse(savedClassCourse);
    }
    
    /**
     * 更新班级课程关联
     */
    public ClassCourseResponse updateClassCourse(Long id, ClassCourseRequest request) {
        ClassCourse classCourse = classCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_COURSE_NOT_FOUND, "班级课程关联不存在"));
        
        // 检查班级是否存在
        if (!classRepository.existsById(request.getClassId())) {
            throw new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在");
        }
        
        // 检查课程是否存在
        if (!courseRepository.existsById(request.getCourseId())) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在");
        }
        
        // 检查关联是否已存在（排除当前记录）
        if (!classCourse.getClassId().equals(request.getClassId()) || 
            !classCourse.getCourseId().equals(request.getCourseId())) {
            if (classCourseRepository.existsByClassIdAndCourseIdAndIsActiveTrue(request.getClassId(), request.getCourseId())) {
                throw new BusinessException(ErrorCode.CLASS_COURSE_EXISTS, "班级课程关联已存在");
            }
        }
        
        BeanUtils.copyProperties(request, classCourse);
        
        ClassCourse savedClassCourse = classCourseRepository.save(classCourse);
        return convertToResponse(savedClassCourse);
    }
    
    /**
     * 删除班级课程关联
     */
    public void deleteClassCourse(Long id) {
        ClassCourse classCourse = classCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_COURSE_NOT_FOUND, "班级课程关联不存在"));
        
        classCourse.setIsActive(false);
        classCourseRepository.save(classCourse);
    }
    
    /**
     * 批量删除班级课程关联
     */
    public void batchDeleteClassCourses(List<Long> ids) {
        List<ClassCourse> classCourses = classCourseRepository.findAllById(ids);
        for (ClassCourse classCourse : classCourses) {
            classCourse.setIsActive(false);
        }
        classCourseRepository.saveAll(classCourses);
    }
    
    /**
     * 根据班级ID删除所有关联
     */
    public void deleteByClassId(Long classId) {
        classCourseRepository.deleteByClassId(classId);
    }
    
    /**
     * 根据课程ID删除所有关联
     */
    public void deleteByCourseId(Long courseId) {
        classCourseRepository.deleteByCourseId(courseId);
    }
    
    /**
     * 根据ID获取班级课程关联
     */
    @Transactional(readOnly = true)
    public ClassCourseResponse getClassCourseById(Long id) {
        ClassCourse classCourse = classCourseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_COURSE_NOT_FOUND, "班级课程关联不存在"));
        return convertToResponse(classCourse);
    }
    
    /**
     * 根据班级ID获取所有关联的课程
     */
    @Transactional(readOnly = true)
    public List<ClassCourseResponse> getClassCoursesByClassId(Long classId) {
        List<ClassCourse> classCourses = classCourseRepository.findByClassIdAndIsActiveTrue(classId);
        return classCourses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据课程ID获取所有关联的班级
     */
    @Transactional(readOnly = true)
    public List<ClassCourseResponse> getClassCoursesByCourseId(Long courseId) {
        List<ClassCourse> classCourses = classCourseRepository.findByCourseIdAndIsActiveTrue(courseId);
        return classCourses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取班级课程关联
     */
    @Transactional(readOnly = true)
    public PageResponse<ClassCourseResponse> getClassCoursesWithPagination(int page, int size, 
                                                                          Long classId, Long courseId,
                                                                          String semester, String academicYear) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<ClassCourse> classCoursePage = classCourseRepository.findByFilters(
                classId, courseId, semester, academicYear, pageable);
        
        List<ClassCourseResponse> responses = classCoursePage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<ClassCourseResponse>builder()
                .content(responses)
                .total(classCoursePage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(classCoursePage.getTotalPages())
                .first(page == 1)
                .last(page == classCoursePage.getTotalPages())
                .hasNext(page < classCoursePage.getTotalPages())
                .hasPrevious(page > 1)
                .build();
    }
    
    /**
     * 获取所有课程列表（用于添加课程时选择）
     */
    @Transactional(readOnly = true)
    public List<ClassCourseResponse> getAllCourses() {
        // 这里应该返回所有可用的课程，而不是班级课程关联
        // 我们需要从课程表中获取所有课程
        return courseRepository.findByIsActiveTrue().stream()
                .map(course -> {
                    ClassCourseResponse response = new ClassCourseResponse();
                    response.setCourseId(course.getId());
                    response.setCourseName(course.getCourseName());
                    response.setCourseCode(course.getCourseCode());
                    response.setCredits(course.getCredits());
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应对象
     */
    private ClassCourseResponse convertToResponse(ClassCourse classCourse) {
        ClassCourseResponse response = new ClassCourseResponse();
        BeanUtils.copyProperties(classCourse, response);
        
        // 设置班级信息
        classRepository.findById(classCourse.getClassId()).ifPresent(classEntity -> {
            response.setClassName(classEntity.getClassName());
            response.setClassCode(classEntity.getClassCode());
        });
        
        // 设置课程信息
        courseRepository.findById(classCourse.getCourseId()).ifPresent(course -> {
            response.setCourseName(course.getCourseName());
            response.setCourseCode(course.getCourseCode());
            response.setCredits(course.getCredits());
        });
        
        return response;
    }
}
