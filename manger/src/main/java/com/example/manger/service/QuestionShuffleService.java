package com.example.manger.service;

import com.example.manger.dto.PaperResponse;
import com.example.manger.entity.Exam;
import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionOption;
import com.example.manger.entity.QuestionAnswer;
import com.example.manger.repository.QuestionRepository;
import com.example.manger.repository.QuestionOptionRepository;
import com.example.manger.repository.QuestionAnswerRepository;
import com.example.manger.repository.ExamRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 题目乱序服务
 */
@Service
public class QuestionShuffleService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private QuestionOptionRepository questionOptionRepository;
    
    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;
    
    @Autowired
    private ExamRepository examRepository;
    
    /**
     * 为学生生成乱序的试卷
     */
    public PaperResponse generateShuffledPaperForStudent(Long examId, Long studentId, PaperResponse originalPaper) {
        Exam exam = getExamById(examId);
        if (exam == null) {
            throw new RuntimeException("考试不存在");
        }
        
        PaperResponse shuffledPaper = new PaperResponse();
        BeanUtils.copyProperties(originalPaper, shuffledPaper);
        
        // 获取试卷的所有题目
        List<PaperResponse.PaperQuestionResponse> questions = originalPaper.getQuestions();
        if (questions == null || questions.isEmpty()) {
            return shuffledPaper;
        }
        
        // 根据考试设置决定是否乱序
        List<PaperResponse.PaperQuestionResponse> shuffledQuestions = questions;
        
        if (exam.getIsRandomOrder()) {
            // 题目乱序
            shuffledQuestions = shuffleQuestions(questions, studentId);
        }
        
        if (exam.getIsRandomOptions()) {
            // 选项乱序
            shuffledQuestions = shuffleOptions(shuffledQuestions, studentId);
        }
        
        shuffledPaper.setQuestions(shuffledQuestions);
        return shuffledPaper;
    }
    
    /**
     * 题目乱序
     */
    private List<PaperResponse.PaperQuestionResponse> shuffleQuestions(
            List<PaperResponse.PaperQuestionResponse> questions, Long studentId) {
        
        // 按题型分组
        Map<String, List<PaperResponse.PaperQuestionResponse>> questionsByType = questions.stream()
                .collect(Collectors.groupingBy(PaperResponse.PaperQuestionResponse::getQuestionType));
        
        List<PaperResponse.PaperQuestionResponse> shuffledQuestions = new ArrayList<>();
        
        // 定义题型顺序
        String[] questionTypeOrder = {
            "SINGLE_CHOICE",    // 单选题
            "MULTIPLE_CHOICE",  // 多选题
            "TRUE_FALSE",       // 判断题
            "FILL_BLANK",       // 填空题
            "SUBJECTIVE"        // 主观题
        };
        
        // 按固定顺序处理每种题型
        for (String questionType : questionTypeOrder) {
            List<PaperResponse.PaperQuestionResponse> typeQuestions = questionsByType.get(questionType);
            if (typeQuestions != null && !typeQuestions.isEmpty()) {
                // 使用学生ID作为种子，确保同一学生每次看到相同的顺序
                Collections.shuffle(typeQuestions, new Random(studentId + questionType.hashCode()));
                shuffledQuestions.addAll(typeQuestions);
            }
        }
        
        // 重新设置题目序号
        for (int i = 0; i < shuffledQuestions.size(); i++) {
            shuffledQuestions.get(i).setQuestionOrder(i + 1);
        }
        
        return shuffledQuestions;
    }
    
    /**
     * 选项乱序
     */
    private List<PaperResponse.PaperQuestionResponse> shuffleOptions(
            List<PaperResponse.PaperQuestionResponse> questions, Long studentId) {
        
        for (PaperResponse.PaperQuestionResponse question : questions) {
            List<PaperResponse.PaperQuestionResponse.QuestionOptionResponse> options = question.getOptions();
            if (options != null && !options.isEmpty()) {
                // 使用学生ID和题目ID作为种子
                Collections.shuffle(options, new Random(studentId + question.getQuestionId().hashCode()));
                
                // 重新设置选项序号
                for (int i = 0; i < options.size(); i++) {
                    options.get(i).setSortOrder(i + 1);
                }
            }
        }
        
        return questions;
    }
    
    /**
     * 获取考试信息
     */
    private Exam getExamById(Long examId) {
        return examRepository.findById(examId).orElse(null);
    }
}
