package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.PaperGenerationRequest;
import com.example.manger.dto.PaperResponse;
import com.example.manger.entity.*;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 试卷生成服务
 */
@Service
@RequiredArgsConstructor
public class PaperGenerationService {
    
    private final PaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final ClassRepository classRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    
    /**
     * 生成试卷
     */
    @Transactional
    public PaperResponse generatePaper(PaperGenerationRequest request, Long teacherId) {
        // 验证班级和课程是否存在
        com.example.manger.entity.Class classEntity = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLASS_NOT_FOUND, "班级不存在"));
        
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND, "课程不存在"));
        
        // 创建试卷
        Paper paper = new Paper();
        paper.setPaperCode(generatePaperCode());
        paper.setPaperName(request.getPaperName());
        paper.setDescription(request.getDescription());
        paper.setClassId(request.getClassId());
        paper.setCourseId(request.getCourseId());
        paper.setTeacherId(teacherId);
        paper.setDurationMinutes(request.getDurationMinutes());
        paper.setTotalQuestions(request.getTotalQuestions());
        paper.setTotalPoints(request.getTotalPoints());
        paper.setDifficultyDistribution(convertMapToJson(request.getDifficultyDistribution()));
        paper.setQuestionTypeDistribution(convertMapToJson(request.getQuestionTypeDistribution()));
        paper.setIsActive(true);
        paper.setCreatedAt(LocalDateTime.now());
        
        Paper savedPaper = paperRepository.save(paper);
        
        // 智能选题
        List<Question> selectedQuestions = selectQuestions(request);
        
        // 保存试卷题目关联
        savePaperQuestions(savedPaper.getId(), selectedQuestions, request);
        
        // 更新试卷统计信息
        updatePaperStatistics(savedPaper.getId());
        
        return convertToResponse(savedPaper);
    }
    
    /**
     * 智能选题算法 - 按题型顺序组织试卷
     */
    private List<Question> selectQuestions(PaperGenerationRequest request) {
        System.out.println("Starting question selection for courseId: " + request.getCourseId());
        System.out.println("Total questions requested: " + request.getTotalQuestions());
        
        List<Question> selectedQuestions = new ArrayList<>();
        
        // 定义题型顺序：单选题 → 多选题 → 判断题 → 填空题 → 主观题
        String[] questionTypeOrder = {
            "SINGLE_CHOICE",    // 单选题
            "MULTIPLE_CHOICE",  // 多选题
            "TRUE_FALSE",       // 判断题
            "FILL_BLANK",       // 填空题
            "SUBJECTIVE"        // 主观题
        };
        
        // 根据题型分布选题，按照固定顺序
        Map<String, Integer> typeDistribution = request.getQuestionTypeDistribution();
        System.out.println("Question type distribution: " + typeDistribution);
        
        if (typeDistribution != null && !typeDistribution.isEmpty()) {
            // 按照预定义的顺序选题
            for (String questionType : questionTypeOrder) {
                Integer count = typeDistribution.get(questionType);
                if (count != null && count > 0) {
                    System.out.println("Selecting " + count + " questions of type: " + questionType);
                    List<Question> questions = selectQuestionsByType(questionType, count, request);
                    selectedQuestions.addAll(questions);
                    System.out.println("Selected " + questions.size() + " " + questionType + " questions");
                }
            }
        } else {
            // 如果没有指定题型分布，按比例随机选择题目
            System.out.println("No type distribution specified, selecting random questions");
            selectedQuestions = selectRandomQuestions(request);
        }
        
        System.out.println("After type-based selection: " + selectedQuestions.size() + " questions");
        
        // 如果题目数量不足，补充随机题目
        if (selectedQuestions.size() < request.getTotalQuestions()) {
            int needMore = request.getTotalQuestions() - selectedQuestions.size();
            System.out.println("Need " + needMore + " more questions, selecting random ones");
            List<Question> additionalQuestions = selectRandomQuestions(request, needMore);
            selectedQuestions.addAll(additionalQuestions);
        }
        
        // 注意：这里不再随机打乱题目顺序，保持题型顺序
        // 每个题型内部的题目顺序可以随机，但题型之间的顺序保持固定
        
        List<Question> result = selectedQuestions.stream()
                .limit(request.getTotalQuestions())
                .collect(Collectors.toList());
        
        System.out.println("Final selected questions: " + result.size());
        return result;
    }
    
    /**
     * 根据题型选择题目
     */
    private List<Question> selectQuestionsByType(String questionType, Integer count, PaperGenerationRequest request) {
        try {
            // 构建查询条件
            Question.QuestionType type = Question.QuestionType.valueOf(questionType);
            List<Question> questions = questionRepository.findByTypeAndCourseIdAndIsActiveTrue(
                    type, 
                    request.getCourseId()
            );
            
            System.out.println("Found " + questions.size() + " questions for type: " + questionType + ", courseId: " + request.getCourseId());
            
            // 根据难度分布筛选
            if (request.getDifficultyDistribution() != null && !request.getDifficultyDistribution().isEmpty()) {
                questions = filterByDifficulty(questions, request.getDifficultyDistribution());
                System.out.println("After difficulty filtering: " + questions.size() + " questions");
            }
            
            // 随机选择指定数量的题目
            Collections.shuffle(questions);
            List<Question> result = questions.stream()
                    .limit(count)
                    .collect(Collectors.toList());
            
            System.out.println("Selected " + result.size() + " questions for type: " + questionType);
            return result;
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid question type: " + questionType);
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error selecting questions by type: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 随机选择题目
     */
    private List<Question> selectRandomQuestions(PaperGenerationRequest request) {
        return selectRandomQuestions(request, request.getTotalQuestions());
    }
    
    private List<Question> selectRandomQuestions(PaperGenerationRequest request, Integer count) {
        List<Question> questions = questionRepository.findByCourseIdAndIsActiveTrue(request.getCourseId());
        
        // 根据难度分布筛选
        if (request.getDifficultyDistribution() != null && !request.getDifficultyDistribution().isEmpty()) {
            questions = filterByDifficulty(questions, request.getDifficultyDistribution());
        }
        
        Collections.shuffle(questions);
        return questions.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据难度分布筛选题目
     */
    private List<Question> filterByDifficulty(List<Question> questions, Map<String, Integer> difficultyDistribution) {
        List<Question> filteredQuestions = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : difficultyDistribution.entrySet()) {
            String difficulty = entry.getKey();
            Integer count = entry.getValue();
            
            if (count > 0) {
                try {
                    Question.DifficultyLevel difficultyLevel = Question.DifficultyLevel.valueOf(difficulty);
                    List<Question> difficultyQuestions = questions.stream()
                            .filter(q -> q.getDifficulty() == difficultyLevel)
                            .limit(count)
                            .collect(Collectors.toList());
                    filteredQuestions.addAll(difficultyQuestions);
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid difficulty level: " + difficulty);
                }
            }
        }
        
        return filteredQuestions;
    }
    
    /**
     * 保存试卷题目关联
     */
    private void savePaperQuestions(Long paperId, List<Question> questions, PaperGenerationRequest request) {
        int totalPoints = request.getTotalPoints();
        int questionCount = questions.size();
        
        // 防止除零错误
        if (questionCount == 0) {
            return;
        }
        
        int basePoints = totalPoints / questionCount;
        int remainingPoints = totalPoints % questionCount;
        
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            PaperQuestion paperQuestion = new PaperQuestion();
            paperQuestion.setPaperId(paperId);
            paperQuestion.setQuestionId(question.getId());
            paperQuestion.setQuestionOrder(i + 1);
            
            // 分配分数，前remainingPoints个题目多分配1分
            int points = basePoints + (i < remainingPoints ? 1 : 0);
            paperQuestion.setPoints(points);
            
            paperQuestionRepository.save(paperQuestion);
        }
    }
    
    /**
     * 更新试卷统计信息
     */
    private void updatePaperStatistics(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷不存在"));
        
        long questionCount = paperQuestionRepository.countByPaperId(paperId);
        int totalPoints = paperQuestionRepository.findByPaperIdOrderByQuestionOrder(paperId)
                .stream()
                .mapToInt(PaperQuestion::getPoints)
                .sum();
        
        paper.setTotalQuestions((int) questionCount);
        paper.setTotalPoints(totalPoints);
        paperRepository.save(paper);
    }
    
    /**
     * 分页获取教师的试卷
     */
    public PageResponse<PaperResponse> getPapersWithPagination(Long teacherId, int page, int size, String keyword) {
        // 这里需要实现分页查询逻辑
        // 暂时返回空结果
        return PageResponse.of(new ArrayList<>(), page, size, 0L);
    }
    
    /**
     * 分页获取所有试卷
     */
    public PageResponse<PaperResponse> getAllPapersWithPagination(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<Paper> paperPage;
        
        System.out.println("Getting papers with page=" + page + ", size=" + size + ", keyword=" + keyword);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            paperPage = paperRepository.findByKeyword(keyword, pageable);
        } else {
            paperPage = paperRepository.findByIsActiveTrue(pageable);
        }
        
        System.out.println("Found " + paperPage.getTotalElements() + " papers, " + paperPage.getContent().size() + " in current page");
        
        List<PaperResponse> responses = paperPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        System.out.println("Converted to " + responses.size() + " responses");
        
        return PageResponse.of(responses, page, size, paperPage.getTotalElements());
    }
    
    /**
     * 根据ID获取试卷详情
     */
    public PaperResponse getPaperById(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷不存在"));
        return convertToResponse(paper);
    }
    
    /**
     * 删除试卷
     */
    @Transactional
    public void deletePaper(Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAPER_NOT_FOUND, "试卷不存在"));
        
        // 软删除试卷
        paper.setIsActive(false);
        paperRepository.save(paper);
        
        // 删除试卷题目关联
        paperQuestionRepository.deleteByPaperId(paperId);
    }
    
    /**
     * 转换为响应DTO
     */
    private PaperResponse convertToResponse(Paper paper) {
        PaperResponse response = new PaperResponse();
        BeanUtils.copyProperties(paper, response);
        
        // 设置班级名称
        classRepository.findById(paper.getClassId())
                .ifPresent(clazz -> response.setClassName(clazz.getClassName()));
        
        // 设置课程信息
        courseRepository.findById(paper.getCourseId())
                .ifPresent(course -> {
                    response.setCourseName(course.getCourseName());
                    response.setCourseCode(course.getCourseCode());
                });
        
        // 设置教师名称
        userRepository.findById(paper.getTeacherId())
                .ifPresent(teacher -> response.setTeacherName(teacher.getUsername()));
        
        // 解析JSON字段
        response.setDifficultyDistribution(parseJsonToMap(paper.getDifficultyDistribution()));
        response.setQuestionTypeDistribution(parseJsonToMap(paper.getQuestionTypeDistribution()));
        
        // 设置试卷题目详情
        response.setQuestions(getPaperQuestions(paper.getId()));
        
        return response;
    }
    
    /**
     * 获取试卷题目详情
     */
    private List<PaperResponse.PaperQuestionResponse> getPaperQuestions(Long paperId) {
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperIdOrderByQuestionOrder(paperId);
        
        return paperQuestions.stream().map(pq -> {
            Question question = questionRepository.findById(pq.getQuestionId()).orElse(null);
            if (question == null) {
                return null;
            }
            
            PaperResponse.PaperQuestionResponse questionResponse = new PaperResponse.PaperQuestionResponse();
            questionResponse.setQuestionId(question.getId());
            questionResponse.setQuestionTitle(question.getTitle());
            questionResponse.setQuestionContent(question.getContent());
            questionResponse.setQuestionType(question.getType().name());
            questionResponse.setDifficulty(question.getDifficulty().name());
            questionResponse.setPoints(pq.getPoints());
            questionResponse.setQuestionOrder(pq.getQuestionOrder());
            questionResponse.setTags(question.getTags());
            questionResponse.setExplanation(question.getExplanation());
            
            // 获取题目选项
            List<QuestionOption> options = questionOptionRepository.findByQuestionIdOrderBySortOrder(question.getId());
            List<PaperResponse.PaperQuestionResponse.QuestionOptionResponse> optionResponses = options.stream()
                    .map(option -> {
                        PaperResponse.PaperQuestionResponse.QuestionOptionResponse optionResponse = 
                                new PaperResponse.PaperQuestionResponse.QuestionOptionResponse();
                        optionResponse.setId(option.getId());
                        optionResponse.setOptionKey(option.getOptionKey());
                        optionResponse.setOptionContent(option.getOptionContent());
                        optionResponse.setIsCorrect(option.getIsCorrect());
                        optionResponse.setSortOrder(option.getSortOrder());
                        return optionResponse;
                    })
                    .collect(Collectors.toList());
            questionResponse.setOptions(optionResponses);
            
            // 获取题目答案
            List<QuestionAnswer> answers = questionAnswerRepository.findByQuestionIdOrderBySortOrder(question.getId());
            List<PaperResponse.PaperQuestionResponse.QuestionAnswerResponse> answerResponses = answers.stream()
                    .map(answer -> {
                        PaperResponse.PaperQuestionResponse.QuestionAnswerResponse answerResponse = 
                                new PaperResponse.PaperQuestionResponse.QuestionAnswerResponse();
                        answerResponse.setId(answer.getId());
                        answerResponse.setAnswerContent(answer.getAnswerContent());
                        answerResponse.setAnswerType(answer.getAnswerType().name());
                        answerResponse.setIsPrimary(answer.getIsPrimary());
                        answerResponse.setSortOrder(answer.getSortOrder());
                        return answerResponse;
                    })
                    .collect(Collectors.toList());
            questionResponse.setAnswers(answerResponses);
            
            return questionResponse;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
    
    /**
     * 生成试卷代码
     */
    private String generatePaperCode() {
        return "PAPER_" + System.currentTimeMillis();
    }
    
    /**
     * 将Map转换为JSON字符串
     */
    private String convertMapToJson(Map<String, Integer> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        
        // 手动构建JSON字符串
        StringBuilder json = new StringBuilder();
        json.append("{");
        
        boolean first = true;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":").append(entry.getValue());
            first = false;
        }
        
        json.append("}");
        return json.toString();
    }
    
    /**
     * 解析JSON字符串为Map
     */
    private Map<String, Integer> parseJsonToMap(String json) {
        Map<String, Integer> result = new HashMap<>();
        
        if (json == null || json.trim().isEmpty() || "{}".equals(json.trim())) {
            return result;
        }
        
        try {
            // 简单的JSON解析，移除大括号
            String content = json.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1);
            }
            
            if (content.isEmpty()) {
                return result;
            }
            
            // 分割键值对
            String[] pairs = content.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim();
                    try {
                        result.put(key, Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        // 忽略无效的数字
                    }
                }
            }
        } catch (Exception e) {
            // 解析失败，返回空Map
        }
        
        return result;
    }
}