package com.example.manger.service;

import com.example.manger.dto.ClassRequest;
import com.example.manger.dto.ClassResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.entity.Class;
import com.example.manger.entity.Major;
import com.example.manger.entity.User;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.ClassRepository;
import com.example.manger.repository.MajorRepository;
import com.example.manger.repository.UserRepository;
import com.example.manger.repository.StudentClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 班级服务类
 */
@Service
@RequiredArgsConstructor
public class ClassService {
    
    private final ClassRepository classRepository;
    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final StudentClassRepository studentClassRepository;
    
    /**
     * 创建班级
     */
    @Transactional
    public ClassResponse createClass(ClassRequest request) {
        // 检查班级代码是否已存在
        if (classRepository.existsByClassCode(request.getClassCode())) {
            throw new BusinessException(ErrorCode.CLASS_CODE_EXISTS, "班级代码已存在");
        }
        
        // 检查专业是否存在
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND, "专业不存在"));
        
        // 检查教师是否存在
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "教师不存在"));
        
        Class classEntity = new Class();
        BeanUtils.copyProperties(request, classEntity);
        classEntity.setIsActive(true);
        classEntity.setCurrentStudents(0);
        classEntity.setCreatedAt(LocalDateTime.now());
        classEntity.setUpdatedAt(LocalDateTime.now());
        
        Class savedClass = classRepository.save(classEntity);
        return convertToResponse(savedClass);
    }
    
    /**
     * 更新班级
     */
    @Transactional
    public ClassResponse updateClass(Long id, ClassRequest request) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        // 检查班级代码是否被其他班级使用
        if (!classEntity.getClassCode().equals(request.getClassCode()) && 
            classRepository.existsByClassCode(request.getClassCode())) {
            throw new BusinessException(ErrorCode.CLASS_CODE_EXISTS, "班级代码已存在");
        }
        
        // 检查专业是否存在
        Major major = majorRepository.findById(request.getMajorId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MAJOR_NOT_FOUND, "专业不存在"));
        
        // 检查教师是否存在
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "教师不存在"));
        
        BeanUtils.copyProperties(request, classEntity);
        classEntity.setUpdatedAt(LocalDateTime.now());
        
        Class savedClass = classRepository.save(classEntity);
        return convertToResponse(savedClass);
    }
    
    /**
     * 删除班级
     */
    @Transactional
    public void deleteClass(Long id) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        classEntity.setIsActive(false);
        classEntity.setUpdatedAt(LocalDateTime.now());
        classRepository.save(classEntity);
    }
    
    /**
     * 批量删除班级
     */
    @Transactional
    public void batchDeleteClasses(List<Long> ids) {
        List<Class> classes = classRepository.findAllById(ids);
        
        for (Class classEntity : classes) {
            classEntity.setIsActive(false);
            classEntity.setUpdatedAt(LocalDateTime.now());
        }
        
        classRepository.saveAll(classes);
    }
    
    /**
     * 根据ID获取班级
     */
    @Transactional(readOnly = true)
    public ClassResponse getClassById(Long id) {
        Class classEntity = classRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        return convertToResponse(classEntity);
    }
    
    /**
     * 获取所有班级
     */
    @Transactional(readOnly = true)
    public List<ClassResponse> getAllClasses() {
        List<Class> classes = classRepository.findByIsActiveTrue();
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据教师ID获取班级
     */
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByTeacherId(Long teacherId) {
        List<Class> classes = classRepository.findByTeacherIdAndIsActiveTrue(teacherId);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据专业ID获取班级
     */
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByMajorId(Long majorId) {
        List<Class> classes = classRepository.findByMajorIdAndIsActiveTrue(majorId);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据年级获取班级
     */
    @Transactional(readOnly = true)
    public List<ClassResponse> getClassesByGrade(String grade) {
        List<Class> classes = classRepository.findByGradeAndIsActiveTrue(grade);
        return classes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 分页获取班级
     */
    @Transactional(readOnly = true)
    public PageResponse<ClassResponse> getClassesWithPagination(int page, int size, 
                                                               Long teacherId, Long majorId, 
                                                               String grade, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Class> classPage;
        if (teacherId != null) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                classPage = classRepository.findByTeacherIdAndKeyword(teacherId, keyword.trim(), pageable);
            } else {
                classPage = classRepository.findByTeacherIdAndIsActiveTrue(teacherId, pageable);
            }
        } else if (majorId != null && grade != null) {
            classPage = classRepository.findByMajorIdAndGradeAndIsActiveTrue(majorId, grade, pageable);
        } else if (majorId != null) {
            classPage = classRepository.findByMajorIdAndIsActiveTrue(majorId, pageable);
        } else if (grade != null) {
            classPage = classRepository.findByGradeAndIsActiveTrue(grade, pageable);
        } else {
            classPage = classRepository.findByIsActiveTrue(pageable);
        }
        
        List<ClassResponse> responses = classPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<ClassResponse>builder()
                .content(responses)
                .total(classPage.getTotalElements())
                .page(page)
                .size(size)
                .totalPages(classPage.getTotalPages())
                .first(page == 1)
                .last(page == classPage.getTotalPages())
                .hasNext(page < classPage.getTotalPages())
                .hasPrevious(page > 1)
                .build();
    }
    
    /**
     * 添加学生到班级
     */
    @Transactional
    public void addStudentToClass(Long classId, Long studentId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "学生不存在"));
        
        // 检查班级是否已满
        if (classEntity.getCurrentStudents() >= classEntity.getMaxStudents()) {
            throw new BusinessException(ErrorCode.CLASS_FULL, "班级已满");
        }
        
        // 检查学生是否已在班级中
        if (studentClassRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new BusinessException(ErrorCode.STUDENT_ALREADY_IN_CLASS, "学生已在班级中");
        }
        
        // 这里需要创建StudentClass实体，暂时跳过
        // StudentClass studentClass = new StudentClass();
        // studentClass.setClassId(classId);
        // studentClass.setStudentId(studentId);
        // studentClassRepository.save(studentClass);
        
        // 更新班级学生数量
        classEntity.setCurrentStudents(classEntity.getCurrentStudents() + 1);
        classRepository.save(classEntity);
    }
    
    /**
     * 从班级移除学生
     */
    @Transactional
    public void removeStudentFromClass(Long classId, Long studentId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        // 检查学生是否在班级中
        if (!studentClassRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_IN_CLASS, "学生不在班级中");
        }
        
        // 删除学生班级关联
        studentClassRepository.deleteByClassIdAndStudentId(classId, studentId);
        
        // 更新班级学生数量
        classEntity.setCurrentStudents(Math.max(0, classEntity.getCurrentStudents() - 1));
        classRepository.save(classEntity);
    }
    
    /**
     * 获取班级学生列表
     */
    @Transactional(readOnly = true)
    public List<User> getClassStudents(Long classId) {
        // 这里需要实现获取班级学生的逻辑
        // 暂时返回空列表
        return List.of();
    }
    
    /**
     * 转换为响应对象
     */
    private ClassResponse convertToResponse(Class classEntity) {
        ClassResponse response = new ClassResponse();
        BeanUtils.copyProperties(classEntity, response);
        
        // 设置专业名称
        if (classEntity.getMajorId() != null) {
            majorRepository.findById(classEntity.getMajorId())
                    .ifPresent(major -> response.setMajorName(major.getMajorName()));
        }
        
        // 设置教师名称
        if (classEntity.getTeacherId() != null) {
            userRepository.findById(classEntity.getTeacherId())
                    .ifPresent(teacher -> response.setTeacherName(teacher.getUsername()));
        }
        
        // 优先使用数据库中的currentStudents字段，如果为null或0则动态计算
        if (classEntity.getCurrentStudents() == null || classEntity.getCurrentStudents() == 0) {
            long currentStudentCount = studentClassRepository.countByClassIdAndIsActiveTrue(classEntity.getId());
            response.setCurrentStudents((int) currentStudentCount);
            
            // 同时更新数据库中的字段以保持同步
            if (currentStudentCount != classEntity.getCurrentStudents()) {
                classEntity.setCurrentStudents((int) currentStudentCount);
                classRepository.save(classEntity);
            }
        } else {
            response.setCurrentStudents(classEntity.getCurrentStudents());
        }
        
        return response;
    }
}