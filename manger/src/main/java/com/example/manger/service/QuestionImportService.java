package com.example.manger.service;

import com.example.manger.entity.Question;
import com.example.manger.entity.QuestionCourse;
import com.example.manger.repository.QuestionCourseRepository;
import com.example.manger.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 题目导入服务
 */
@Service
@RequiredArgsConstructor
public class QuestionImportService {
    
    private final QuestionRepository questionRepository;
    private final QuestionCourseRepository questionCourseRepository;
    
    /**
     * 导入Excel文件中的题目
     */
    @Transactional
    public Map<String, Object> importQuestions(MultipartFile file, Long courseId,Long userId) throws IOException {
        List<Question> questions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过表头（第一行）
            int startRow = 1;
            
            for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                
                if (row == null) {
                    continue;
                }
                
                try {
                    Question question = parseQuestion(row, rowIndex + 1);
                    if (question != null) {
                        question.setCreatedBy(userId);
                        questions.add(question);
                    }
                } catch (Exception e) {
                    errors.add("第 " + (rowIndex + 1) + " 行: " + e.getMessage());
                }
            }
        }
        
        // 批量保存
        if (!questions.isEmpty()) {
            questionRepository.saveAll(questions);
        }
        for(Question question:questions){
            QuestionCourse questionCourse = new QuestionCourse();
            questionCourse.setQuestionId(question.getId());
            questionCourse.setCourseId(courseId);
            questionCourseRepository.save(questionCourse);
        }

        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", questions.size());
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        
        return result;
    }
    
    /**
     * 解析单行题目数据
     */
    private Question parseQuestion(Row row, int rowNumber) throws Exception {
        Question question = new Question();
        
        // 题目类型 (必填)
        String typeStr = getCellValueAsString(row, 0);
        if (typeStr == null || typeStr.trim().isEmpty()) {
            throw new Exception("题目类型不能为空");
        }
        try {
            question.setType(Question.QuestionType.valueOf(typeStr.trim()));
        } catch (IllegalArgumentException e) {
            throw new Exception("无效的题目类型: " + typeStr);
        }
        
        // 题目内容 (必填)
        String content = getCellValueAsString(row, 1);
        if (content == null || content.trim().isEmpty()) {
            throw new Exception("题目内容不能为空");
        }
        question.setContent(content.trim());
        
        // 设置题目标题 (title 必填，用 content 作为标题)
        question.setTitle(content.trim());
        
        // 选择题需要解析选项和答案
        if (question.getType() == Question.QuestionType.SINGLE_CHOICE || 
            question.getType() == Question.QuestionType.MULTIPLE_CHOICE ||
            question.getType() == Question.QuestionType.TRUE_FALSE) {
            
            List<Map<String, Object>> options = new ArrayList<>();
            
            // 解析选项A-E
            String[] optionKeys = {"A", "B", "C", "D", "E"};
            for (int i = 0; i < optionKeys.length; i++) {
                String optionContent = getCellValueAsString(row, 2 + i);
                if (optionContent != null && !optionContent.trim().isEmpty()) {
                    Map<String, Object> option = new HashMap<>();
                    option.put("key", optionKeys[i]);
                    option.put("content", optionContent.trim());
                    option.put("correct", 0); // 默认不正确，后续根据正确答案设置
                    options.add(option);
                }
            }
            
            if (options.isEmpty()) {
                throw new Exception("选择题必须至少有一个选项");
            }
            
            question.setOptions(options);
            
            // 解析正确答案
            String correctAnswer = getCellValueAsString(row, 7);
            if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                throw new Exception("正确答案不能为空");
            }
            
            // 直接存储正确答案内容
            question.setCorrectAnswer(correctAnswer.trim());
            
        } else if (question.getType() == Question.QuestionType.FILL_BLANK) {
            // 填空题
            String correctAnswer = getCellValueAsString(row, 7);
            if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                throw new Exception("正确答案不能为空");
            }
            // 填空题多个答案用分号分隔
            question.setCorrectAnswer(correctAnswer.trim().replace(",", ";"));
            
        } else if (question.getType() == Question.QuestionType.SUBJECTIVE) {
            // 主观题
            String correctAnswer = getCellValueAsString(row, 7);
            if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                throw new Exception("参考答案不能为空");
            }
            question.setCorrectAnswer(correctAnswer.trim());
        } else if (question.getType() == Question.QuestionType.PROGRAMMING) {
            // 程序题
            String correctAnswer = getCellValueAsString(row, 7);
            if (correctAnswer == null || correctAnswer.trim().isEmpty()) {
                throw new Exception("参考答案（代码）不能为空");
            }
            question.setCorrectAnswer(correctAnswer.trim());
        }
        
        // 难度 (可选)
        String difficultyStr = getCellValueAsString(row, 8);
        if (difficultyStr != null && !difficultyStr.trim().isEmpty()) {
            try {
                question.setDifficulty(Question.DifficultyLevel.valueOf(difficultyStr.trim()));
            } catch (IllegalArgumentException e) {
                throw new Exception("无效的难度级别: " + difficultyStr);
            }
        } else {
            question.setDifficulty(Question.DifficultyLevel.MEDIUM); // 默认中等
        }
        
        // 分值 (可选)
        String pointsStr = getCellValueAsString(row, 9);
        if (pointsStr != null && !pointsStr.trim().isEmpty()) {
            try {
                question.setPoints(Integer.parseInt(pointsStr.trim()));
            } catch (NumberFormatException e) {
                throw new Exception("分值必须是数字");
            }
        } else {
            question.setPoints(1); // 默认1分
        }
        
        // 知识点 (可选)
        String knowledgePoints = getCellValueAsString(row, 10);
        // 这里可以扩展处理知识点关联
        
        // 标签 (可选)
        String tags = getCellValueAsString(row, 11);
        question.setTags(tags);
        
        // 答案解析 (可选)
        String explanation = getCellValueAsString(row, 12);
        question.setExplanation(explanation);
        
        // 检查Excel列数以确定是否有编程语言列（向后兼容）
        int lastCellNum = row.getLastCellNum();
        boolean hasProgrammingLanguageColumn = lastCellNum > 14; // 新格式有15列（索引0-14）
        
        // 编程语言 (可选，仅程序题使用)
        String programmingLanguage = null;
        String images = null;
        
        if (hasProgrammingLanguageColumn) {
            // 新格式：编程语言在第13列，图片路径在第14列
            programmingLanguage = getCellValueAsString(row, 13);
            images = getCellValueAsString(row, 14);
        } else {
            // 旧格式：没有编程语言列，图片路径在第13列
            images = getCellValueAsString(row, 13);
        }
        
        // 处理编程语言（仅程序题）
        if (question.getType() == Question.QuestionType.PROGRAMMING) {
            // 程序题：如果提供了编程语言就使用，否则使用默认值 JAVA
            if (programmingLanguage == null || programmingLanguage.trim().isEmpty()) {
                programmingLanguage = "JAVA"; // 默认值
            }
            question.setProgrammingLanguage(programmingLanguage.trim().toUpperCase());
        } else {
            // 非程序题：清空编程语言字段
            question.setProgrammingLanguage(null);
        }
        
        // 图片路径 (可选)
        question.setImages(images);
        
        // 设置默认值
        question.setIsActive(true);
        
        return question;
    }
    
    /**
     * 获取单元格值并转换为字符串
     */
    private String getCellValueAsString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 避免科学计数法
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}

