package com.example.manger.service;

import com.example.manger.dto.ExamRequest;
import com.example.manger.dto.ExamResponse;
import com.example.manger.dto.PageResponse;
import com.example.manger.entity.*;
import com.example.manger.repository.*;
import com.example.manger.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 考试服务类
 */
@Service
@Transactional
public class ExamService {
    
    @Autowired
    private ExamRepository examRepository;
    
    @Autowired
    private StudentExamRepository studentExamRepository;
    
    @Autowired
    private PaperRepository paperRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentClassRepository studentClassRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private StudentAnswerRepository studentAnswerRepository;
    
    @Autowired
    private GradingResultRepository gradingResultRepository;
    
    @Autowired
    private StudentExamPaperService studentExamPaperService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 创建考试
     */
    public ExamResponse createExam(ExamRequest request, HttpServletRequest httpRequest) {
        // 获取当前用户ID
        Long teacherId = getCurrentUserId(httpRequest);
        
        // 验证试卷是否存在
        Paper paper = paperRepository.findById(request.getPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        
        // 验证班级是否存在
        com.example.manger.entity.Class classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("班级不存在"));
        
        // 验证时间设置
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new RuntimeException("开始时间不能晚于结束时间");
        }
        
        // 允许5分钟的容错时间
        if (request.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        
        // 创建考试
        Exam exam = new Exam();
        exam.setExamCode(generateExamCode());
        exam.setExamName(request.getExamName());
        exam.setDescription(request.getDescription());
        exam.setPaperId(request.getPaperId());
        exam.setClassId(request.getClassId());
        exam.setTeacherId(teacherId);
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setMaxAttempts(request.getMaxAttempts());
        exam.setIsRandomOrder(request.getIsRandomOrder());
        exam.setIsRandomOptions(request.getIsRandomOptions());
        exam.setAllowReview(request.getAllowReview());
        exam.setStatus(Exam.ExamStatus.SCHEDULED);
        
        // 计算应考人数（班级学生总数）
        long studentCount = studentClassRepository.countByClassIdAndIsActiveTrue(request.getClassId());
        exam.setStudentCount((int) studentCount);
        exam.setParticipatedCount(0); // 初始实考人数为0
        
        Exam savedExam = examRepository.save(exam);
        
        // 为班级中的所有学生创建考试记录
        createStudentExamRecords(savedExam.getId(), request.getClassId());
        
        return convertToResponse(savedExam);
    }
    
    /**
     * 为班级学生创建考试记录
     */
    private void createStudentExamRecords(Long examId, Long classId) {
        List<StudentClass> studentClasses = studentClassRepository.findByClassIdAndIsActiveTrue(classId);
        
        for (StudentClass studentClass : studentClasses) {
            StudentExam studentExam = new StudentExam();
            studentExam.setExamId(examId);
            studentExam.setStudentId(studentClass.getStudentId());
            studentExam.setAttemptNumber(1);
            studentExam.setStatus(StudentExam.StudentExamStatus.NOT_STARTED);
            studentExamRepository.save(studentExam);
        }
    }
    
    /**
     * 更新实考人数
     */
    @Transactional
    public void updateParticipatedCount(Long examId) {
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
     * 分页查询考试列表
     */
    public PageResponse<ExamResponse> getExamsWithPagination(int page, int size, String keyword, HttpServletRequest httpRequest) {
        Long teacherId = getCurrentUserId(httpRequest);
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Exam> examPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            examPage = examRepository.findByKeyword(teacherId, keyword.trim(), pageable);
        } else {
            examPage = examRepository.findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(teacherId, pageable);
        }
        
        List<ExamResponse> examResponses = examPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(examResponses, page, size, examPage.getTotalElements());
    }
    
    /**
     * 根据ID获取考试详情
     */
    public ExamResponse getExamById(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        return convertToResponse(exam);
    }
    
    /**
     * 删除考试
     */
    public void deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 软删除考试
        exam.setIsActive(false);
        examRepository.save(exam);
        
        // 软删除相关的学生考试记录
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(examId);
        for (StudentExam studentExam : studentExams) {
            studentExam.setIsActive(false);
            studentExamRepository.save(studentExam);
        }
    }
    
    /**
     * 开始考试
     */
    public void startExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        if (exam.getStatus() != Exam.ExamStatus.SCHEDULED) {
            throw new RuntimeException("只有已安排的考试才能开始");
        }
        
        if (LocalDateTime.now().isBefore(exam.getStartTime())) {
            throw new RuntimeException("考试尚未到开始时间");
        }
        
        exam.setStatus(Exam.ExamStatus.ONGOING);
        examRepository.save(exam);
    }
    
    /**
     * 结束考试
     */
    public void endExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        exam.setStatus(Exam.ExamStatus.COMPLETED);
        examRepository.save(exam);
        
        // 处理未提交的学生考试记录：自动标记为已提交
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(examId);
        LocalDateTime now = LocalDateTime.now();
        
        for (StudentExam studentExam : studentExams) {
            // 只处理进行中的考试记录
            if (studentExam.getStatus() == StudentExam.StudentExamStatus.IN_PROGRESS) {
                studentExam.setStatus(StudentExam.StudentExamStatus.SUBMITTED);
                // 如果还没有提交时间，设置为当前时间或考试结束时间
                if (studentExam.getSubmitTime() == null) {
                    studentExam.setSubmitTime(now.isAfter(exam.getEndTime()) ? exam.getEndTime() : now);
                }
                
                // 计算考试用时
                if (studentExam.getStartTime() != null && studentExam.getSubmitTime() != null) {
                    long minutes = java.time.Duration.between(studentExam.getStartTime(), studentExam.getSubmitTime()).toMinutes();
                    studentExam.setTimeSpentMinutes((int) minutes);
                }
                
                studentExamRepository.save(studentExam);
            }
        }
    }
    
    /**
     * 更新考试
     */
    public ExamResponse updateExam(Long examId, ExamRequest request) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 只有草稿状态或已安排状态的考试才能修改
        if (exam.getStatus() != Exam.ExamStatus.DRAFT && exam.getStatus() != Exam.ExamStatus.SCHEDULED) {
            throw new RuntimeException("只有草稿状态或已安排状态的考试才能修改");
        }
        
        // 验证开始时间
        if (request.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }
        
        // 更新考试信息
        exam.setExamName(request.getExamName());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setMaxAttempts(request.getMaxAttempts());
        exam.setIsRandomOrder(request.getIsRandomOrder());
        exam.setIsRandomOptions(request.getIsRandomOptions());
        exam.setAllowReview(request.getAllowReview());
        
        // 重新计算结束时间
        exam.setEndTime(exam.getStartTime().plusMinutes(exam.getDurationMinutes()));
        
        // 如果状态是草稿，更新为已安排
        if (exam.getStatus() == Exam.ExamStatus.DRAFT) {
            exam.setStatus(Exam.ExamStatus.SCHEDULED);
        }
        
        Exam savedExam = examRepository.save(exam);
        return convertToResponse(savedExam);
    }
    
    /**
     * 转换为响应DTO
     */
    private ExamResponse convertToResponse(Exam exam) {
        ExamResponse response = new ExamResponse();
        BeanUtils.copyProperties(exam, response);
        
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
        
        // 计算考试统计数据
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(exam.getId());
        long submittedCount = studentExams.stream()
                .filter(se -> se.getSubmitTime() != null)
                .count();
        long gradedCount = studentExams.stream()
                .filter(se -> se.getTotalScore() != null)
                .count();
        
        // 设置统计数据
        response.setStudentCount(exam.getStudentCount());
        response.setParticipatedCount((int) submittedCount);
        response.setUnsubmittedCount((int) (studentExams.size() - submittedCount));
        response.setGradedCount((int) gradedCount);
        
        // 计算判卷进度
        int gradingProgress = submittedCount > 0 ? (int) (gradedCount * 100 / submittedCount) : 0;
        response.setGradingProgress(gradingProgress);
        
        // 设置考试时长（分钟）
        response.setDuration(exam.getDurationMinutes());
        
        // 手动设置状态字段，确保枚举值正确转换
        if (exam.getStatus() != null) {
            response.setStatus(exam.getStatus().name());
        }
        
        return response;
    }
    
    /**
     * 生成考试代码
     */
    private String generateExamCode() {
        return "EXAM_" + System.currentTimeMillis();
    }
    
    /**
     * 获取考试学生列表
     */
    public List<Map<String, Object>> getExamStudents(Long examId) {
        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取该考试的所有学生考试记录
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(examId);
        
        List<Map<String, Object>> students = new ArrayList<>();
        
        for (StudentExam studentExam : studentExams) {
            // 获取学生信息
            User student = userRepository.findById(studentExam.getStudentId()).orElse(null);
            if (student == null) continue;
            
            // 获取学生班级信息
            List<StudentClass> studentClasses = studentClassRepository.findByStudentIdAndIsActiveTrue(student.getId());
            String className = "";
            if (!studentClasses.isEmpty()) {
                com.example.manger.entity.Class clazz = classRepository.findById(studentClasses.get(0).getClassId()).orElse(null);
                if (clazz != null) {
                    className = clazz.getClassName();
                }
            }
            
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("id", student.getId());
            studentInfo.put("username", student.getUsername());
            studentInfo.put("realName", student.getUsername()); // 使用用户名作为显示名称
            studentInfo.put("email", student.getEmail());
            studentInfo.put("phone", student.getPhone());
            studentInfo.put("className", className);
            studentInfo.put("examStatus", studentExam.getStatus().name());
            studentInfo.put("startTime", studentExam.getStartTime());
            studentInfo.put("submitTime", studentExam.getSubmitTime());
            studentInfo.put("timeSpentMinutes", studentExam.getTimeSpentMinutes());
            studentInfo.put("totalScore", studentExam.getTotalScore());
            studentInfo.put("attemptNumber", studentExam.getAttemptNumber());
            
            students.add(studentInfo);
        }
        
        return students;
    }
    
    /**
     * 获取判卷考试列表
     */
    public PageResponse<Map<String, Object>> getGradingExams(int page, int size, String keyword, Long classId, String status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "startTime"));
        
        // 构建查询条件
        Specification<Exam> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isActive"), true));
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(root.get("examName"), "%" + keyword + "%"));
            }
            
            if (classId != null) {
                predicates.add(cb.equal(root.get("classId"), classId));
            }
            
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), Exam.ExamStatus.valueOf(status)));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        Page<Exam> examPage = examRepository.findAll(spec, pageable);
        
        List<Map<String, Object>> examResponses = examPage.getContent().stream()
                .map(this::convertToGradingResponse)
                .collect(Collectors.toList());
        
        return PageResponse.of(examResponses, page, size, examPage.getTotalElements());
    }
    
    /**
     * 获取考试学生列表（用于判卷）
     */
    public PageResponse<Map<String, Object>> getExamStudentsForGrading(Long examId, int page, int size, 
            String keyword, String status, String gradingStatus) {
        // 获取考试信息
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取该考试的所有学生考试记录
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(examId);
        
        // 先获取所有学生信息，然后进行过滤
        List<Map<String, Object>> allStudents = new ArrayList<>();
        
        for (StudentExam studentExam : studentExams) {
            // 获取学生信息
            User student = userRepository.findById(studentExam.getStudentId()).orElse(null);
            if (student == null) continue;
            
            // 获取学生班级信息
            List<StudentClass> studentClasses = studentClassRepository.findByStudentIdAndIsActiveTrue(student.getId());
            String className = "";
            if (!studentClasses.isEmpty()) {
                com.example.manger.entity.Class clazz = classRepository.findById(studentClasses.get(0).getClassId()).orElse(null);
                if (clazz != null) {
                    className = clazz.getClassName();
                }
            }
            
            // 查询判卷结果，判断是否已完成判卷（只有提交后才算完成）
            Optional<GradingResult> gradingResultOpt = gradingResultRepository.findByStudentExamId(studentExam.getId());
            String studentGradingStatus;
            if (gradingResultOpt.isPresent() && gradingResultOpt.get().getIsGradingCompleted()) {
                studentGradingStatus = "GRADED"; // 已完成判卷（已提交）
            } else {
                studentGradingStatus = "NOT_GRADED"; // 未完成判卷（未提交或未开始）
            }
            
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("id", student.getId());
            studentInfo.put("username", student.getUsername());
            studentInfo.put("realName", student.getUsername()); // 使用用户名作为显示名称
            studentInfo.put("email", student.getEmail());
            studentInfo.put("phone", student.getPhone());
            studentInfo.put("className", className);
            studentInfo.put("examStatus", studentExam.getStatus().name());
            studentInfo.put("startTime", studentExam.getStartTime());
            studentInfo.put("submitTime", studentExam.getSubmitTime());
            studentInfo.put("timeSpentMinutes", studentExam.getTimeSpentMinutes());
            studentInfo.put("totalScore", studentExam.getTotalScore());
            studentInfo.put("attemptNumber", studentExam.getAttemptNumber());
            studentInfo.put("gradingStatus", studentGradingStatus);
            
            allStudents.add(studentInfo);
        }
        
        // 应用过滤条件
        List<Map<String, Object>> filteredStudents = allStudents.stream()
                .filter(student -> {
                    // 状态过滤
                    if (status != null && !status.trim().isEmpty()) {
                        if ("SUBMITTED".equals(status) && student.get("submitTime") == null) return false;
                        if ("NOT_SUBMITTED".equals(status) && student.get("submitTime") != null) return false;
                    }
                    
                    // 判卷状态过滤
                    if (gradingStatus != null && !gradingStatus.trim().isEmpty()) {
                        String currentGradingStatus = (String) student.get("gradingStatus");
                        if (!gradingStatus.equals(currentGradingStatus)) return false;
                    }
                    
                    // 关键词搜索
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        String searchKeyword = keyword.toLowerCase();
                        String username = ((String) student.get("username")).toLowerCase();
                        String email = ((String) student.get("email")).toLowerCase();
                        String className = ((String) student.get("className")).toLowerCase();
                        
                        if (!username.contains(searchKeyword) && 
                            !email.contains(searchKeyword) && 
                            !className.contains(searchKeyword)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // 按交卷时间排序
        filteredStudents.sort((a, b) -> {
            Object submitTimeA = a.get("submitTime");
            Object submitTimeB = b.get("submitTime");
            
            if (submitTimeA == null && submitTimeB == null) return 0;
            if (submitTimeA == null) return 1;
            if (submitTimeB == null) return -1;
            
            return ((LocalDateTime) submitTimeB).compareTo((LocalDateTime) submitTimeA);
        });
        
        // 分页处理
        int start = (page - 1) * size;
        int end = Math.min(start + size, filteredStudents.size());
        List<Map<String, Object>> pageStudents = filteredStudents.subList(start, end);
        
        return PageResponse.of(pageStudents, page, size, (long) filteredStudents.size());
    }
    
    /**
     * 获取学生答案（新设计：使用单条记录存储判分结果）
     */
    public Map<String, Object> getStudentAnswers(Long examId, Long studentId, HttpServletRequest request) {
        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new RuntimeException("学生考试记录不存在"));
        
        // 获取学生答案和试卷内容
        Optional<StudentAnswer> studentAnswerOpt = studentAnswerRepository.findByStudentExamId(studentExam.getId());
        Map<String, Object> studentAnswers = new HashMap<>();
        List<Map<String, Object>> questions;
        
        if (studentAnswerOpt.isPresent()) {
            StudentAnswer studentAnswer = studentAnswerOpt.get();
            Map<String, Object> answerContent = studentAnswer.getAnswerContent();
            if (answerContent != null && !answerContent.isEmpty()) {
                // answerContent 直接就是答案 Map，不需要再提取 "answers"
                studentAnswers = answerContent;
            }
            
            // 优先从保存的试卷内容读取（包含题目和选项顺序）
            Map<String, Object> paperContent = studentAnswer.getPaperContent();
            if (paperContent != null && paperContent.containsKey("questions")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> savedQuestions = (List<Map<String, Object>>) paperContent.get("questions");
                questions = savedQuestions;
            } else {
                // 如果没有保存试卷内容，则获取乱序试卷（兼容旧数据）
                questions = getShuffledQuestionsForGrading(exam, studentId);
            }
        } else {
            // 没有学生答案，获取乱序试卷
            questions = getShuffledQuestionsForGrading(exam, studentId);
        }
        
        // 获取判分结果（新设计：每个学生考试一条记录）
        Optional<GradingResult> gradingResultOpt = gradingResultRepository.findByStudentExamId(studentExam.getId());
        Map<Long, Map<String, Object>> gradingMap = new HashMap<>();
        
        if (gradingResultOpt.isPresent()) {
            GradingResult gradingResult = gradingResultOpt.get();
            @SuppressWarnings("unchecked")
            Map<String, Object> gradingData = (Map<String, Object>) gradingResult.getGradingData();
            
            // 将JSON数据转换为题目ID到判分信息的映射
            for (Map.Entry<String, Object> entry : gradingData.entrySet()) {
                try {
                    Long questionId = Long.valueOf(entry.getKey());
                    @SuppressWarnings("unchecked")
                    Map<String, Object> questionGrading = (Map<String, Object>) entry.getValue();
                    gradingMap.put(questionId, questionGrading);
                } catch (NumberFormatException e) {
                    // 忽略非数字键
                }
            }
        }
        
        // 补充正确答案信息（从数据库读取）
        enrichQuestionsWithCorrectAnswers(questions);
        
        // 将学生答案和判分数据添加到题目中
        for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            // 尝试从 "id" 或 "questionId" 获取题目ID（兼容不同数据源）
            Object idObj = question.get("id");
            if (idObj == null) {
                idObj = question.get("questionId");
            }
            Long questionId = idObj != null ? Long.valueOf(idObj.toString()) : null;
            
            // 确保题目有 id 字段（前端使用 id）
            if (!question.containsKey("id") && question.containsKey("questionId")) {
                question.put("id", question.get("questionId"));
            }
            
            // 确保题目文本字段名称统一（前端使用 questionText）
            if (!question.containsKey("questionText") && question.containsKey("questionContent")) {
                question.put("questionText", question.get("questionContent"));
            }
            
            // 学生答案使用题目索引作为键
            question.put("studentAnswers", studentAnswers.get(String.valueOf(i)));
            
            // 从判分结果中获取已保存的分数（只有当 questionId 不为 null 时才查询）
            Map<String, Object> questionGrading = null;
            if (questionId != null) {
                questionGrading = gradingMap.get(questionId);
            }
            if (questionGrading != null) {
                Object givenScoreObj = questionGrading.get("givenScore");
                if (givenScoreObj != null) {
                    question.put("givenScore", Double.valueOf(givenScoreObj.toString()));
                } else {
                    question.put("givenScore", null);
                }
                // 安全处理 isGraded 字段的类型转换
                Object isGradedObj = questionGrading.get("isGraded");
                if (isGradedObj instanceof Boolean) {
                    question.put("isGraded", isGradedObj);
                } else if (isGradedObj instanceof Integer) {
                    question.put("isGraded", ((Integer) isGradedObj) == 1);
                } else if (isGradedObj instanceof Number) {
                    question.put("isGraded", ((Number) isGradedObj).intValue() == 1);
                } else {
                    question.put("isGraded", false);
                }
            } else {
                question.put("givenScore", null);
                question.put("isGraded", false);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("examId", examId);
        result.put("studentId", studentId);
        result.put("questions", questions);
        result.put("totalQuestions", questions.size());
        result.put("totalPoints", questions.stream()
            .mapToInt(q -> {
                Object points = q.get("points");
                return points instanceof Integer ? (Integer) points : 0;
            })
            .sum());
        
        return result;
    }
    
    /**
     * 补充题目的正确答案信息
     */
    private void enrichQuestionsWithCorrectAnswers(List<Map<String, Object>> questions) {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        
        // 提取所有题目ID
        List<Long> questionIds = new ArrayList<>();
        for (Map<String, Object> question : questions) {
            Object idObj = question.get("questionId");
            if (idObj == null) {
                idObj = question.get("id");
            }
            if (idObj != null) {
                try {
                    questionIds.add(Long.valueOf(idObj.toString()));
                } catch (NumberFormatException e) {
                    // 忽略无效的ID
                }
            }
        }
        
        if (questionIds.isEmpty()) {
            return;
        }
        
        // 从数据库查询题目完整信息
        List<Question> questionList = questionRepository.findByIdIn(questionIds);
        Map<Long, Question> questionMap = questionList.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        
        // 为每个题目补充正确答案信息
        for (Map<String, Object> question : questions) {
            Object idObj = question.get("questionId");
            if (idObj == null) {
                idObj = question.get("id");
            }
            if (idObj == null) continue;
            
            Long questionId;
            try {
                questionId = Long.valueOf(idObj.toString());
            } catch (NumberFormatException e) {
                continue;
            }
            
            Question fullQuestion = questionMap.get(questionId);
            if (fullQuestion == null) continue;
            
            String questionType = (String) question.get("questionType");
            if (questionType == null) {
                questionType = fullQuestion.getType().name();
                question.put("questionType", questionType);
            }
            
            // 补充题目内容（如果缺失）
            if (!question.containsKey("questionText") && !question.containsKey("questionContent")) {
                question.put("questionText", fullQuestion.getContent());
                question.put("questionContent", fullQuestion.getContent());
            } else if (!question.containsKey("questionText")) {
                // 确保有 questionText 字段（前端使用）
                question.put("questionText", question.get("questionContent"));
            }
            
            // 确保有 id 字段（前端使用，使用 questionId 的值）
            if (!question.containsKey("id") && question.containsKey("questionId")) {
                question.put("id", question.get("questionId"));
            }
            
            // 补充选项的正确答案标记（选择题和判断题）
            if (isChoiceQuestion(questionType)) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
                if (options != null && fullQuestion.getOptions() != null) {
                    Map<String, Boolean> correctMap = new HashMap<>();
                    for (Map<String, Object> opt : fullQuestion.getOptions()) {
                        Object key = opt.get("key");
                        Object correct = opt.get("correct");
                        if (key != null && correct != null) {
                            Boolean isCorrect = false;
                            if (correct instanceof Boolean) {
                                isCorrect = (Boolean) correct;
                            } else if (correct instanceof Integer) {
                                isCorrect = ((Integer) correct) == 1;
                            } else if (correct instanceof Number) {
                                isCorrect = ((Number) correct).intValue() == 1;
                            }
                            correctMap.put(key.toString(), isCorrect);
                        }
                    }
                    
                    // 为选项添加 isCorrect 标记
                    for (Map<String, Object> opt : options) {
                        Object key = opt.get("optionKey");
                        if (key != null) {
                            opt.put("isCorrect", correctMap.getOrDefault(key.toString(), false));
                        }
                    }
                }
            }
            
            // 补充答案（填空题、主观题和程序题）
            if (fullQuestion.getType() == Question.QuestionType.FILL_BLANK || 
                fullQuestion.getType() == Question.QuestionType.SUBJECTIVE ||
                fullQuestion.getType() == Question.QuestionType.PROGRAMMING) {
                if (fullQuestion.getCorrectAnswer() != null && !fullQuestion.getCorrectAnswer().isEmpty()) {
                    List<Map<String, Object>> answers = new ArrayList<>();
                    Map<String, Object> answerData = new HashMap<>();
                    answerData.put("answerContent", fullQuestion.getCorrectAnswer());
                    answers.add(answerData);
                    question.put("answers", answers);
                }
            }
            
            // 补充程序题的特殊字段（编程语言）
            if (fullQuestion.getType() == Question.QuestionType.PROGRAMMING) {
                // 从数据库读取编程语言，如果没有则使用默认值 JAVA
                String programmingLanguage = fullQuestion.getProgrammingLanguage();
                if (programmingLanguage == null || programmingLanguage.isEmpty()) {
                    programmingLanguage = "JAVA"; // 默认值
                }
                question.put("programmingLanguage", programmingLanguage);
            }
        }
    }
    
    /**
     * 获取学生答案（内部方法，不需要HttpServletRequest）
     */
    private Map<String, Object> getStudentAnswersInternal(Long examId, Long studentId) {
        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new RuntimeException("学生考试记录不存在"));
        
        // 获取试卷题目（原始顺序，用于计算分数）- 新版本：从Paper的questions字段读取
        Paper paper = paperRepository.findById(exam.getPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        
        List<Map<String, Object>> paperQuestions = paper.getQuestions();
        if (paperQuestions == null || paperQuestions.isEmpty()) {
            throw new RuntimeException("试卷中没有题目");
        }
        
        List<Long> questionIds = paperQuestions.stream()
                .map(q -> ((Number) q.get("questionId")).longValue())
                .collect(Collectors.toList());
        
        List<Question> questions = questionRepository.findByIdIn(questionIds);
        
        // 获取学生答案
        Optional<StudentAnswer> studentAnswerOpt = studentAnswerRepository.findByStudentExamId(studentExam.getId());
        Map<String, Object> studentAnswers = new HashMap<>();
        if (studentAnswerOpt.isPresent()) {
            Map<String, Object> answerContent = studentAnswerOpt.get().getAnswerContent();
            if (answerContent != null && !answerContent.isEmpty()) {
                // answerContent 直接就是答案 Map，不需要再提取 "answers"
                studentAnswers = answerContent;
            }
        }
        
        // 构建题目数据（原始顺序）
        List<Map<String, Object>> questionList = new ArrayList<>();
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            Map<String, Object> paperQuestion = paperQuestions.get(i);
            
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("id", question.getId());
            questionData.put("questionText", question.getContent());
            questionData.put("questionType", question.getType().name());
            questionData.put("points", paperQuestion.get("points"));
            questionData.put("questionOrder", paperQuestion.get("questionOrder"));
            
            // 获取选项 - 从Question的JSON字段读取
            List<Map<String, Object>> optionList = new ArrayList<>();
            if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                for (Map<String, Object> optionMap : question.getOptions()) {
                    Map<String, Object> optionData = new HashMap<>();
                    optionData.put("optionKey", optionMap.get("key"));
                    optionData.put("optionContent", optionMap.get("content"));
                    // 标记正确答案 - 安全类型转换
                    Object correctObj = optionMap.get("correct");
                    Boolean isCorrect = false;
                    if (correctObj instanceof Boolean) {
                        isCorrect = (Boolean) correctObj;
                    } else if (correctObj instanceof Integer) {
                        isCorrect = ((Integer) correctObj) == 1;
                    } else if (correctObj instanceof Number) {
                        isCorrect = ((Number) correctObj).intValue() == 1;
                    }
                    optionData.put("isCorrect", isCorrect);
                    optionList.add(optionData);
                }
            }
            
            questionData.put("options", optionList);
            questionData.put("studentAnswers", studentAnswers.get(String.valueOf(i)));
            
            questionList.add(questionData);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("examId", examId);
        result.put("studentId", studentId);
        result.put("questions", questionList);
        result.put("totalQuestions", questions.size());
        result.put("totalPoints", paperQuestions.stream()
                .mapToInt(q -> ((Number) q.get("points")).intValue())
                .sum());
        
        return result;
    }
    
    /**
     * 获取乱序题目（用于判卷显示）- 新版本：从Paper的questions字段读取
     */
    private List<Map<String, Object>> getShuffledQuestionsForGrading(Exam exam, Long studentId) {
        // 获取试卷题目
        Paper paper = paperRepository.findById(exam.getPaperId())
                .orElseThrow(() -> new RuntimeException("试卷不存在"));
        
        List<Map<String, Object>> paperQuestions = paper.getQuestions();
        if (paperQuestions == null || paperQuestions.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> questionIds = paperQuestions.stream()
                .map(q -> ((Number) q.get("questionId")).longValue())
                .collect(Collectors.toList());
        
        List<Question> questions = questionRepository.findByIdIn(questionIds);
        
        // 构建题目数据（按原始顺序）
        List<Map<String, Object>> questionList = new ArrayList<>();
        
        for (int i = 0; i < paperQuestions.size(); i++) {
            Map<String, Object> paperQuestion = paperQuestions.get(i);
            Long questionId = ((Number) paperQuestion.get("questionId")).longValue();
            
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElse(null);
            
            if (question == null) continue;
            
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("id", question.getId());
            questionData.put("questionText", question.getContent());
            questionData.put("questionType", question.getType().name());
            questionData.put("points", paperQuestion.get("points"));
            questionData.put("questionOrder", paperQuestion.get("questionOrder"));
            
            // 处理选项（如果是选择题）- 从Question的JSON字段读取
            if (isChoiceQuestion(question.getType().name())) {
                List<Map<String, Object>> optionList = new ArrayList<>();
                if (question.getOptions() != null && !question.getOptions().isEmpty()) {
                    for (Map<String, Object> optionMap : question.getOptions()) {
                        Map<String, Object> optionData = new HashMap<>();
                        optionData.put("optionKey", optionMap.get("key"));
                        optionData.put("optionContent", optionMap.get("content"));
                        // 标记正确答案 - 安全类型转换
                        Object correctObj = optionMap.get("correct");
                        Boolean isCorrect = false;
                        if (correctObj instanceof Boolean) {
                            isCorrect = (Boolean) correctObj;
                        } else if (correctObj instanceof Integer) {
                            isCorrect = ((Integer) correctObj) == 1;
                        } else if (correctObj instanceof Number) {
                            isCorrect = ((Number) correctObj).intValue() == 1;
                        }
                        optionData.put("isCorrect", isCorrect);
                        optionList.add(optionData);
                    }
                }
                
                // 如果考试设置了选项乱序，则对选项进行乱序（判断题除外）
                if (exam.getIsRandomOptions() && question.getType() != Question.QuestionType.TRUE_FALSE) {
                    optionList = shuffleOptions(optionList, studentId, question.getId());
                }
                
                // 调试：检查乱序后的正确答案标记
                System.out.println("After shuffling, correct options for question " + question.getId() + ":");
                for (Map<String, Object> opt : optionList) {
                    if ((Boolean) opt.get("isCorrect")) {
                        System.out.println("  - " + opt.get("optionKey") + ": " + opt.get("optionContent"));
                    }
                }
                
                
                questionData.put("options", optionList);
            }
            
            // 处理答案（如果是填空题或主观题）- 从Question的TEXT字段读取
            if ((question.getType() == Question.QuestionType.FILL_BLANK || 
                question.getType() == Question.QuestionType.SUBJECTIVE) && 
                question.getCorrectAnswer() != null) {
                List<Map<String, Object>> answerList = new ArrayList<>();
                Map<String, Object> answerData = new HashMap<>();
                answerData.put("answerContent", question.getCorrectAnswer());
                answerList.add(answerData);
                
                questionData.put("answers", answerList);
            }
            
            questionList.add(questionData);
        }
        
        // 如果考试设置了题目乱序，则对题目进行乱序（与学生考试时保持一致）
        if (exam.getIsRandomOrder()) {
            questionList = shuffleQuestions(questionList, studentId);
        }
        
        return questionList;
    }
    
    /**
     * 题目乱序（与学生考试时保持一致）
     */
    private List<Map<String, Object>> shuffleQuestions(List<Map<String, Object>> questions, Long studentId) {
        // 使用学生ID作为种子，确保同一学生每次看到相同的顺序
        Random random = new Random(studentId);
        
        // 按题型分组
        Map<String, List<Map<String, Object>>> groupedQuestions = questions.stream()
                .collect(Collectors.groupingBy(q -> (String) q.get("questionType")));
        
        List<Map<String, Object>> shuffledQuestions = new ArrayList<>();
        
        // 按固定顺序处理题型：单选 -> 多选 -> 判断 -> 填空 -> 主观
        String[] typeOrder = {"SINGLE_CHOICE", "MULTIPLE_CHOICE", "TRUE_FALSE", "FILL_BLANK", "SUBJECTIVE"};
        
        for (String type : typeOrder) {
            List<Map<String, Object>> typeQuestions = groupedQuestions.get(type);
            if (typeQuestions != null && !typeQuestions.isEmpty()) {
                // 对同类型题目进行乱序
                Collections.shuffle(typeQuestions, random);
                shuffledQuestions.addAll(typeQuestions);
            }
        }
        
        // 重新设置题目顺序
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            shuffledQuestions.get(i).put("questionOrder", i + 1);
        }
        
        return shuffledQuestions;
    }
    
    /**
     * 乱序选项（与学生考试时保持一致）
     */
    private List<Map<String, Object>> shuffleOptions(List<Map<String, Object>> options, Long studentId, Long questionId) {
        // 使用学生ID和题目ID作为种子，确保同一学生同一题目每次看到相同的顺序
        // 同时确保不同学生的选项顺序不同
        long seed = studentId * 1000000L + questionId;
        Random random = new Random(seed);
        Collections.shuffle(options, random);
        return options;
    }
    
    /**
     * 保存判卷结果（新设计：使用单条记录存储）
     */
    public void saveGradingResult(Map<String, Object> gradingData) {
        Long examId = Long.valueOf(gradingData.get("examId").toString());
        Long studentId = Long.valueOf(gradingData.get("studentId").toString());
        
        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new RuntimeException("学生考试记录不存在"));
        
        // 获取考试信息
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取学生信息
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        // 处理题目分数
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) gradingData.get("questions");
        Map<String, Object> gradingDataMap = new HashMap<>();
        
        if (questions != null) {
            // 构建题目分数映射
            Map<String, Object> questionGrading = new HashMap<>();
            for (Map<String, Object> question : questions) {
                // 兼容不同的字段名：优先 "questionId"，其次 "id"
                Object idObj = question.get("questionId");
                if (idObj == null) {
                    idObj = question.get("id");
                }
                
                if (idObj == null) {
                    System.err.println("警告：题目缺少ID字段，跳过此题");
                    continue;
                }
                
                Long questionId = Long.valueOf(idObj.toString());
                Object givenScoreObj = question.get("givenScore");
                
                // 安全处理 isGraded 字段
                Boolean isGraded = false;
                Object isGradedObj = question.get("isGraded");
                if (isGradedObj != null) {
                    if (isGradedObj instanceof Boolean) {
                        isGraded = (Boolean) isGradedObj;
                    } else if (isGradedObj instanceof String) {
                        isGraded = Boolean.valueOf((String) isGradedObj);
                    }
                }
                
                Map<String, Object> questionData = new HashMap<>();
                if (givenScoreObj != null) {
                    questionData.put("givenScore", Double.valueOf(givenScoreObj.toString()));
                } else {
                    questionData.put("givenScore", null);
                }
                questionData.put("isGraded", isGraded);
                
                gradingDataMap.put(questionId.toString(), questionData);
                questionGrading.put(questionId.toString(), givenScoreObj != null ? Double.valueOf(givenScoreObj.toString()) : 0.0);
            }
            gradingData.put("questionGrading", questionGrading);
        }
        
        // 计算总分（包含自动判分和手动判分）
        Double totalScore = calculateTotalScore(examId, studentId, gradingData);
        studentExam.setTotalScore(BigDecimal.valueOf(totalScore));
        
        // 查找或创建判分结果记录
        Optional<GradingResult> existingResult = gradingResultRepository.findByStudentExamId(studentExam.getId());
        GradingResult gradingResult;
        
        if (existingResult.isPresent()) {
            gradingResult = existingResult.get();
        } else {
            gradingResult = new GradingResult();
            gradingResult.setExamId(examId);
            gradingResult.setStudentId(studentId);
            gradingResult.setStudentExamId(studentExam.getId());
            gradingResult.setExamName(exam.getExamName());
            gradingResult.setStudentName(student.getUsername());
            gradingResult.setExamStartTime(exam.getStartTime());
        }
        
        // 更新判分数据（保存时标记为未完成，只有提交时才标记为完成）
        gradingResult.setGradingData(gradingDataMap);
        gradingResult.setTotalScore(BigDecimal.valueOf(totalScore));
        gradingResult.setIsGradingCompleted(false); // 保存时标记为未完成
        gradingResult.setUpdatedAt(LocalDateTime.now());
        
        gradingResultRepository.save(gradingResult);
        studentExamRepository.save(studentExam);
    }
    
    /**
     * 提交判卷结果（新设计：使用单条记录存储）
     */
    public void submitGradingResult(Map<String, Object> gradingData, HttpServletRequest request) {
        Long examId = Long.valueOf(gradingData.get("examId").toString());
        Long studentId = Long.valueOf(gradingData.get("studentId").toString());
        
        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId)
                .orElseThrow(() -> new RuntimeException("学生考试记录不存在"));
        
        // 获取考试信息
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取学生信息
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("学生不存在"));
        
        // 处理题目分数
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> questions = (List<Map<String, Object>>) gradingData.get("questions");
        Map<String, Object> gradingDataMap = new HashMap<>();
        
        if (questions != null) {
            // 构建题目分数映射
            Map<String, Object> questionGrading = new HashMap<>();
            for (Map<String, Object> question : questions) {
                // 兼容不同的字段名：优先 "questionId"，其次 "id"
                Object idObj = question.get("questionId");
                if (idObj == null) {
                    idObj = question.get("id");
                }
                
                if (idObj == null) {
                    System.err.println("警告：题目缺少ID字段，跳过此题");
                    continue;
                }
                
                Long questionId = Long.valueOf(idObj.toString());
                Object givenScoreObj = question.get("givenScore");
                
                // 安全处理 isGraded 字段
                Boolean isGraded = false;
                Object isGradedObj = question.get("isGraded");
                if (isGradedObj != null) {
                    if (isGradedObj instanceof Boolean) {
                        isGraded = (Boolean) isGradedObj;
                    } else if (isGradedObj instanceof String) {
                        isGraded = Boolean.valueOf((String) isGradedObj);
                    }
                }
                
                Map<String, Object> questionData = new HashMap<>();
                if (givenScoreObj != null) {
                    questionData.put("givenScore", Double.valueOf(givenScoreObj.toString()));
                } else {
                    questionData.put("givenScore", null);
                }
                questionData.put("isGraded", isGraded);
                
                gradingDataMap.put(questionId.toString(), questionData);
                questionGrading.put(questionId.toString(), givenScoreObj != null ? Double.valueOf(givenScoreObj.toString()) : 0.0);
            }
            gradingData.put("questionGrading", questionGrading);
        }
        
        // 计算总分（包含自动判分和手动判分）
        Double totalScore = calculateTotalScore(examId, studentId, gradingData);
        studentExam.setTotalScore(BigDecimal.valueOf(totalScore));
        
        // 查找或创建判分结果记录
        Optional<GradingResult> existingResult = gradingResultRepository.findByStudentExamId(studentExam.getId());
        GradingResult gradingResult;
        
        if (existingResult.isPresent()) {
            gradingResult = existingResult.get();
        } else {
            gradingResult = new GradingResult();
            gradingResult.setExamId(examId);
            gradingResult.setStudentId(studentId);
            gradingResult.setStudentExamId(studentExam.getId());
            gradingResult.setExamName(exam.getExamName());
            gradingResult.setStudentName(student.getUsername());
            gradingResult.setExamStartTime(exam.getStartTime());
        }
        
        // 更新判分数据
        gradingResult.setGradingData(gradingDataMap);
        gradingResult.setTotalScore(BigDecimal.valueOf(totalScore));
        gradingResult.setIsGradingCompleted(true); // 提交时标记为完成
        gradingResult.setGradedAt(LocalDateTime.now());
        gradingResult.setGradedBy(getCurrentUserId(request)); // 获取当前用户ID
        gradingResult.setUpdatedAt(LocalDateTime.now());
        
        gradingResultRepository.save(gradingResult);
        studentExamRepository.save(studentExam);
    }
    
    /**
     * 转换为判卷响应格式
     */
    private Map<String, Object> convertToGradingResponse(Exam exam) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", exam.getId());
        response.put("examName", exam.getExamName());
        
        // 获取试卷名称
        String paperName = "";
        try {
            Paper paper = paperRepository.findById(exam.getPaperId()).orElse(null);
            if (paper != null) {
                paperName = paper.getPaperName();
            }
        } catch (Exception e) {
            paperName = "未知试卷";
        }
        response.put("paperName", paperName);
        
        // 获取班级名称
        String className = "";
        try {
            com.example.manger.entity.Class clazz = classRepository.findById(exam.getClassId()).orElse(null);
            if (clazz != null) {
                className = clazz.getClassName();
            }
        } catch (Exception e) {
            className = "未知班级";
        }
        response.put("className", className);
        
        // 计算判卷进度和已判卷人数
        List<StudentExam> studentExams = studentExamRepository.findByExamIdAndIsActiveTrue(exam.getId());
        long submittedCount = studentExams.stream()
                .filter(se -> se.getSubmitTime() != null)
                .count();
        long gradedCount = studentExams.stream()
                .filter(se -> se.getTotalScore() != null)
                .count();
        
        response.put("studentCount", exam.getStudentCount());
        response.put("participatedCount", (int) submittedCount);
        response.put("unsubmittedCount", (int) (studentExams.size() - submittedCount));
        response.put("startTime", exam.getStartTime());
        response.put("endTime", exam.getEndTime());
        response.put("duration", exam.getDurationMinutes());
        response.put("status", exam.getStatus().name());
        
        int gradingProgress = studentExams.isEmpty() ? 0 : (int) (gradedCount * 100 / studentExams.size());
        response.put("gradingProgress", gradingProgress);
        response.put("gradedCount", (int) gradedCount);
        
        return response;
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("无法获取用户信息");
    }
    
    /**
     * 计算总分（包含自动判分）
     */
    private Double calculateTotalScore(Long examId, Long studentId, Map<String, Object> gradingData) {
        try {
            // 获取学生答案（内部方法，不需要HttpServletRequest）
            Map<String, Object> studentAnswers = getStudentAnswersInternal(examId, studentId);
            List<Map<String, Object>> questions = (List<Map<String, Object>>) studentAnswers.get("questions");
            
            if (questions == null || questions.isEmpty()) {
                return 0.0;
            }
            
            double totalScore = 0.0;
            
            for (Map<String, Object> question : questions) {
                String questionType = (String) question.get("questionType");
                Integer points = (Integer) question.get("points");
                Long questionId = Long.valueOf(question.get("id").toString());
                
                // 检查是否有手动评分
                @SuppressWarnings("unchecked")
                Map<String, Object> questionGrading = (Map<String, Object>) gradingData.get("questionGrading");
                if (questionGrading != null && questionGrading.containsKey(questionId.toString())) {
                    // 使用手动评分
                    Object scoreObj = questionGrading.get(questionId.toString());
                    if (scoreObj != null) {
                        Double manualScore = Double.valueOf(scoreObj.toString());
                        totalScore += manualScore;
                    }
                } else {
                    // 自动判分（仅选择题和判断题）
                    if (isChoiceQuestion(questionType)) {
                        Double autoScore = calculateAutoScore(question, studentAnswers);
                        totalScore += autoScore;
                    }
                    // 填空题和主观题不自动判分，得分为0
                }
            }
            
            return totalScore;
        } catch (Exception e) {
            // 如果计算失败，返回0分
            return 0.0;
        }
    }
    
    /**
     * 判断是否为选择题
     */
    private boolean isChoiceQuestion(String questionType) {
        return "SINGLE_CHOICE".equals(questionType) || 
               "MULTIPLE_CHOICE".equals(questionType) || 
               "TRUE_FALSE".equals(questionType);
    }
    
    /**
     * 计算自动判分
     */
    private Double calculateAutoScore(Map<String, Object> question, Map<String, Object> studentAnswers) {
        try {
            String questionType = (String) question.get("questionType");
            Integer points = (Integer) question.get("points");
            Long questionId = Long.valueOf(question.get("id").toString());
            
            // 获取学生答案
            @SuppressWarnings("unchecked")
            Map<String, Object> answers = (Map<String, Object>) studentAnswers.get("answers");
            Object studentAnswer = answers.get(questionId.toString());
            
            // 获取正确答案
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
            List<String> correctAnswers = new ArrayList<>();
            
            if (options != null) {
                for (Map<String, Object> option : options) {
                    Boolean isCorrect = (Boolean) option.get("isCorrect");
                    if (Boolean.TRUE.equals(isCorrect)) {
                        correctAnswers.add((String) option.get("optionContent"));
                    }
                }
            }
            
            // 判分逻辑
            if ("SINGLE_CHOICE".equals(questionType) || "TRUE_FALSE".equals(questionType)) {
                // 单选题和判断题
                if (studentAnswer != null && correctAnswers.contains(studentAnswer.toString())) {
                    return points.doubleValue();
                }
            } else if ("MULTIPLE_CHOICE".equals(questionType)) {
                // 多选题
                if (studentAnswer instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> studentAnswersList = (List<String>) studentAnswer;
                    if (studentAnswersList.size() == correctAnswers.size() &&
                        studentAnswersList.containsAll(correctAnswers)) {
                        return points.doubleValue();
                    }
                }
            }
            
            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * 获取学生考试结果（仅显示分数，不显示题目内容）
     */
    public Map<String, Object> getStudentExamResult(Long examId, HttpServletRequest request) {
        // 获取当前用户ID
        Long currentUserId = getCurrentUserId(request);
        
        // 验证考试是否存在
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        
        // 获取学生考试记录
        StudentExam studentExam = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, currentUserId)
                .orElseThrow(() -> new RuntimeException("学生考试记录不存在"));
        
        // 检查学生是否有权限查看结果
        if (!studentExam.getStatus().equals(StudentExam.StudentExamStatus.SUBMITTED) && 
            !studentExam.getStatus().equals(StudentExam.StudentExamStatus.GRADED)) {
            throw new RuntimeException("考试尚未提交或评分完成");
        }
        
        // 获取判分结果
        Optional<GradingResult> gradingResultOpt = gradingResultRepository.findByStudentExamId(studentExam.getId());
        List<Map<String, Object>> questionScores = new ArrayList<>();
        
        if (gradingResultOpt.isPresent()) {
            GradingResult gradingResult = gradingResultOpt.get();
            @SuppressWarnings("unchecked")
            Map<String, Object> gradingData = (Map<String, Object>) gradingResult.getGradingData();
            
            // 获取题目信息（不包含题目内容）- 新版本：从Paper的questions字段读取
            Paper paper = paperRepository.findById(exam.getPaperId())
                    .orElseThrow(() -> new RuntimeException("试卷不存在"));
            
            List<Map<String, Object>> paperQuestions = paper.getQuestions();
            if (paperQuestions == null || paperQuestions.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("examInfo", exam);
                result.put("studentExam", studentExam);
                result.put("questionScores", questionScores);
                return result;
            }
            
            List<Long> questionIds = paperQuestions.stream()
                    .map(q -> ((Number) q.get("questionId")).longValue())
                    .collect(Collectors.toList());
            
            List<Question> questions = questionRepository.findByIdIn(questionIds);
            Map<Long, Question> questionMap = questions.stream()
                    .collect(Collectors.toMap(Question::getId, q -> q));
            
            // 构建题目分数列表
            for (int i = 0; i < paperQuestions.size(); i++) {
                Map<String, Object> paperQuestion = paperQuestions.get(i);
                Long questionId = ((Number) paperQuestion.get("questionId")).longValue();
                Question question = questionMap.get(questionId);
                
                if (question == null) continue;
                
                Map<String, Object> questionScore = new HashMap<>();
                questionScore.put("id", question.getId());
                questionScore.put("questionType", question.getType().name());
                questionScore.put("points", paperQuestion.get("points"));
                
                // 从判分结果中获取分数
                Map<String, Object> questionGrading = (Map<String, Object>) gradingData.get(question.getId().toString());
                if (questionGrading != null) {
                    Object givenScoreObj = questionGrading.get("givenScore");
                    if (givenScoreObj != null) {
                        questionScore.put("givenScore", Double.valueOf(givenScoreObj.toString()));
                    } else {
                        questionScore.put("givenScore", null);
                    }
                } else {
                    questionScore.put("givenScore", null);
                }
                
                questionScores.add(questionScore);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("examInfo", exam);
        result.put("studentExam", studentExam);
        result.put("questionScores", questionScores);
        
        return result;
    }
    
    /**
     * 获取老师端仪表盘统计数据
     */
    public Map<String, Object> getDashboardStats(HttpServletRequest request) {
        // 获取当前用户ID
        Long teacherId = getCurrentUserId(request);
        
        // 获取题库总数
        Long questionBankCount = questionRepository.count();
        
        // 获取进行中的考试数量
        Long ongoingExams = examRepository.countByStatusAndIsActiveTrue(Exam.ExamStatus.ONGOING);
        
        // 获取学生总数（当前老师管理的班级的学生）
        List<Exam> teacherExams = examRepository.findByTeacherIdAndIsActiveTrue(teacherId);
        Set<Long> classIds = teacherExams.stream()
                .map(Exam::getClassId)
                .collect(Collectors.toSet());
        
        Long studentCount = 0L;
        if (!classIds.isEmpty()) {
            studentCount = userRepository.countByClassIdInAndIsActiveTrue(new ArrayList<>(classIds));
        }
        
        // 获取已完成的考试数量
        Long completedExams = examRepository.countByStatusAndIsActiveTrue(Exam.ExamStatus.COMPLETED);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("questionBankCount", questionBankCount);
        stats.put("ongoingExams", ongoingExams);
        stats.put("studentCount", studentCount);
        stats.put("completedExams", completedExams);
        
        return stats;
    }
    
    /**
     * 获取老师端最近活动
     */
    public List<Map<String, Object>> getRecentActivities(HttpServletRequest request) {
        // 获取当前用户ID
        Long teacherId = getCurrentUserId(request);
        
        List<Map<String, Object>> activities = new ArrayList<>();
        
        // 获取最近创建的考试
        List<Exam> recentExams = examRepository.findByTeacherIdAndIsActiveTrueOrderByCreatedAtDesc(teacherId);
        for (Exam exam : recentExams.stream().limit(3).collect(Collectors.toList())) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", exam.getId());
            activity.put("content", "创建了新的考试：" + exam.getExamName());
            activity.put("time", exam.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            activity.put("type", "primary");
            activities.add(activity);
        }
        
        // 获取最近完成的判卷
        List<GradingResult> recentGrading = gradingResultRepository.findByGradedByOrderByGradedAtDesc(teacherId);
        for (GradingResult grading : recentGrading.stream().limit(2).collect(Collectors.toList())) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("id", grading.getId());
            activity.put("content", "批改了考试：" + grading.getExamName() + " - " + grading.getStudentName());
            activity.put("time", grading.getGradedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            activity.put("type", "success");
            activities.add(activity);
        }
        
        // 按时间排序并限制数量
        activities.sort((a, b) -> {
            String timeA = (String) a.get("time");
            String timeB = (String) b.get("time");
            return timeB.compareTo(timeA); // 降序排列
        });
        
        return activities.stream().limit(4).collect(Collectors.toList());
    }
}
