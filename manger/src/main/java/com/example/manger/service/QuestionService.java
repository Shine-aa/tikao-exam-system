package com.example.manger.service;

import com.example.manger.dto.PageResponse;
import com.example.manger.dto.QuestionRequest;
import com.example.manger.dto.QuestionResponse;
import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionCourse;
import com.example.manger.exception.BusinessException;
import com.example.manger.exception.ErrorCode;
import com.example.manger.repository.QuestionRepository;
import com.example.manger.repository.QuestionCourseRepository;
import com.example.manger.repository.TeacherCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final QuestionCourseRepository questionCourseRepository;
    private final TeacherCourseRepository teacherCourseRepository;
    
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

        QuestionCourse questionCourse = new QuestionCourse();
        questionCourse.setQuestionId(questionId);
        questionCourse.setCourseId(request.getCourseId());
        questionCourseRepository.save(questionCourse);
        
        return convertToResponse(question, request.getCourseId());
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

        // 保存更新后的question
        Long courseId = questionCourseRepository.findByQuestionIdAndIsActiveTrue(question.getId());
        question = questionRepository.save(question);
        if(!(courseId==request.getCourseId())) questionCourseRepository.deleteByQuestionIdAndCourseId(question.getId(), courseId);

        QuestionCourse questionCourse = new QuestionCourse();
        questionCourse.setQuestionId(question.getId());
        questionCourse.setCourseId(request.getCourseId());
        questionCourseRepository.save(questionCourse);
        
        return convertToResponse(question, request.getCourseId());
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
        Long courseId = questionCourseRepository.findByQuestionIdAndIsActiveTrue(id);
        return convertToResponse(question,courseId);
    }


    /**
     * 分页查询题目（支持按课程过滤）
     */
    public PageResponse<QuestionResponse> getQuestionsWithPagination(
            Question.QuestionType type,
            Question.DifficultyLevel difficulty,
            long courseId,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir,
            Long userId) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 1. 先查询符合所有条件的总条数（关键修正：先算总数）
        long filteredTotal;
        if (courseId == -1) {
            // 不过滤课程：直接查基础条件的总数
            filteredTotal = questionRepository.countByFilters(type, difficulty, keyword);
        } else {
            // 过滤课程：查“基础条件 + 属于该课程”的总数
            Set<Long> courseQuestionIds = questionCourseRepository.findByCourseIdAndIsActiveTrue(courseId)
                    .stream()
                    .map(QuestionCourse::getQuestionId)
                    .collect(Collectors.toSet());
            filteredTotal = questionRepository.countByFiltersAndIds(type, difficulty, keyword, courseQuestionIds);
        }

        // 2. 计算总页数（基于真实总数）
        int totalPages = filteredTotal == 0 ? 0 : (int) Math.ceil((double) filteredTotal / size);

        // 3. 查询当前页的数据（符合所有条件）
        List<Question> currentPageQuestions;
        if (courseId == -1) {
            // 不过滤课程：直接查分页数据
            currentPageQuestions = questionRepository.findByFilters(type, difficulty, keyword, pageable).getContent();
        } else {
            // 过滤课程：直接查询“基础条件+课程关联”的分页数据（关键修正）
            Set<Long> courseQuestionIds = questionCourseRepository.findByCourseIdAndIsActiveTrue(courseId)
                    .stream()
                    .map(QuestionCourse::getQuestionId)
                    .collect(Collectors.toSet());

            // 直接调用带课程ID过滤的分页查询，而不是先查基础数据再过滤
            currentPageQuestions = questionRepository.findByFiltersAndIds(
                            type, difficulty, keyword, courseQuestionIds, pageable)
                    .getContent();
        }


        // 4. 转换为响应DTO
        List<QuestionResponse> responses = currentPageQuestions.stream()
                .map(question -> convertToResponse(question, questionCourseRepository.findByQuestionIdAndIsActiveTrue(question.getId())))
                .collect(Collectors.toList());

        // 5. 构建分页响应（基于真实总数和总页数）
        return PageResponse.<QuestionResponse>builder()
                .content(responses)
                .page(page)
                .size(size)
                .total(filteredTotal) // 真实总条数
                .totalPages(totalPages) // 基于真实总数的总页数
                .first(page == 1)
                .last(page >= totalPages || totalPages == 0)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }


    /**
     * 分页查询题目（手动组卷）
     */
    public PageResponse<QuestionResponse> getQuestionsMannual(
            Question.QuestionType type, // 单个题型（原有参数，兼容旧逻辑）
            Question.DifficultyLevel difficulty, // 单个难度（原有参数，兼容旧逻辑）
            long courseId,
            String keyword,
            int page,
            int size,
            String sortBy,
            String sortDir,
            Long userId,
            List<Question.QuestionType> batchTypes, // 新增：批量题型（手动组卷用）
            List<Question.DifficultyLevel> batchDifficulties) { // 新增：批量难度（手动组卷用）

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        // 处理筛选参数：优先使用批量参数（手动组卷），无则使用单个参数（原有逻辑）
        List<Question.QuestionType> finalTypes = (batchTypes != null && !batchTypes.isEmpty()) ? batchTypes : (type != null ? List.of(type) : null);
        List<Question.DifficultyLevel> finalDifficulties = (batchDifficulties != null && !batchDifficulties.isEmpty()) ? batchDifficulties : (difficulty != null ? List.of(difficulty) : null);

        // 1. 获取当前课程下的所有有效题目ID（关联表过滤）
        Set<Long> courseQuestionIds = null;
        if (courseId != -1) {
            courseQuestionIds = questionCourseRepository.findByCourseIdAndIsActiveTrue(courseId)
                    .stream()
                    .map(QuestionCourse::getQuestionId)
                    .collect(Collectors.toSet());
        }

        // 2. 计算符合条件的总条数
        long filteredTotal;
        if (finalTypes != null || finalDifficulties != null || keyword != null || courseQuestionIds != null) {
            // 使用批量筛选方法计算总数
            filteredTotal = questionRepository.countByBatchFilters(
                    finalTypes,
                    finalDifficulties,
                    keyword,
                    courseQuestionIds);
        } else {
            // 无筛选条件：查询所有有效题目总数
            filteredTotal = questionRepository.countByIsActiveTrue();
        }

        // 3. 计算总页数
        int totalPages = filteredTotal == 0 ? 0 : (int) Math.ceil((double) filteredTotal / size);

        // 4. 查询当前页数据
        Page<Question> questionPage;
        if (finalTypes != null || finalDifficulties != null || keyword != null || courseQuestionIds != null) {
            // 使用批量筛选方法查询分页数据
            questionPage = questionRepository.findByBatchFilters(
                    finalTypes,
                    finalDifficulties,
                    keyword,
                    courseQuestionIds,
                    pageable);
        } else {
            // 无筛选条件：查询所有有效题目分页数据
            questionPage = questionRepository.findByIsActiveTrue(pageable);
        }

        // 5. 转换为响应DTO（复用原有逻辑）
        List<QuestionResponse> responses = questionPage.getContent().stream()
                .map(question -> convertToResponse(question, questionCourseRepository.findByQuestionIdAndIsActiveTrue(question.getId())))
                .collect(Collectors.toList());

        // 6. 构建分页响应
        return PageResponse.<QuestionResponse>builder()
                .content(responses)
                .page(page)
                .size(size)
                .total(filteredTotal)
                .totalPages(totalPages)
                .first(page == 1)
                .last(page >= totalPages || totalPages == 0)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }
    /**
     * 批量删除题目
     */
    @Transactional
    public void batchDeleteQuestions(List<Long> ids, Long userId) {
        List<Question> questions = questionRepository.findAllById(ids);
        
        //for (Question question : questions) {
        //    if (!question.getCreatedBy().equals(userId)) {
        //        throw new BusinessException(ErrorCode.ACCESS_DENIED, "无权限删除题目: " + question.getId());
        //    }
        //}
        
        // 删除题目本身
        questionRepository.deleteAll(questions);
    }

    /**
     * 获取教师管理课程下的题目统计信息
     */
    public QuestionStatistics getQuestionStatistics(Long teacherId) {
        // 1. 获取教师管理的所有课程ID
        List<Long> teacherCourseIds = teacherCourseRepository.findByTeacherIdAndIsActiveTrue(teacherId)
                .stream()
                .map(tc -> tc.getCourseId())
                .collect(Collectors.toList());

        if (teacherCourseIds.isEmpty()) {
            return new QuestionStatistics(0, 0, 0, 0, 0, 0, 0);
        }

        // 2. 通过中间表 QuestionCourse，找到这些课程下的所有题目ID
        Set<Long> questionIds = questionCourseRepository.findByCourseIdInAndIsActiveTrue(teacherCourseIds)
                .stream()
                .map(QuestionCourse::getQuestionId)
                .collect(Collectors.toSet());

        if (questionIds.isEmpty()) {
            return new QuestionStatistics(0, 0, 0, 0, 0, 0, 0);
        }

        // 3. 统计这些题目ID对应的题目数量（按类型区分）
        long totalQuestions = questionRepository.countByIdIn(questionIds);

        long singleChoiceCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.SINGLE_CHOICE);
        long multipleChoiceCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.MULTIPLE_CHOICE);
        long trueFalseCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.TRUE_FALSE);
        long fillBlankCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.FILL_BLANK);
        long subjectiveCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.SUBJECTIVE);
        long programmingCount = questionRepository.countByIdInAndType(
                questionIds, Question.QuestionType.PROGRAMMING);

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

    public Long getTotalOnMyManagement(Long teacherId){
        // 1. 获取教师管理的所有课程ID
        List<Long> teacherCourseIds = teacherCourseRepository.findByTeacherIdAndIsActiveTrue(teacherId)
                .stream()
                .map(tc -> tc.getCourseId())
                .collect(Collectors.toList());

        if (teacherCourseIds.isEmpty()) {
            return 0l;
        }

        // 2. 通过中间表 QuestionCourse，找到这些课程下的所有题目ID
        Set<Long> questionIds = questionCourseRepository.findByCourseIdInAndIsActiveTrue(teacherCourseIds)
                .stream()
                .map(QuestionCourse::getQuestionId)
                .collect(Collectors.toSet());

        if (questionIds.isEmpty()) {
            return 0l;
        }

        // 3. 统计这些题目ID对应的题目数量（按类型区分）
        long totalQuestions = questionRepository.countByIdIn(questionIds);

        return totalQuestions;
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
    private QuestionResponse convertToResponse(Question question,Long courseId) {
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

        response.setCourseId(courseId);
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
                    // 核心：兼容 Boolean/Integer/String 三种类型，覆盖 true/false、1/0、"1"/"0"、"true"/"false"
                    Object correctObj = optionMap.get("correct");
                    boolean isCorrect = false;

                    if (correctObj != null) {
                        // 场景1：原生 Boolean 类型（true/false）- 直接使用
                        if (correctObj instanceof Boolean) {
                            isCorrect = (Boolean) correctObj;
                        }
                        // 场景2：Integer 类型（1/0）- 1=正确，0=错误
                        else if (correctObj instanceof Integer) {
                            isCorrect = correctObj.equals(1);
                        }
                        // 场景3：String 类型（"1"/"0" 或 "true"/"false"，忽略大小写）
                        else if (correctObj instanceof String) {
                            String correctStr = ((String) correctObj).trim().toLowerCase();
                            isCorrect = "1".equals(correctStr) || "true".equals(correctStr);
                        }
                    }

                    optionResponse.setIsCorrect(isCorrect);
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
                .map(question -> convertToResponse(question, courseId))
                .collect(Collectors.toList());
    }
}
