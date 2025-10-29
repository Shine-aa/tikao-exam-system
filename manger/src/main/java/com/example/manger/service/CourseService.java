package com.example.manger.service;

import com.example.manger.dto.CourseRequest;
import com.example.manger.dto.CourseResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.entity.Course;
import com.example.manger.entity.User;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.CourseRepository;
import com.example.manger.repository.ClassRepository;
import com.example.manger.repository.ClassCourseRepository;
import com.example.manger.repository.UserRepository;
import com.example.manger.repository.TeacherCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程服务类
 */
@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final ClassRepository classRepository;
    private final ClassCourseRepository classCourseRepository;
    private final UserRepository userRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    
    /**
     * 创建课程
     */
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        // 检查课程代码是否已存在
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new BusinessException(ErrorCode.COURSE_CODE_EXISTS, "课程代码已存在");
        }
        
        // 检查教师是否存在
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "教师不存在"));
        
        // 创建课程
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setTeacherId(request.getTeacherId());
        course.setSemester(request.getSemester());
        course.setAcademicYear(request.getAcademicYear());
        course.setIsActive(request.getIsActive());
        
        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }
    
    /**
     * 更新课程
     */
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在"));
        
        // 检查课程代码是否被其他课程使用
        if (!course.getCourseCode().equals(request.getCourseCode()) && 
            courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new BusinessException(ErrorCode.COURSE_CODE_EXISTS, "课程代码已存在");
        }
        
        // 检查教师是否存在
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "教师不存在"));
        
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setTeacherId(request.getTeacherId());
        course.setSemester(request.getSemester());
        course.setAcademicYear(request.getAcademicYear());
        course.setIsActive(request.getIsActive());
        
        Course savedCourse = courseRepository.save(course);
        return convertToResponse(savedCourse);
    }
    
    /**
     * 删除课程
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在"));
        
        // 检查是否有班级课程关联使用此课程
        // 这里需要检查ClassCourseRepository，暂时跳过检查
        // long classCount = classCourseRepository.countByCourseId(id);
        // if (classCount > 0) {
        //     throw new BusinessException(ErrorCode.COURSE_HAS_CLASSES, "课程下存在班级，无法删除");
        // }
        
        course.setIsActive(false);
        courseRepository.save(course);
    }
    
    /**
     * 根据ID获取课程
     */
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在"));
        return convertToResponse(course);
    }
    
    /**
     * 分页获取教师的课程（通过teacher_courses关联表）
     */
    public PageResponse<CourseResponse> getCoursesWithPagination(Long teacherId, int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        
        // 先获取教师的课程ID列表
        List<Long> teacherCourseIds = teacherCourseRepository.findByTeacherIdAndIsActiveTrue(teacherId)
            .stream()
            .map(tc -> tc.getCourseId())
            .collect(Collectors.toList());
        
        if (teacherCourseIds.isEmpty()) {
            return PageResponse.<CourseResponse>builder()
                    .content(List.of())
                    .total(0L)
                    .page(page)
                    .size(size)
                    .totalPages(0)
                    .first(true)
                    .last(true)
                    .hasNext(false)
                    .hasPrevious(false)
                    .build();
        }
        
        // 根据课程ID列表和关键词查询
        List<Course> courses;
        if (keyword != null && !keyword.trim().isEmpty()) {
            courses = courseRepository.findByIdInAndKeyword(teacherCourseIds, keyword.trim());
        } else {
            courses = courseRepository.findByIdInAndIsActiveTrue(teacherCourseIds);
        }
        
        // 手动分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, courses.size());
        List<CourseResponse> responses = courses.subList(start, end).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<CourseResponse>builder()
                .content(responses)
                .total((long) courses.size())
                .page(page)
                .size(size)
                .totalPages((int) Math.ceil((double) courses.size() / size))
                .first(page == 1)
                .last(end >= courses.size())
                .hasNext(end < courses.size())
                .hasPrevious(page > 1)
                .build();
    }
    
    /**
     * 获取教师的所有课程
     */
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        List<Course> courses = courseRepository.findByTeacherIdAndIsActiveTrue(teacherId);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * 转换为响应对象
     */
    private CourseResponse convertToResponse(Course course) {
        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setCourseName(course.getCourseName());
        response.setDescription(course.getDescription());
        response.setTeacherId(course.getTeacherId());
        response.setSemester(course.getSemester());
        response.setAcademicYear(course.getAcademicYear());
        response.setIsActive(course.getIsActive());
        response.setCreatedAt(course.getCreatedAt());
        response.setUpdatedAt(course.getUpdatedAt());
        
        // 获取教师姓名
        User teacher = userRepository.findById(course.getTeacherId()).orElse(null);
        if (teacher != null) {
            response.setTeacherName(teacher.getUsername());
        }
        
        // 获取班级数量 - 通过班级课程关联表统计
        long classCount = classCourseRepository.countByCourseId(course.getId());
        response.setClassCount(classCount);
        
        return response;
    }
}
