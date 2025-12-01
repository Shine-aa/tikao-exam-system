package com.example.manger.service;

import com.example.manger.dto.*;
import com.example.manger.entity.Course;
import com.example.manger.entity.TeacherCourse;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        User creator = userRepository.findById(request.getTeacherId())
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

        // 保存课程，获取保存后的课程ID（必须先保存课程，才能获取ID用于关联）
        Course savedCourse = courseRepository.save(course);

        // 创建课程-教师关联：自动将创建者添加到课程的教师列表
        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTeacherId(creator.getId()); // 创建者的教师ID
        teacherCourse.setCourseId(savedCourse.getId()); // 刚创建的课程ID
        teacherCourse.setIsActive(true); // 默认为激活状态
        // createdAt和updatedAt会由@CreationTimestamp和@UpdateTimestamp自动填充
        // 保存关联关系
        teacherCourseRepository.save(teacherCourse);

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
     * 获取课程的教师授权信息（包含已授权教师和所有教师）
     */
    public CourseTeacherAuthDTO getCourseTeacherAuthInfo(Long courseId) {
        // 1. 验证课程是否存在
        if (!courseRepository.existsById(courseId)) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在");
        }

        // 2. 获取系统中所有教师（角色为TEACHER且激活的用户）
        List<User> allTeachers = userRepository.findByRolesRoleCodeAndIsActiveTrue("TEACHER");
        List<TeacherResponse> allTeacherResponses = allTeachers.stream()
                .map(this::convertToTeacherResponse)
                .collect(Collectors.toList());

        // 3. 获取当前课程已授权的教师ID列表
        List<Long> authorizedTeacherIds = teacherCourseRepository.findByCourseIdAndIsActiveTrue(courseId)
                .stream()
                .map(TeacherCourse::getTeacherId)
                .collect(Collectors.toList());

        // 4. 过滤出已授权的教师
        List<TeacherResponse> authorizedTeachers = allTeachers.stream()
                .filter(teacher -> authorizedTeacherIds.contains(teacher.getId()))
                .map(this::convertToTeacherResponse)
                .collect(Collectors.toList());

        // 5. 包装结果返回
        return new CourseTeacherAuthDTO(authorizedTeachers, allTeacherResponses);
    }

    /**
     * 授权教师到课程（全量更新：新增需要的，移除不需要的）
     */
    @Transactional
    public void authorizeTeachers(Long courseId, List<Long> newTeacherIds) {
        // 1. 验证课程是否存在
        if (!courseRepository.existsById(courseId)) {
            throw new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在");
        }

        // 2. 验证传入的教师是否都存在（过滤无效ID，避免后续处理异常）
        List<Long> existingTeacherIds = userRepository.findAllById(newTeacherIds).stream()
                .map(User::getId)
                .collect(Collectors.toList());
        if (existingTeacherIds.size() != newTeacherIds.size()) {
            // 计算不存在的ID，便于日志提示
            List<Long> invalidIds = newTeacherIds.stream()
                    .filter(id -> !existingTeacherIds.contains(id))
                    .collect(Collectors.toList());
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "部分教师不存在：" + invalidIds);
        }

        // 3. 获取当前课程已授权的教师关联列表（仅激活状态）
        List<TeacherCourse> currentAuthorized = teacherCourseRepository.findByCourseIdAndIsActiveTrue(courseId);
        List<Long> currentTeacherIds = currentAuthorized.stream()
                .map(TeacherCourse::getTeacherId)
                .collect(Collectors.toList());

        // 4. 计算需要新增的教师ID（新列表有，当前没有的）
        List<Long> toAdd = existingTeacherIds.stream()
                .filter(id -> !currentTeacherIds.contains(id))
                .collect(Collectors.toList());

        // 5. 计算需要移除的教师关联（当前有，新列表没有的）
        List<TeacherCourse> toRemove = currentAuthorized.stream()
                .filter(tc -> !existingTeacherIds.contains(tc.getTeacherId()))
                .collect(Collectors.toList());

        // 6. 执行新增：创建新的关联记录
        for (Long teacherId : toAdd) {
            TeacherCourse newRelation = new TeacherCourse();
            newRelation.setTeacherId(teacherId);
            newRelation.setCourseId(courseId);
            newRelation.setIsActive(true);
            // createdAt/updatedAt由注解自动填充，无需手动设置
            teacherCourseRepository.save(newRelation);
        }

        // 7. 执行移除：将关联记录置为非激活（逻辑删除，保留历史）
        // 若需要物理删除，可改为 teacherCourseRepository.deleteAll(toRemove);
        for (TeacherCourse relation : toRemove) {
            relation.setIsActive(false);
            relation.setUpdatedAt(LocalDateTime.now()); // 手动更新时间戳（可选）
            teacherCourseRepository.save(relation);
        }
    }
    /**
     * 转换用户为教师响应DTO
     */
    private TeacherResponse convertToTeacherResponse(User user) {
        TeacherResponse response = new TeacherResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setName(user.getName()); // 假设用户表有姓名字段
        response.setEmail(user.getEmail()); // 假设用户表有邮箱字段
        // 可补充其他需要的教师信息字段
        return response;
    }


    /**
     * 获取教师的所有课程
     */
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        // 获取教师的课程ID列表
        List<Long> teacherCourseIds = teacherCourseRepository.findByTeacherIdAndIsActiveTrue(teacherId)
                .stream()
                .map(TeacherCourse::getCourseId) // 假设实体类是TeacherCourse
                .collect(Collectors.toList());

        // 批量查询课程（处理Optional并过滤无效课程）
        List<Course> courses = teacherCourseIds.stream()
                .map(courseId -> courseRepository.findById(courseId).orElse(null))
                .filter(Objects::nonNull) // 排除不存在的课程
                .collect(Collectors.toList());

        // 转换为响应对象
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
