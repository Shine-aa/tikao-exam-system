package com.example.manger.service;

import com.example.manger.dto.ExamResponse;
import com.example.manger.entity.*;
import com.example.manger.repository.*;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学生考试试卷服务
 */
@Service
@RequiredArgsConstructor
public class StudentExamPaperService {
    
    private final ExamRepository examRepository;
    private final PaperRepository paperRepository;
    private final QuestionRepository questionRepository;
    private final StudentClassRepository studentClassRepository;
    private final StudentExamRepository studentExamRepository;
    private final JwtUtil jwtUtil;
    
    /**
     * 获取学生考试试卷题目（带乱序）
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStudentExamPaper(Long examId, HttpServletRequest request) {
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
        
        // 获取试卷信息
        Paper paper = paperRepository.findById(exam.getPaperId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷不存在"));
        
        // 获取试卷题目关联 - 新版本：从Paper的questions字段读取
        List<Map<String, Object>> questionList = paper.getQuestions();
        
        if (questionList == null || questionList.isEmpty()) {
            throw new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷中没有题目");
        }
        
        // 获取题目ID列表
        List<Long> questionIds = questionList.stream()
                .map(q -> ((Number) q.get("questionId")).longValue())
                .collect(Collectors.toList());
        
        // 获取题目详情
        List<Question> questions = questionRepository.findAllById(questionIds);
        
        // 构建题目响应数据
        List<Map<String, Object>> questionResponseList = new ArrayList<>();
        
        for (int i = 0; i < questionList.size(); i++) {
            Map<String, Object> paperQuestion = questionList.get(i);
            Long questionId = ((Number) paperQuestion.get("questionId")).longValue();
            
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(questionId))
                    .findFirst()
                    .orElse(null);
            
            if (question == null) {
                continue;
            }
            
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionId", question.getId());
            questionData.put("questionOrder", paperQuestion.get("questionOrder"));
            questionData.put("questionType", question.getType().name());
            questionData.put("questionContent", question.getContent());
            questionData.put("points", paperQuestion.get("points"));
            questionData.put("difficulty", question.getDifficulty().name());
            // 添加图片字段
            questionData.put("images", question.getImages());
            
            // 处理选项（如果是选择题）- 从Question的JSON字段读取
            if (isChoiceQuestion(question.getType()) && question.getOptions() != null && !question.getOptions().isEmpty()) {
                List<Map<String, Object>> optionList = question.getOptions().stream()
                    .map(optionMap -> {
                        Map<String, Object> optionData = new HashMap<>();
                        optionData.put("optionKey", optionMap.get("key"));
                        optionData.put("optionContent", optionMap.get("content"));
                        return optionData;
                    })
                    .collect(Collectors.toList());
                
                // 如果考试设置了选项乱序，则对选项进行乱序（判断题除外）
                if (exam.getIsRandomOptions() && question.getType() != Question.QuestionType.TRUE_FALSE) {
                    optionList = shuffleOptions(optionList, studentId, question.getId());
                }
                
                questionData.put("options", optionList);
            }
            
            // 处理答案（如果是填空题、主观题或程序题）- 从Question的TEXT字段读取
            if ((question.getType() == Question.QuestionType.FILL_BLANK || 
                question.getType() == Question.QuestionType.SUBJECTIVE ||
                question.getType() == Question.QuestionType.PROGRAMMING) && 
                question.getCorrectAnswer() != null) {
                List<Map<String, Object>> answerList = new ArrayList<>();
                Map<String, Object> answerData = new HashMap<>();
                answerData.put("answerContent", question.getCorrectAnswer());
                answerList.add(answerData);
                
                questionData.put("answers", answerList);
            }
            
            // 处理程序题的特殊字段（编程语言和测试用例）
            if (question.getType() == Question.QuestionType.PROGRAMMING) {
                // 从数据库读取编程语言，如果没有则使用默认值 JAVA
                String programmingLanguage = question.getProgrammingLanguage();
                if (programmingLanguage == null || programmingLanguage.isEmpty()) {
                    programmingLanguage = "JAVA"; // 默认值
                }
                questionData.put("programmingLanguage", programmingLanguage);
                
                // 添加测试用例（仅程序题）
                if (question.getTestCases() != null && !question.getTestCases().isEmpty()) {
                    List<Map<String, Object>> testCases = question.getTestCases().stream()
                        .map(testCaseMap -> {
                            Map<String, Object> testCase = new HashMap<>();
                            testCase.put("input", testCaseMap.get("input"));
                            testCase.put("output", testCaseMap.get("output"));
                            return testCase;
                        })
                        .collect(Collectors.toList());
                    questionData.put("testCases", testCases);
                }
            }
            
            questionResponseList.add(questionData);
        }
        
        // 如果考试设置了题目乱序，则对题目进行乱序
        if (exam.getIsRandomOrder()) {
            questionResponseList = shuffleQuestions(questionResponseList, studentId);
        }
        
        // 获取学生考试记录以计算个人截止时间
        Optional<StudentExam> studentExamOpt = studentExamRepository.findByExamIdAndStudentIdAndIsActiveTrue(examId, studentId);
        // 构建响应数据
        Map<String, Object> response = new HashMap<>();
        response.put("examId", exam.getId());
        response.put("examName", exam.getExamName());
        response.put("paperName", paper.getPaperName());
        response.put("totalQuestions", paper.getTotalQuestions());
        response.put("totalPoints", paper.getTotalPoints());
        response.put("durationMinutes", exam.getDurationMinutes());
        response.put("startTime", exam.getStartTime());
        response.put("endTime", exam.getEndTime());
        response.put("isRandomOrder", exam.getIsRandomOrder());
        response.put("isRandomOptions", exam.getIsRandomOptions());
        // 学生个人开始时间与有效截止时间（窗口截止 与 个人时长截止 取较早者）
        if (studentExamOpt.isPresent()) {
            StudentExam se = studentExamOpt.get();
            response.put("studentStartTime", se.getStartTime());
            if (se.getStartTime() != null && exam.getDurationMinutes() != null) {
                java.time.LocalDateTime personalDeadline = se.getStartTime().plusMinutes(exam.getDurationMinutes());
                java.time.LocalDateTime allowedEndTime = personalDeadline.isBefore(exam.getEndTime()) ? personalDeadline : exam.getEndTime();
                response.put("allowedEndTime", allowedEndTime);
            } else {
                response.put("allowedEndTime", exam.getEndTime());
            }
        } else {
            response.put("allowedEndTime", exam.getEndTime());
        }
        response.put("questions", questionResponseList);
        
        return response;
    }
    
    /**
     * 判断是否为选择题
     */
    private boolean isChoiceQuestion(Question.QuestionType questionType) {
        return questionType == Question.QuestionType.SINGLE_CHOICE ||
               questionType == Question.QuestionType.MULTIPLE_CHOICE ||
               questionType == Question.QuestionType.TRUE_FALSE;
    }
    
    /**
     * 题目乱序
     */
    private List<Map<String, Object>> shuffleQuestions(List<Map<String, Object>> questions, Long studentId) {
        // 使用学生ID作为种子，确保同一学生每次看到相同的顺序
        Random random = new Random(studentId);
        
        // 按题型分组
        Map<String, List<Map<String, Object>>> groupedQuestions = questions.stream()
                .collect(Collectors.groupingBy(q -> (String) q.get("questionType")));
        
        List<Map<String, Object>> shuffledQuestions = new ArrayList<>();
        
        // 按固定顺序处理题型：单选 -> 多选 -> 判断 -> 填空 -> 主观 -> 程序题
        String[] typeOrder = {"SINGLE_CHOICE", "MULTIPLE_CHOICE", "TRUE_FALSE", "FILL_BLANK", "SUBJECTIVE", "PROGRAMMING"};
        
        for (String type : typeOrder) {
            List<Map<String, Object>> typeQuestions = groupedQuestions.get(type);
            if (typeQuestions != null && !typeQuestions.isEmpty()) {
                // 对同类型题目进行乱序
                Collections.shuffle(typeQuestions, random);
                shuffledQuestions.addAll(typeQuestions);
            }
        }
        
        // 处理未在typeOrder中定义的题型（如果有的话）
        for (Map.Entry<String, List<Map<String, Object>>> entry : groupedQuestions.entrySet()) {
            String type = entry.getKey();
            if (!Arrays.asList(typeOrder).contains(type)) {
                List<Map<String, Object>> typeQuestions = entry.getValue();
                if (typeQuestions != null && !typeQuestions.isEmpty()) {
                    Collections.shuffle(typeQuestions, random);
                    shuffledQuestions.addAll(typeQuestions);
                }
            }
        }
        
        // 重新设置题目顺序
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            shuffledQuestions.get(i).put("questionOrder", i + 1);
        }
        
        return shuffledQuestions;
    }
    
    /**
     * 选项乱序
     */
    private List<Map<String, Object>> shuffleOptions(List<Map<String, Object>> options, Long studentId, Long questionId) {
        // 使用学生ID和题目ID作为种子，确保同一学生同一题目每次看到相同的顺序
        // 同时确保不同学生的选项顺序不同
        long seed = studentId * 1000000L + questionId;
        Random random = new Random(seed);
        Collections.shuffle(options, random);
        return options;
    }
}
