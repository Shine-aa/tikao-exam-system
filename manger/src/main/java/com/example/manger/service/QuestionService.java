package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.QuestionRequest;
import com.example.manger.dto.QuestionResponse;
import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionAnswer;
import com.example.manger.entity.QuestionOption;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.QuestionAnswerRepository;
import com.example.manger.repository.QuestionOptionRepository;
import com.example.manger.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    
    /**
     * 创建题目
     */
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, Long userId) {
        Question question = new Question();
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());
        question.setKnowledgePointId(request.getKnowledgePointId());
        question.setTags(request.getTags());
        question.setExplanation(request.getExplanation());
        question.setCreatedBy(userId);
        
        question = questionRepository.save(question);
        final Long questionId = question.getId();
        
        // 保存选择题选项
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<QuestionOption> options = request.getOptions().stream()
                .map(optionRequest -> {
                    QuestionOption option = new QuestionOption();
                    option.setQuestionId(questionId);
                    option.setOptionKey(optionRequest.getOptionKey());
                    option.setOptionContent(optionRequest.getOptionContent());
                    option.setIsCorrect(optionRequest.getIsCorrect());
                    option.setSortOrder(optionRequest.getSortOrder());
                    return option;
                })
                .collect(Collectors.toList());
            questionOptionRepository.saveAll(options);
        }
        
        // 保存题目答案
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            List<QuestionAnswer> answers = request.getAnswers().stream()
                .map(answerRequest -> {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setQuestionId(questionId);
                    answer.setAnswerContent(answerRequest.getAnswerContent());
                    answer.setAnswerType(answerRequest.getAnswerType());
                    answer.setIsPrimary(answerRequest.getIsPrimary());
                    answer.setSortOrder(answerRequest.getSortOrder());
                    return answer;
                })
                .collect(Collectors.toList());
            questionAnswerRepository.saveAll(answers);
        }
        
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
        question.setType(request.getType());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());
        question.setKnowledgePointId(request.getKnowledgePointId());
        question.setTags(request.getTags());
        question.setExplanation(request.getExplanation());
        
        question = questionRepository.save(question);
        final Long questionId = question.getId();
        
        // 删除原有选项和答案
        questionOptionRepository.deleteByQuestionId(id);
        questionAnswerRepository.deleteByQuestionId(id);
        
        // 保存新的选项和答案
        if (request.getOptions() != null && !request.getOptions().isEmpty()) {
            List<QuestionOption> options = request.getOptions().stream()
                .map(optionRequest -> {
                    QuestionOption option = new QuestionOption();
                    option.setQuestionId(questionId);
                    option.setOptionKey(optionRequest.getOptionKey());
                    option.setOptionContent(optionRequest.getOptionContent());
                    option.setIsCorrect(optionRequest.getIsCorrect());
                    option.setSortOrder(optionRequest.getSortOrder());
                    return option;
                })
                .collect(Collectors.toList());
            questionOptionRepository.saveAll(options);
        }
        
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            List<QuestionAnswer> answers = request.getAnswers().stream()
                .map(answerRequest -> {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setQuestionId(questionId);
                    answer.setAnswerContent(answerRequest.getAnswerContent());
                    answer.setAnswerType(answerRequest.getAnswerType());
                    answer.setIsPrimary(answerRequest.getIsPrimary());
                    answer.setSortOrder(answerRequest.getSortOrder());
                    return answer;
                })
                .collect(Collectors.toList());
            questionAnswerRepository.saveAll(answers);
        }
        
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
        
        // 删除关联的选项
        questionOptionRepository.deleteByQuestionId(id);
        
        // 删除关联的答案
        questionAnswerRepository.deleteByQuestionId(id);
        
        // 删除题目本身
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
            Long knowledgePointId,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir,
            Long userId) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        
        Page<Question> questionPage = questionRepository.findByFilters(
            type, difficulty, knowledgePointId, keyword, userId, pageable);
        
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
            
            // 删除关联的选项
            questionOptionRepository.deleteByQuestionId(question.getId());
            
            // 删除关联的答案
            questionAnswerRepository.deleteByQuestionId(question.getId());
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
        
        return new QuestionStatistics(
            totalQuestions,
            singleChoiceCount,
            multipleChoiceCount,
            trueFalseCount,
            fillBlankCount,
            subjectiveCount
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
        
        public QuestionStatistics(long totalQuestions, long singleChoiceCount, long multipleChoiceCount,
                                long trueFalseCount, long fillBlankCount, long subjectiveCount) {
            this.totalQuestions = totalQuestions;
            this.singleChoiceCount = singleChoiceCount;
            this.multipleChoiceCount = multipleChoiceCount;
            this.trueFalseCount = trueFalseCount;
            this.fillBlankCount = fillBlankCount;
            this.subjectiveCount = subjectiveCount;
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
        response.setType(question.getType());
        response.setTypeDescription(question.getType().getDescription());
        response.setDifficulty(question.getDifficulty());
        response.setDifficultyDescription(question.getDifficulty().getDescription());
        response.setPoints(question.getPoints());
        response.setKnowledgePointId(question.getKnowledgePointId());
        response.setTags(question.getTags());
        response.setExplanation(question.getExplanation());
        response.setIsActive(question.getIsActive());
        response.setCreatedBy(question.getCreatedBy());
        response.setCreatedAt(question.getCreatedAt());
        response.setUpdatedAt(question.getUpdatedAt());
        
        // 主动加载选项数据
        List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderBySortOrder(question.getId());
        if (options != null && !options.isEmpty()) {
            List<QuestionResponse.QuestionOptionResponse> optionResponses = options.stream()
                .map(option -> {
                    QuestionResponse.QuestionOptionResponse optionResponse = new QuestionResponse.QuestionOptionResponse();
                    optionResponse.setId(option.getId());
                    optionResponse.setOptionKey(option.getOptionKey());
                    optionResponse.setOptionContent(option.getOptionContent());
                    optionResponse.setIsCorrect(option.getIsCorrect());
                    optionResponse.setSortOrder(option.getSortOrder());
                    return optionResponse;
                })
                .collect(Collectors.toList());
            response.setOptions(optionResponses);
        }
        
        // 主动加载答案数据
        List<QuestionAnswer> answers = questionAnswerRepository.findByQuestionIdOrderBySortOrder(question.getId());
        if (answers != null && !answers.isEmpty()) {
            List<QuestionResponse.QuestionAnswerResponse> answerResponses = answers.stream()
                .map(answer -> {
                    QuestionResponse.QuestionAnswerResponse answerResponse = new QuestionResponse.QuestionAnswerResponse();
                    answerResponse.setId(answer.getId());
                    answerResponse.setAnswerContent(answer.getAnswerContent());
                    answerResponse.setAnswerType(answer.getAnswerType());
                    answerResponse.setAnswerTypeDescription(answer.getAnswerType().getDescription());
                    answerResponse.setIsPrimary(answer.getIsPrimary());
                    answerResponse.setSortOrder(answer.getSortOrder());
                    return answerResponse;
                })
                .collect(Collectors.toList());
            response.setAnswers(answerResponses);
        }
        
        return response;
    }
}
