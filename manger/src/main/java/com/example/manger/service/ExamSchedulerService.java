package com.example.manger.service;

import com.example.manger.entity.Exam;
import com.example.manger.repository.ExamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试定时任务服务
 * 负责自动结束超时的考试
 */
@Service
public class ExamSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(ExamSchedulerService.class);

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamService examService;

    /**
     * 定时检查并自动结束超时的考试
     * 每1分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒 = 60000毫秒
    @Transactional
    public void autoEndExpiredExams() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 查询需要结束的考试（状态为ONGOING且结束时间已过）
            List<Exam> examsToEnd = examRepository.findExamsToEnd(now);
            
            if (!examsToEnd.isEmpty()) {
                logger.info("发现 {} 场考试需要自动结束", examsToEnd.size());
                
                for (Exam exam : examsToEnd) {
                    try {
                        logger.info("自动结束考试: ID={}, 名称={}, 结束时间={}", 
                                exam.getId(), exam.getExamName(), exam.getEndTime());
                        
                        // 调用ExamService的endExam方法，会自动处理学生考试记录
                        examService.endExam(exam.getId());
                        
                        logger.info("考试 {} 已自动结束", exam.getId());
                    } catch (Exception e) {
                        logger.error("自动结束考试失败: ID={}, 错误: {}", exam.getId(), e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("定时检查超时考试时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 定时检查并自动开始已到开始时间的考试
     * 每1分钟执行一次
     */
    @Scheduled(fixedRate = 60000) // 60秒 = 60000毫秒
    @Transactional
    public void autoStartScheduledExams() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 查询需要开始的考试（状态为SCHEDULED且开始时间已到）
            List<Exam> examsToStart = examRepository.findExamsToStart(now);
            
            if (!examsToStart.isEmpty()) {
                logger.info("发现 {} 场考试需要自动开始", examsToStart.size());
                
                for (Exam exam : examsToStart) {
                    try {
                        logger.info("自动开始考试: ID={}, 名称={}, 开始时间={}", 
                                exam.getId(), exam.getExamName(), exam.getStartTime());
                        
                        examService.startExam(exam.getId());
                        
                        logger.info("考试 {} 已自动开始", exam.getId());
                    } catch (Exception e) {
                        logger.error("自动开始考试失败: ID={}, 错误: {}", exam.getId(), e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("定时检查开始考试时发生错误: {}", e.getMessage(), e);
        }
    }
}

