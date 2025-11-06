package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.QuestionRequest;
import com.example.manger.dto.QuestionResponse;
import com.example.manger.entity.Question;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.QuestionRepository;
import com.example.manger.repository.QuestionCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final QuestionCourseRepository questionCourseRepository;
    
    /**
     * 创建题目
     */
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, Long userId) {
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setImages(request.getImages()); // 设置图片
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());
        // knowledge_point_id 已删除，改用 question_knowledge_points 关联表
        // question.setKnowledgePointId(request.getKnowledgePointId());
        question.setTags(request.getTags());
        question.setExplanation(request.getExplanation());
        // 设置编程语言（仅程序题）
        if (request.getType() == Question.QuestionType.PROGRAMMING) {
            // 如果提供了编程语言就使用，否则使用默认值 JAVA
            String programmingLanguage = request.getProgrammingLanguage();
            if (programmingLanguage == null || programmingLanguage.isEmpty()) {
                programmingLanguage = "JAVA"; // 默认值
            }
            question.setProgrammingLanguage(programmingLanguage);
            
            // 保存测试用例到Question的JSON字段（仅程序题）
            if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
                List<Map<String, Object>> testCases = request.getTestCases().stream()
                    .map(testCaseRequest -> {
                        Map<String, Object> testCase = new java.util.HashMap<>();
                        testCase.put("input", testCaseRequest.getInput() != null ? testCaseRequest.getInput() : "");
                        testCase.put("output", testCaseRequest.getOutput() != null ? testCaseRequest.getOutput() : "");
                        return testCase;
                    })
                    .collect(Collectors.toList());
                question.setTestCases(testCases);
            }
        }
        question.setCreatedBy(userId);
        
        question = questionRepository.save(question);
        final Long questionId = question.getId();
        
        // 保存选择题选项到Question的JSON字段
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<Map<String, Object>> options = request.getOptions().stream()
                .map(optionRequest -> {
                    Map<String, Object> option = new java.util.HashMap<>();
                    option.put("key", optionRequest.getOptionKey());
                    option.put("content", optionRequest.getOptionContent());
                    option.put("correct", optionRequest.getIsCorrect());
                    return option;
                })
                .collect(Collectors.toList());
            question.setOptions(options);
        }
        
        // 保存题目答案到Question的TEXT字段
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            // 合并所有答案内容
            String correctAnswer = request.getAnswers().stream()
                .map(QuestionRequest.QuestionAnswerRequest::getAnswerContent)
                .collect(Collectors.joining(";"));
            question.setCorrectAnswer(correctAnswer);
        }
        
        // 保存更新后的question
        question = questionRepository.save(question);
        
        return convertToResponse(question);
    }
    
    /**
     * 更新题目
     */
    @Transactional
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, Long userId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, "题目不存在"));
        
        // 检查权限
        if (!question.getCreatedBy().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限修改此题目");
        }
        
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setImages(request.getImages()); // 设置图片
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());
        // knowledge_point_id 已删除，改用 question_knowledge_points 关联表
        // question.setKnowledgePointId(request.getKnowledgePointId());
        question.setTags(request.getTags());
        question.setExplanation(request.getExplanation());
        // 设置编程语言（仅程序题）
        if (request.getType() == Question.QuestionType.PROGRAMMING) {
            // 如果提供了编程语言就使用，否则使用默认值 JAVA
            String programmingLanguage = request.getProgrammingLanguage();
            if (programmingLanguage == null || programmingLanguage.isEmpty()) {
                programmingLanguage = "JAVA"; // 默认值
            }
            question.setProgrammingLanguage(programmingLanguage);
            
            // 保存测试用例到Question的JSON字段（仅程序题）
            if (request.getTestCases() != null && !request.getTestCases().isEmpty()) {
                List<Map<String, Object>> testCases = request.getTestCases().stream()
                    .map(testCaseRequest -> {
                        Map<String, Object> testCase = new java.util.HashMap<>();
                        testCase.put("input", testCaseRequest.getInput() != null ? testCaseRequest.getInput() : "");
                        testCase.put("output", testCaseRequest.getOutput() != null ? testCaseRequest.getOutput() : "");
                        return testCase;
                    })
                    .collect(Collectors.toList());
                question.setTestCases(testCases);
            } else {
                // 如果没有提供测试用例，清空测试用例
                question.setTestCases(null);
            }
        } else if (request.getType() != Question.QuestionType.PROGRAMMING) {
            // 如果不是程序题，清空编程语言和测试用例字段
            question.setProgrammingLanguage(null);
            question.setTestCases(null);
        }
        
        // 保存选择题选项到Question的JSON字段
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<Map<String, Object>> options = request.getOptions().stream()
                .map(optionRequest -> {
                    Map<String, Object> option = new java.util.HashMap<>();
                    option.put("key", optionRequest.getOptionKey());
                    option.put("content", optionRequest.getOptionContent());
                    option.put("correct", optionRequest.getIsCorrect());
                    return option;
                })
                .collect(Collectors.toList());
            question.setOptions(options);
        } else {
            // 如果没有提供选项，清空选项
            question.setOptions(null);
        }
        
        // 保存题目答案到Question的TEXT字段
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            // 合并所有答案内容
            String correctAnswer = request.getAnswers().stream()
                .map(QuestionRequest.QuestionAnswerRequest::getAnswerContent)
                .collect(Collectors.joining(";"));
            question.setCorrectAnswer(correctAnswer);
        } else {
            // 如果没有提供答案，清空答案
            question.setCorrectAnswer(null);
        }
        
        question = questionRepository.save(question);
        
        return convertToResponse(question);
    }
    
    /**
     * 删除题目
     */
    @Transactional
    public void deleteQuestion(Long id, Long userId) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, "题目不存在"));
        
        // 检查权限
        if (!question.getCreatedBy().equals(userId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限删除此题目");
        }
        
        // 删除题目本身（选项和答案已存储在Question表中，会随题目一起删除）
        questionRepository.delete(question);
    }
    
    /**
     * 获取题目详情
     */
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.QUESTION_NOT_FOUND, "题目不存在"));
        
        return convertToResponse(question);
    }
    
    /**
     * 分页查询题目
     */
    public PageResponse<QuestionResponse> getQuestionsWithPagination(
            Question.QuestionType type,
            Question.DifficultyLevel difficulty,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir,
            Long userId) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<Question> questionPage = questionRepository.findByFilters(
            type, difficulty, keyword, userId, pageable);
        
        List<QuestionResponse> responses = questionPage.getContent().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        int totalPages = questionPage.getTotalPages();
        return PageResponse.<QuestionResponse>builder()
            .content(responses)
            .page(page)
            .size(size)
            .total(questionPage.getTotalElements())
            .totalPages(totalPages)
            .first(page == 1)
            .last(page == totalPages)
            .hasNext(page != totalPages)
            .hasPrevious(page != 1)
            .build();
    }
    
    /**
     * 批量删除题目
     */
    @Transactional
    public void batchDeleteQuestions(List<Long> ids, Long userId) {
        List<Question> questions = questionRepository.findAllById(ids);
        
        for (Question question : questions) {
            if (!question.getCreatedBy().equals(userId)) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限删除题目: " + question.getId());
            }
        }
        
        // 删除题目本身
        questionRepository.deleteAll(questions);
    }
    
    /**
     * 获取题目统计信息
     */
    public QuestionStatistics getQuestionStatistics(Long userId) {
        long totalQuestions = questionRepository.countByCreatedBy(userId);
        long singleChoiceCount = questionRepository.countByType(Question.QuestionType.SINGLE_CHOICE);
        long multipleChoiceCount = questionRepository.countByType(Question.QuestionType.MULTIPLE_CHOICE);
        long trueFalseCount = questionRepository.countByType(Question.QuestionType.TRUE_FALSE);
        long fillBlankCount = questionRepository.countByType(Question.QuestionType.FILL_BLANK);
        long subjectiveCount = questionRepository.countByType(Question.QuestionType.SUBJECTIVE);
        long programmingCount = questionRepository.countByType(Question.QuestionType.PROGRAMMING);
        
        return new QuestionStatistics(
            totalQuestions,
            singleChoiceCount,
            multipleChoiceCount,
            trueFalseCount,
            fillBlankCount,
            subjectiveCount,
            programmingCount
        );
    }
    
    /**
     * 题目统计信息内部类
     */
    public static class QuestionStatistics {
        public final long totalQuestions;
        public final long singleChoiceCount;
        public final long multipleChoiceCount;
        public final long trueFalseCount;
        public final long fillBlankCount;
        public final long subjectiveCount;
        public final long programmingCount;
        
        public QuestionStatistics(long totalQuestions, long singleChoiceCount, long multipleChoiceCount,
                                long trueFalseCount, long fillBlankCount, long subjectiveCount, long programmingCount) {
            this.totalQuestions = totalQuestions;
            this.singleChoiceCount = singleChoiceCount;
            this.multipleChoiceCount = multipleChoiceCount;
            this.trueFalseCount = trueFalseCount;
            this.fillBlankCount = fillBlankCount;
            this.subjectiveCount = subjectiveCount;
            this.programmingCount = programmingCount;
        }
    }
    
    /**
     * 转换为响应对象
     */
    private QuestionResponse convertToResponse(Question question) {
        QuestionResponse response = new QuestionResponse();
        response.setId(question.getId());
        response.setTitle(question.getTitle());
        response.setContent(question.getContent());
        response.setImages(question.getImages()); // 设置图片
        response.setType(question.getType());
        response.setTypeDescription(question.getType().getDescription());
        response.setDifficulty(question.getDifficulty());
        response.setDifficultyDescription(question.getDifficulty().getDescription());
        response.setPoints(question.getPoints());
        // knowledge_point_id 已删除，改用 question_knowledge_points 关联表
        // response.setKnowledgePointId(question.getKnowledgePointId());
        response.setTags(question.getTags());
        response.setExplanation(question.getExplanation());
        // 设置编程语言（仅程序题）
        response.setProgrammingLanguage(question.getProgrammingLanguage());
        
        // 从JSON字段读取测试用例数据（仅程序题）
        if (question.getTestCases() != null && !question.getTestCases().isEmpty()) {
            List<QuestionResponse.TestCaseResponse> testCaseResponses = question.getTestCases().stream()
                .map(testCaseMap -> {
                    QuestionResponse.TestCaseResponse testCaseResponse = new QuestionResponse.TestCaseResponse();
                    testCaseResponse.setInput(String.valueOf(testCaseMap.get("input")));
                    testCaseResponse.setOutput(String.valueOf(testCaseMap.get("output")));
                    return testCaseResponse;
                })
                .collect(Collectors.toList());
            response.setTestCases(testCaseResponses);
        }
        
        response.setIsActive(question.getIsActive());
        response.setCreatedBy(question.getCreatedBy());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        
        // 从JSON字段读取选项数据（新方式）
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            List<QuestionResponse.QuestionOptionResponse> optionResponses = question.getOptions().stream()
                .map(optionMap -> {
                    QuestionResponse.QuestionOptionResponse optionResponse = new QuestionResponse.QuestionOptionResponse();
                    optionResponse.setOptionKey(String.valueOf(optionMap.get("key")));
                    optionResponse.setOptionContent(String.valueOf(optionMap.get("content")));
                    optionResponse.setIsCorrect(Boolean.valueOf(String.valueOf(optionMap.get("correct"))));
                    return optionResponse;
                })
                .collect(Collectors.toList());
            response.setOptions(optionResponses);
        }
        
        // 从TEXT字段读取答案数据（新方式）
        if (question.getCorrectAnswer() != null && !question.getCorrectAnswer().isEmpty()) {
            List<QuestionResponse.QuestionAnswerResponse> answerResponses = Arrays.stream(question.getCorrectAnswer().split(";"))
                .map(answerContent -> {
                    QuestionResponse.QuestionAnswerResponse answerResponse = new QuestionResponse.QuestionAnswerResponse();
                    answerResponse.setAnswerContent(answerContent.trim());
                    return answerResponse;
                })
                .collect(Collectors.toList());
            response.setAnswers(answerResponses);
        }
        
        return response;
    }
    
    /**
     * 根据课程ID获取题目列表
     */
    public List<QuestionResponse> getQuestionsByCourseId(Long courseId) {
        List<Question> questions = questionRepository.findByCourseIdAndIsActiveTrue(courseId);
        return questions.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
}
