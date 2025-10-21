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
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final StudentClassRepository studentClassRepository;
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
        
        // 获取试卷题目关联
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperIdOrderByQuestionOrder(exam.getPaperId());
        
        if (paperQuestions.isEmpty()) {
            throw new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷中没有题目");
        }
        
        // 获取题目ID列表
        List<Long> questionIds = paperQuestions.stream()
                .map(PaperQuestion::getQuestionId)
                .collect(Collectors.toList());
        
        // 获取题目详情
        List<Question> questions = questionRepository.findAllById(questionIds);
        
        // 获取题目选项和答案
        List<QuestionOption> allOptions = questionOptionRepository.findByQuestionIdIn(questionIds);
        List<QuestionAnswer> allAnswers = questionAnswerRepository.findByQuestionIdIn(questionIds);
        
        // 按题目ID分组
        Map<Long, List<QuestionOption>> optionsMap = allOptions.stream()
                .collect(Collectors.groupingBy(QuestionOption::getQuestionId));
        Map<Long, List<QuestionAnswer>> answersMap = allAnswers.stream()
                .collect(Collectors.groupingBy(QuestionAnswer::getQuestionId));
        
        // 构建题目响应数据
        List<Map<String, Object>> questionList = new ArrayList<>();
        
        for (int i = 0; i < paperQuestions.size(); i++) {
            PaperQuestion paperQuestion = paperQuestions.get(i);
            Question question = questions.stream()
                    .filter(q -> q.getId().equals(paperQuestion.getQuestionId()))
                    .findFirst()
                    .orElse(null);
            
            if (question == null) continue;
            
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("questionId", question.getId());
            questionData.put("questionOrder", i + 1);
            questionData.put("questionType", question.getType().name());
            questionData.put("questionContent", question.getContent());
            questionData.put("points", paperQuestion.getPoints());
            questionData.put("difficulty", question.getDifficulty().name());
            
            // 处理选项（如果是选择题）
            if (isChoiceQuestion(question.getType())) {
                List<QuestionOption> options = optionsMap.getOrDefault(question.getId(), new ArrayList<>());
                List<Map<String, Object>> optionList = new ArrayList<>();
                
                for (QuestionOption option : options) {
                    Map<String, Object> optionData = new HashMap<>();
                    optionData.put("optionId", option.getId());
                    optionData.put("optionKey", option.getOptionKey());
                    optionData.put("optionContent", option.getOptionContent());
                    optionData.put("sortOrder", option.getSortOrder());
                    optionList.add(optionData);
                }
                
                // 如果考试设置了选项乱序，则对选项进行乱序（判断题除外）
                if (exam.getIsRandomOptions() && question.getType() != Question.QuestionType.TRUE_FALSE) {
                    optionList = shuffleOptions(optionList, studentId, question.getId());
                }
                
                questionData.put("options", optionList);
            }
            
            // 处理答案（如果是填空题或主观题）
            if (question.getType() == Question.QuestionType.FILL_BLANK || 
                question.getType() == Question.QuestionType.SUBJECTIVE) {
                List<QuestionAnswer> answers = answersMap.getOrDefault(question.getId(), new ArrayList<>());
                List<Map<String, Object>> answerList = new ArrayList<>();
                
                for (QuestionAnswer answer : answers) {
                    Map<String, Object> answerData = new HashMap<>();
                    answerData.put("answerId", answer.getId());
                    answerData.put("answerContent", answer.getAnswerContent());
                    answerData.put("answerType", answer.getAnswerType());
                    answerData.put("isPrimary", answer.getIsPrimary());
                    answerData.put("sortOrder", answer.getSortOrder());
                    answerList.add(answerData);
                }
                
                questionData.put("answers", answerList);
            }
            
            questionList.add(questionData);
        }
        
        // 如果考试设置了题目乱序，则对题目进行乱序
        if (exam.getIsRandomOrder()) {
            questionList = shuffleQuestions(questionList, studentId);
        }
        
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
        response.put("questions", questionList);
        
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
