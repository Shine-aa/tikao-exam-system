package com.example.manger.service;

import com.example.manger.dto.ExamResponse;
import com.example.manger.entity.Exam;
import com.example.manger.entity.Paper;
import com.example.manger.entity.StudentExam;
import com.example.manger.entity.StudentClass;
import com.example.manger.entity.User;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.*;
import com.example.manger.entity.StudentAnswer;
import com.example.manger.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生端考试服务
 */
@Service
public class StudentExamService {

    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private StudentExamRepository studentExamRepository;
    
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentClassRepository studentClassRepository;
    
    @Autowired
    private PaperRepository paperRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private PaperQuestionRepository paperQuestionRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取学生的考试列表（按状态分类）
     */
    public Map<String, Object> getStudentExamsByStatus(HttpServletRequest request) {
        // 获取当前学生ID
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") 
            ? authorizationHeader.substring(7) 
            : authorizationHeader;
        Long studentId = jwtUtil.getUserIdFromToken(token);

        // 获取学生所在的班级
        List<StudentClass> studentClasses = studentClassRepository.findByStudentIdAndIsActiveTrue(studentId);
        if (studentClasses.isEmpty()) {
            return Map.of(
                "ongoing", new ArrayList<>(),
                "upcoming", new ArrayList<>(),
                "completed", new ArrayList<>()
            );
        }

        List<Long> classIds = studentClasses.stream()
                .map(StudentClass::getClassId)
                .collect(Collectors.toList());

        // 获取学生相关的考试
        List<Exam> exams = examRepository.findByClassIdInAndIsActiveTrueOrderByStartTimeDesc(classIds);
        
        // 获取学生的考试记录
        List<StudentExam> studentExams = studentExamRepository.findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId);
        Map<Long, StudentExam> studentExamMap = studentExams.stream()
                .collect(Collectors.toMap(StudentExam::getExamId, se -> se, (existing, replacement) -> existing));

        // 按状态分类
        List<ExamResponse> ongoing = new ArrayList<>();
        List<ExamResponse> upcoming = new ArrayList<>();
        List<ExamResponse> completed = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();

        for (Exam exam : exams) {
            ExamResponse examResponse = convertToResponse(exam);
            StudentExam studentExam = studentExamMap.get(exam.getId());
            
            if (studentExam != null && studentExam.getTotalScore() != null) {
                examResponse.setTotalScore(studentExam.getTotalScore().doubleValue());
            }

            // 根据考试状态和学生考试状态分类
            if (exam.getStatus() == Exam.ExamStatus.ONGOING) {
                if (studentExam != null && studentExam.getStatus() == StudentExam.StudentExamStatus.IN_PROGRESS) {
                    ongoing.add(examResponse);
                } else if (studentExam != null && studentExam.getStatus() == StudentExam.StudentExamStatus.SUBMITTED) {
                    completed.add(examResponse);
                } else if (exam.getStartTime().isBefore(now) && exam.getEndTime().isAfter(now)) {
                    ongoing.add(examResponse);
                }
            } else if (exam.getStatus() == Exam.ExamStatus.SCHEDULED) {
                if (studentExam != null && studentExam.getStatus() == StudentExam.StudentExamStatus.SUBMITTED) {
                    completed.add(examResponse);
                } else {
                    upcoming.add(examResponse);
                }
            } else if (exam.getStatus() == Exam.ExamStatus.COMPLETED) {
                completed.add(examResponse);
            }
        }

        return Map.of(
            "ongoing", ongoing,
            "upcoming", upcoming,
            "completed", completed
        );
    }

    /**
     * 获取学生考试详情
     */
    public ExamResponse getStudentExamDetail(Long examId, HttpServletRequest request) {
        // 获取当前学生ID
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") 
            ? authorizationHeader.substring(7) 
            : authorizationHeader;
        Long studentId = jwtUtil.getUserIdFromToken(token);

        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXAM_NOT_FOUND, "考试不存在"));

        // 验证学生是否有权限访问此考试
        List<StudentClass> studentClasses = studentClassRepository.findByStudentIdAndIsActiveTrue(studentId);
        boolean hasAccess = studentClasses.stream()
                .anyMatch(sc -> sc.getClassId().equals(exam.getClassId()));

        if (!hasAccess) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "您没有权限访问此考试");
        }

        ExamResponse response = convertToResponse(exam);
        
        // 查询学生考试状态
        Optional<StudentExam> studentExamOpt = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId);
        if (studentExamOpt.isPresent()) {
            StudentExam studentExam = studentExamOpt.get();
            response.setStudentExamStatus(studentExam.getStatus().name());
            response.setTotalScore(studentExam.getTotalScore() != null ? studentExam.getTotalScore().doubleValue() : null);
        } else {
            response.setStudentExamStatus("NOT_STARTED");
        }

        return response;
    }

    /**
     * 开始考试
     */
    @Transactional
    public void startStudentExam(Long examId, HttpServletRequest request) {
        // 获取当前学生ID
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") 
            ? authorizationHeader.substring(7) 
            : authorizationHeader;
        Long studentId = jwtUtil.getUserIdFromToken(token);

        // 验证考试是否存在且状态正确
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXAM_NOT_FOUND, "考试不存在"));

        // 允许SCHEDULED和ONGOING状态的考试开始
        if (exam.getStatus() != Exam.ExamStatus.SCHEDULED && exam.getStatus() != Exam.ExamStatus.ONGOING) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "考试未开始或已结束");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime()) || now.isAfter(exam.getEndTime())) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "不在考试时间内");
        }

        // 获取或创建学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_EXAM_NOT_FOUND, "学生考试记录不存在"));

        // 如果考试已提交，不允许重新开始
        if (studentExam.getStatus() == StudentExam.StudentExamStatus.SUBMITTED) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "考试已提交，无法重新开始");
        }

        // 如果考试已评分，不允许重新开始
        if (studentExam.getStatus() == StudentExam.StudentExamStatus.GRADED) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "考试已评分，无法重新开始");
        }

        // 如果考试正在进行中，允许重新进入（处理网络中断、浏览器崩溃等情况）
        if (studentExam.getStatus() == StudentExam.StudentExamStatus.IN_PROGRESS) {
            // 更新开始时间（重新进入考试）
            studentExam.setStartTime(now);
            studentExamRepository.save(studentExam);
            return; // 直接返回，不重复设置状态
        }

        // 如果考试状态是SCHEDULED，自动更新为ONGOING
        if (exam.getStatus() == Exam.ExamStatus.SCHEDULED) {
            exam.setStatus(Exam.ExamStatus.ONGOING);
            examRepository.save(exam);
        }

        // 更新学生考试状态
        studentExam.setStatus(StudentExam.StudentExamStatus.IN_PROGRESS);
        studentExam.setStartTime(now);
        studentExamRepository.save(studentExam);

        // 更新考试的实考人数
        updateParticipatedCount(examId);
    }

    /**
     * 提交考试答案
     */
    @Transactional
    public void submitStudentExam(Long examId, Map<String, Object> requestData, HttpServletRequest request) {
        // 获取当前学生ID
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") 
            ? authorizationHeader.substring(7) 
            : authorizationHeader;
        Long studentId = jwtUtil.getUserIdFromToken(token);

        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXAM_NOT_FOUND, "考试不存在"));

        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_EXAM_NOT_FOUND, "学生考试记录不存在"));

        if (studentExam.getStatus() != StudentExam.StudentExamStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.INVALID_OPERATION, "考试未在进行中");
        }

        // 更新学生考试状态
        studentExam.setStatus(StudentExam.StudentExamStatus.SUBMITTED);
        studentExam.setSubmitTime(LocalDateTime.now());
        
        // 计算考试用时
        if (studentExam.getStartTime() != null) {
            long minutes = java.time.Duration.between(studentExam.getStartTime(), studentExam.getSubmitTime()).toMinutes();
            studentExam.setTimeSpentMinutes((int) minutes);
        }

        studentExamRepository.save(studentExam);

        // 保存学生答案
        saveStudentAnswers(studentExam.getId(), requestData);

        // 这里可以添加自动评分的逻辑
        // TODO: 实现自动评分功能
    }

    /**
     * 保存学生答案（整张试卷）
     */
    private void saveStudentAnswers(Long studentExamId, Map<String, Object> requestData) {
        @SuppressWarnings("unchecked")
        Map<String, Object> answers = (Map<String, Object>) requestData.get("answers");
        
        if (answers == null || answers.isEmpty()) {
            return;
        }

        // 删除之前的答案记录
        studentAnswerRepository.deleteByStudentExamId(studentExamId);

        // 创建新的答案记录（整张试卷）
        StudentAnswer studentAnswer = new StudentAnswer();
        studentAnswer.setStudentExamId(studentExamId);
        studentAnswer.setAnswerContent(answers);  // 直接存储整个答案Map
        studentAnswer.setIsGraded(false);
        
        System.out.println("Saving student answers for studentExamId: " + studentExamId + ", answers: " + answers);
        studentAnswerRepository.save(studentAnswer);
        System.out.println("Student answers saved successfully");
    }

    /**
     * 获取学生考试统计
     */
    public Map<String, Object> getStudentExamStats(HttpServletRequest request) {
        // 获取当前学生ID
        String authorizationHeader = request.getHeader("Authorization");
        String token = authorizationHeader != null && authorizationHeader.startsWith("Bearer ") 
            ? authorizationHeader.substring(7) 
            : authorizationHeader;
        Long studentId = jwtUtil.getUserIdFromToken(token);

        // 获取学生考试记录
        List<StudentExam> studentExams = studentExamRepository.findByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(studentId);
        
        // 计算统计信息
        int totalExams = studentExams.size();
        int completedExams = (int) studentExams.stream()
                .filter(se -> se.getStatus() == StudentExam.StudentExamStatus.SUBMITTED)
                .count();
        
        double avgScore = studentExams.stream()
                .filter(se -> se.getTotalScore() != null)
                .mapToDouble(se -> se.getTotalScore().doubleValue())
                .average()
                .orElse(0.0);

        // 计算学习天数（这里简化处理）
        int studyDays = 30; // 可以根据实际需求计算

        // 计算班级排名（这里简化处理）
        int rank = 1; // 可以根据实际需求计算

        return Map.of(
            "totalExams", totalExams,
            "avgScore", Math.round(avgScore),
            "studyDays", studyDays,
            "rank", rank
        );
    }

    /**
     * 更新实考人数
     */
    private void updateParticipatedCount(Long examId) {
        Exam exam = examRepository.findById(examId).orElse(null);
        if (exam != null) {
            long participatedCount = studentExamRepository.countByExamIdAndStatusInAndIsActiveTrue(
                examId, 
                Arrays.asList(
                    StudentExam.StudentExamStatus.IN_PROGRESS,
                    StudentExam.StudentExamStatus.SUBMITTED,
                    StudentExam.StudentExamStatus.GRADED
                )
            );
            exam.setParticipatedCount((int) participatedCount);
            examRepository.save(exam);
        }
    }

    /**
     * 转换为响应对象
     */
    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        BeanUtils.copyProperties(exam, response);
        
        // 手动设置状态字段，确保枚举值正确转换
        if (exam.getStatus() != null) {
            response.setStatus(exam.getStatus().name());
        }
        
        // 设置试卷信息
        Paper paper = paperRepository.findById(exam.getPaperId()).orElse(null);
        if (paper != null) {
            response.setPaperName(paper.getPaperName());
            response.setPaperCode(paper.getPaperCode());
            response.setTotalQuestions(paper.getTotalQuestions());
            response.setTotalPoints(paper.getTotalPoints());
        }
        
        // 设置班级信息
        com.example.manger.entity.Class classEntity = classRepository.findById(exam.getClassId()).orElse(null);
        if (classEntity != null) {
            response.setClassName(classEntity.getClassName());
            response.setClassCode(classEntity.getClassCode());
        }
        
        // 设置教师信息
        User teacher = userRepository.findById(exam.getTeacherId()).orElse(null);
        if (teacher != null) {
            response.setTeacherName(teacher.getUsername());
        }
        
        // 直接使用数据库中的字段
        response.setStudentCount(exam.getStudentCount());
        response.setParticipatedCount(exam.getParticipatedCount());
        
        return response;
    }
}
