/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 8.1.0 : Database - user_auth_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`user_auth_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `user_auth_db`;

/*Table structure for table `class_courses` */

DROP TABLE IF EXISTS `class_courses`;

CREATE TABLE `class_courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `semester` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开课学期',
  `academic_year` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开课学年',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_course` (`class_id`,`course_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `class_courses_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
  CONSTRAINT `class_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级课程关联表';

/*Data for the table `class_courses` */

insert  into `class_courses`(`id`,`class_id`,`course_id`,`semester`,`academic_year`,`is_active`,`created_at`) values 
(1,1,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(2,1,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(3,1,3,'春季','2025-2026',1,'2025-10-15 09:42:15'),
(4,2,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(5,2,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(6,2,4,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(7,3,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(8,3,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(9,4,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(10,4,3,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(11,4,4,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(12,5,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(13,5,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(14,5,3,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(15,6,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(16,6,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(17,7,5,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(18,7,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(19,7,3,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(20,8,5,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(21,8,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(22,8,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(23,9,6,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(24,9,4,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(25,9,2,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(26,10,6,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(27,10,3,'2025春季','2024-2025',1,'2025-10-14 10:00:00'),
(28,10,1,'2025春季','2024-2025',1,'2025-10-14 10:00:00');

/*Table structure for table `classes` */

DROP TABLE IF EXISTS `classes`;

CREATE TABLE `classes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '班级ID',
  `class_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级代码',
  `class_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '年级（如：2022）',
  `major_id` bigint NOT NULL COMMENT '专业ID',
  `class_number` int NOT NULL COMMENT '班级序号（如：1班、2班）',
  `teacher_id` bigint NOT NULL COMMENT '班主任ID',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '班级描述',
  `max_students` int DEFAULT '50' COMMENT '最大学生数',
  `current_students` int DEFAULT '0' COMMENT '当前学生数',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_class_code` (`class_code`),
  UNIQUE KEY `uk_grade_major_class` (`grade`,`major_id`,`class_number`),
  KEY `idx_major_id` (`major_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_grade` (`grade`),
  CONSTRAINT `classes_ibfk_1` FOREIGN KEY (`major_id`) REFERENCES `majors` (`id`),
  CONSTRAINT `classes_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

/*Data for the table `classes` */

insert  into `classes`(`id`,`class_code`,`class_name`,`grade`,`major_id`,`class_number`,`teacher_id`,`description`,`max_students`,`current_students`,`is_active`,`created_at`,`updated_at`) values 
(1,'SE2022-01','2022级软件工程1班','2022',1,1,1,'2022级软件工程专业1班，培养软件开发能力',50,2,1,'2025-10-14 10:00:00','2025-10-17 08:54:40'),
(2,'SE2022-02','2022级软件工程2班','2022',1,2,1,'2022级软件工程专业2班，培养软件开发能力',45,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(3,'SE2023-01','2023级软件工程1班','2023',1,1,1,'2023级软件工程专业1班，培养软件开发能力',48,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(4,'CS2022-01','2022级计算机科学与技术1班','2022',2,1,1,'2022级计算机科学与技术专业1班，培养系统设计能力',48,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(5,'CS2022-02','2022级计算机科学与技术2班','2022',2,2,1,'2022级计算机科学与技术专业2班，培养系统设计能力',46,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(6,'CS2023-01','2023级计算机科学与技术1班','2023',2,1,1,'2023级计算机科学与技术专业1班，培养系统设计能力',50,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(7,'AI2022-01','2022级人工智能1班','2022',3,1,1,'2022级人工智能专业1班，培养AI算法和机器学习能力',40,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(8,'AI2022-02','2022级人工智能2班','2022',3,2,1,'2022级人工智能专业2班，培养AI算法和机器学习能力',42,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(9,'IS2022-01','2022级信息安全1班','2022',4,1,1,'2022级信息安全专业1班，培养网络安全和数据保护能力',38,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(10,'IS2022-02','2022级信息安全2班','2022',4,2,1,'2022级信息安全专业2班，培养网络安全和数据保护能力',40,0,1,'2025-10-14 10:00:00','2025-10-14 10:00:00');

/*Table structure for table `courses` */

DROP TABLE IF EXISTS `courses`;

CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程代码',
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '课程描述',
  `teacher_id` bigint NOT NULL COMMENT '授课教师ID',
  `semester` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学期',
  `academic_year` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学年',
  `credits` int DEFAULT '3' COMMENT '学分',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_code` (`course_code`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_semester` (`semester`),
  KEY `idx_academic_year` (`academic_year`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

/*Data for the table `courses` */

insert  into `courses`(`id`,`course_code`,`course_name`,`description`,`teacher_id`,`semester`,`academic_year`,`credits`,`is_active`,`created_at`,`updated_at`) values 
(1,'CS101','Java程序设计','Java编程基础课程，包括面向对象编程、集合框架、异常处理等内容',6,'2025春季','2024-2025',3,1,'2025-10-14 10:00:00','2025-10-15 10:33:10'),
(2,'CS102','数据结构与算法','数据结构与算法基础课程，包括线性表、树、图等数据结构和常用算法',6,'2025春季','2024-2025',4,1,'2025-10-14 10:00:00','2025-10-15 10:33:10'),
(3,'CS103','数据库原理','数据库系统原理课程，包括关系数据库、SQL语言、数据库设计等',6,'2025春季','2024-2025',3,1,'2025-10-14 10:00:00','2025-10-15 10:33:10'),
(4,'CS104','计算机网络','计算机网络基础课程，包括网络协议、网络编程、网络安全等',6,'2025春季','2024-2025',3,1,'2025-10-14 10:00:00','2025-10-15 10:33:10'),
(5,'AI101','机器学习','机器学习基础课程，包括监督学习、无监督学习、深度学习等',6,'2025春季','2024-2025',4,1,'2025-10-14 10:00:00','2025-10-15 10:33:10'),
(6,'IS101','密码学','密码学基础课程，包括对称加密、非对称加密、数字签名等',6,'2025春季','2024-2025',3,1,'2025-10-14 10:00:00','2025-10-15 10:33:10');

/*Table structure for table `exams` */

DROP TABLE IF EXISTS `exams`;

CREATE TABLE `exams` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '考试ID',
  `exam_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试代码',
  `exam_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '考试描述',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `teacher_id` bigint NOT NULL COMMENT '创建教师ID',
  `start_time` timestamp NOT NULL COMMENT '开始时间',
  `end_time` timestamp NOT NULL COMMENT '结束时间',
  `duration_minutes` int NOT NULL COMMENT '考试时长(分钟)',
  `max_attempts` int DEFAULT '1' COMMENT '最大尝试次数',
  `is_random_order` tinyint(1) DEFAULT '1' COMMENT '是否随机题目顺序',
  `is_random_options` tinyint(1) DEFAULT '1' COMMENT '是否随机选项顺序',
  `allow_review` tinyint(1) DEFAULT '1' COMMENT '是否允许查看答案',
  `status` enum('DRAFT','SCHEDULED','ONGOING','COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT' COMMENT '考试状态',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `student_count` int DEFAULT '0' COMMENT '应考人数（班级学生总数）',
  `participated_count` int DEFAULT '0' COMMENT '实考人数（实际参加考试的学生数）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_code` (`exam_code`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`id`),
  CONSTRAINT `exams_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
  CONSTRAINT `exams_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试表';

/*Data for the table `exams` */

insert  into `exams`(`id`,`exam_code`,`exam_name`,`description`,`paper_id`,`class_id`,`teacher_id`,`start_time`,`end_time`,`duration_minutes`,`max_attempts`,`is_random_order`,`is_random_options`,`allow_review`,`status`,`is_active`,`created_at`,`updated_at`,`student_count`,`participated_count`) values 
(1,'EXAM_1760581686303','阿瓦达伟大 - 考试','啊达瓦2',6,1,6,'2025-10-17 09:00:00','2025-10-17 11:00:00',120,1,1,1,0,'COMPLETED',1,'2025-10-16 10:28:06','2025-10-20 08:39:33',2,2),
(2,'EXAM_1760667541194','sfsfff - 考试','asd1',7,1,6,'2025-10-17 10:20:00','2025-10-17 12:20:00',120,1,1,1,0,'COMPLETED',1,'2025-10-17 10:19:01','2025-10-20 08:39:30',2,2),
(3,'EXAM_1760668637960','dawdawawfawf - 考试','',13,1,6,'2025-10-17 10:37:00','2025-10-17 12:37:00',120,1,1,1,0,'COMPLETED',1,'2025-10-17 10:37:18','2025-10-20 08:39:27',2,2);

/*Table structure for table `grading_results` */

DROP TABLE IF EXISTS `grading_results`;

CREATE TABLE `grading_results` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_exam_id` bigint NOT NULL COMMENT '学生考试记录ID',
  `exam_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '考试名称',
  `student_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学生姓名',
  `exam_start_time` datetime NOT NULL COMMENT '考试开始时间',
  `grading_data` json NOT NULL COMMENT '判卷结果数据，包含所有题目的分数和判分状态',
  `total_score` decimal(5,2) DEFAULT '0.00' COMMENT '总分',
  `is_grading_completed` tinyint(1) DEFAULT '0' COMMENT '是否判卷完成',
  `graded_at` datetime DEFAULT NULL COMMENT '判卷完成时间',
  `graded_by` bigint DEFAULT NULL COMMENT '判卷人ID',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student` (`exam_id`,`student_id`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_graded_by` (`graded_by`),
  KEY `idx_grading_completed` (`is_grading_completed`),
  KEY `student_exam_id` (`student_exam_id`),
  CONSTRAINT `grading_results_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grading_results_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grading_results_ibfk_3` FOREIGN KEY (`student_exam_id`) REFERENCES `student_exams` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grading_results_ibfk_4` FOREIGN KEY (`graded_by`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='判卷结果表';

/*Data for the table `grading_results` */

insert  into `grading_results`(`id`,`exam_id`,`student_id`,`student_exam_id`,`exam_name`,`student_name`,`exam_start_time`,`grading_data`,`total_score`,`is_grading_completed`,`graded_at`,`graded_by`,`created_at`,`updated_at`) values 
(1,1,2,1,'阿瓦达伟大 - 考试','user','2025-10-17 09:00:00','{\"10\": {\"isGraded\": true, \"givenScore\": 5.0}, \"11\": {\"isGraded\": true, \"givenScore\": 0.0}, \"15\": {\"isGraded\": true, \"givenScore\": 0.0}, \"16\": {\"isGraded\": true, \"givenScore\": 0.0}, \"20\": {\"isGraded\": true, \"givenScore\": 0.0}, \"26\": {\"isGraded\": true, \"givenScore\": 3.0}, \"30\": {\"isGraded\": true, \"givenScore\": 3.0}, \"31\": {\"isGraded\": true, \"givenScore\": 3.0}, \"32\": {\"isGraded\": true, \"givenScore\": 0.0}, \"33\": {\"isGraded\": true, \"givenScore\": 5.0}, \"34\": {\"isGraded\": true, \"givenScore\": 0.0}, \"35\": {\"isGraded\": true, \"givenScore\": 0.0}, \"36\": {\"isGraded\": true, \"givenScore\": 5.0}, \"37\": {\"isGraded\": true, \"givenScore\": 5.0}, \"38\": {\"isGraded\": true, \"givenScore\": 5.0}, \"39\": {\"isGraded\": true, \"givenScore\": 5.0}, \"40\": {\"isGraded\": true, \"givenScore\": 5.0}, \"42\": {\"isGraded\": true, \"givenScore\": 5.0}, \"45\": {\"isGraded\": true, \"givenScore\": 5.0}, \"46\": {\"isGraded\": true, \"givenScore\": 5.0}}',59.00,0,'2025-10-21 08:57:27',6,'2025-10-21 08:52:33','2025-10-21 08:58:09'),
(2,1,8,2,'阿瓦达伟大 - 考试','user123','2025-10-17 09:00:00','{\"10\": {\"isGraded\": true, \"givenScore\": 0.0}, \"11\": {\"isGraded\": true, \"givenScore\": 0.0}, \"15\": {\"isGraded\": true, \"givenScore\": 0.0}, \"16\": {\"isGraded\": true, \"givenScore\": 0.0}, \"20\": {\"isGraded\": true, \"givenScore\": 0.0}, \"26\": {\"isGraded\": true, \"givenScore\": 3.0}, \"30\": {\"isGraded\": true, \"givenScore\": 3.0}, \"31\": {\"isGraded\": true, \"givenScore\": 3.0}, \"32\": {\"isGraded\": true, \"givenScore\": 0.0}, \"33\": {\"isGraded\": true, \"givenScore\": 0.0}, \"34\": {\"isGraded\": true, \"givenScore\": 0.0}, \"35\": {\"isGraded\": true, \"givenScore\": 0.0}, \"36\": {\"isGraded\": true, \"givenScore\": 0.0}, \"37\": {\"isGraded\": false, \"givenScore\": null}, \"38\": {\"isGraded\": true, \"givenScore\": 5.0}, \"39\": {\"isGraded\": true, \"givenScore\": 0.0}, \"40\": {\"isGraded\": true, \"givenScore\": 0.0}, \"42\": {\"isGraded\": false, \"givenScore\": null}, \"45\": {\"isGraded\": false, \"givenScore\": null}, \"46\": {\"isGraded\": false, \"givenScore\": null}}',14.00,1,'2025-10-21 08:59:11',6,'2025-10-21 08:58:54','2025-10-21 08:59:11');

/*Table structure for table `knowledge_points` */

DROP TABLE IF EXISTS `knowledge_points`;

CREATE TABLE `knowledge_points` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识点名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '知识点描述',
  `parent_id` bigint DEFAULT NULL COMMENT '父知识点ID',
  `level` int DEFAULT '1' COMMENT '知识点层级',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点表';

/*Data for the table `knowledge_points` */

insert  into `knowledge_points`(`id`,`name`,`description`,`parent_id`,`level`,`sort_order`,`is_active`,`created_at`,`updated_at`) values 
(50,'计算机基础','计算机基础知识',NULL,1,1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(51,'编程语言','各种编程语言相关知识',NULL,1,2,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(52,'数据结构','数据结构与算法',NULL,1,3,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(53,'数据库','数据库相关知识',NULL,1,4,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(54,'网络技术','计算机网络相关知识',NULL,1,5,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(55,'软件工程','软件工程相关知识',NULL,1,6,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(56,'Java基础','Java编程语言基础',51,2,1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(57,'Python基础','Python编程语言基础',51,2,2,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(58,'C++基础','C++编程语言基础',51,2,3,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(59,'线性表','线性表数据结构',52,2,1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(60,'树结构','树形数据结构',52,2,2,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(61,'图结构','图形数据结构',52,2,3,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(62,'MySQL','MySQL数据库',53,2,1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(63,'Oracle','Oracle数据库',53,2,2,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(64,'Redis','Redis缓存数据库',53,2,3,1,'2025-10-14 09:03:48','2025-10-14 09:03:48');

/*Table structure for table `majors` */

DROP TABLE IF EXISTS `majors`;

CREATE TABLE `majors` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '专业ID',
  `major_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业代码',
  `major_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '专业名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '专业描述',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_major_code` (`major_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专业表';

/*Data for the table `majors` */

insert  into `majors`(`id`,`major_code`,`major_name`,`description`,`is_active`,`created_at`,`updated_at`) values 
(1,'SE','软件工程','软件工程专业，培养软件开发和系统设计能力',1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(2,'CS','计算机科学与技术','计算机科学与技术专业，培养计算机系统设计和应用开发能力',1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(3,'AI','人工智能','人工智能专业，培养AI算法和机器学习能力',1,'2025-10-14 10:00:00','2025-10-14 10:00:00'),
(4,'IS','信息安全','信息安全专业，培养网络安全和数据保护能力',1,'2025-10-14 10:00:00','2025-10-14 10:00:00');

/*Table structure for table `menus` */

DROP TABLE IF EXISTS `menus`;

CREATE TABLE `menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单代码',
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID',
  `menu_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'menu' COMMENT '菜单类型：menu-菜单，button-按钮',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

/*Data for the table `menus` */

insert  into `menus`(`id`,`menu_name`,`menu_code`,`parent_id`,`menu_type`,`path`,`component`,`icon`,`sort_order`,`is_active`,`description`,`create_time`,`update_time`) values 
(1,'钛考管理','system',NULL,'menu','/admin',NULL,'Setting',1,1,'钛考在线考试系统管理模块','2025-09-16 08:50:34','2025-09-30 09:22:50'),
(3,'仪表盘','dashboard',1,'menu','/admin/dashboard','admin/Dashboard','House',1,1,'系统仪表盘','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(4,'用户管理','user_management',1,'menu','/admin/users','admin/UserManagement','User',2,1,'用户管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(5,'角色管理','role_management',1,'menu','/admin/roles','admin/RoleManagement','UserFilled',3,1,'角色管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(6,'权限管理','permission_management',1,'menu','/admin/permissions','admin/PermissionManagement','Lock',4,1,'权限管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(7,'菜单管理','menu_management',1,'menu','/admin/menus','admin/MenuManagement','Menu',5,1,'菜单管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(10,'个人资料','user_profile',1,'menu','/user/profile','user/Profile','User',6,1,'个人资料管理','2025-09-16 08:50:34','2025-09-30 09:22:50'),
(11,'新增用户','user:create',4,'button',NULL,NULL,NULL,1,1,'新增用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(12,'编辑用户','user:edit',4,'button',NULL,NULL,NULL,2,1,'编辑用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(13,'删除用户','user:delete',4,'button',NULL,NULL,NULL,3,1,'删除用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(14,'新增角色','role:create',5,'button',NULL,NULL,NULL,1,1,'新增角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(15,'编辑角色','role:edit',5,'button',NULL,NULL,NULL,2,1,'编辑角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(16,'删除角色','role:delete',5,'button',NULL,NULL,NULL,3,1,'删除角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(17,'新增菜单','menu:create',7,'button',NULL,NULL,NULL,1,1,'新增菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(18,'编辑菜单','menu:edit',7,'button',NULL,NULL,NULL,2,1,'编辑菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(19,'删除菜单','menu:delete',7,'button',NULL,NULL,NULL,3,1,'删除菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00');

/*Table structure for table `paper_questions` */

DROP TABLE IF EXISTS `paper_questions`;

CREATE TABLE `paper_questions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `paper_id` bigint NOT NULL COMMENT '试卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `question_order` int NOT NULL COMMENT '题目顺序',
  `points` int NOT NULL COMMENT '题目分值',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_paper_question` (`paper_id`,`question_id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_question_id` (`question_id`),
  CONSTRAINT `paper_questions_ibfk_1` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`id`) ON DELETE CASCADE,
  CONSTRAINT `paper_questions_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷题目关联表';

/*Data for the table `paper_questions` */

insert  into `paper_questions`(`id`,`paper_id`,`question_id`,`question_order`,`points`,`created_at`) values 
(1,3,75,1,5,'2025-10-15 11:12:09'),
(2,3,67,2,5,'2025-10-15 11:12:09'),
(3,3,69,3,5,'2025-10-15 11:12:09'),
(4,3,53,4,5,'2025-10-15 11:12:09'),
(5,3,56,5,5,'2025-10-15 11:12:09'),
(6,3,74,6,5,'2025-10-15 11:12:09'),
(7,3,64,7,5,'2025-10-15 11:12:09'),
(8,3,60,8,5,'2025-10-15 11:12:09'),
(9,3,59,9,5,'2025-10-15 11:12:09'),
(10,3,54,10,5,'2025-10-15 11:12:09'),
(11,3,57,11,5,'2025-10-15 11:12:09'),
(12,3,48,12,5,'2025-10-15 11:12:09'),
(13,3,81,13,5,'2025-10-15 11:12:09'),
(14,3,80,14,5,'2025-10-15 11:12:09'),
(15,3,73,15,5,'2025-10-15 11:12:09'),
(16,3,70,16,5,'2025-10-15 11:12:09'),
(17,3,55,17,5,'2025-10-15 11:12:09'),
(18,3,68,18,5,'2025-10-15 11:12:09'),
(19,3,51,19,5,'2025-10-15 11:12:09'),
(20,3,50,20,5,'2025-10-15 11:12:09'),
(21,4,33,1,5,'2025-10-15 11:27:32'),
(22,4,28,2,5,'2025-10-15 11:27:32'),
(23,4,31,3,5,'2025-10-15 11:27:32'),
(24,4,26,4,5,'2025-10-15 11:27:32'),
(25,4,41,5,5,'2025-10-15 11:27:32'),
(26,4,8,6,5,'2025-10-15 11:27:32'),
(27,4,35,7,5,'2025-10-15 11:27:32'),
(28,4,46,8,5,'2025-10-15 11:27:32'),
(29,4,37,9,5,'2025-10-15 11:27:32'),
(30,4,10,10,5,'2025-10-15 11:27:32'),
(31,4,36,11,5,'2025-10-15 11:27:32'),
(32,4,32,12,5,'2025-10-15 11:27:32'),
(33,4,12,13,5,'2025-10-15 11:27:32'),
(34,4,45,14,5,'2025-10-15 11:27:32'),
(35,4,40,15,5,'2025-10-15 11:27:32'),
(36,4,15,16,5,'2025-10-15 11:27:32'),
(37,4,34,17,5,'2025-10-15 11:27:32'),
(38,4,16,18,5,'2025-10-15 11:27:32'),
(39,4,38,19,5,'2025-10-15 11:27:32'),
(40,4,42,20,5,'2025-10-15 11:27:32'),
(41,5,59,1,5,'2025-10-15 11:38:17'),
(42,5,53,2,5,'2025-10-15 11:38:17'),
(43,5,50,3,5,'2025-10-15 11:38:17'),
(44,5,48,4,5,'2025-10-15 11:38:17'),
(45,5,47,5,5,'2025-10-15 11:38:17'),
(46,5,70,6,5,'2025-10-15 11:38:17'),
(47,5,69,7,5,'2025-10-15 11:38:17'),
(48,5,66,8,5,'2025-10-15 11:38:17'),
(49,5,71,9,5,'2025-10-15 11:38:17'),
(50,5,64,10,5,'2025-10-15 11:38:17'),
(51,5,76,11,5,'2025-10-15 11:38:17'),
(52,5,73,12,5,'2025-10-15 11:38:17'),
(53,5,75,13,5,'2025-10-15 11:38:17'),
(54,5,72,14,5,'2025-10-15 11:38:17'),
(55,5,74,15,5,'2025-10-15 11:38:17'),
(56,5,79,16,5,'2025-10-15 11:38:17'),
(57,5,77,17,5,'2025-10-15 11:38:17'),
(58,5,84,18,5,'2025-10-15 11:38:17'),
(59,5,83,19,5,'2025-10-15 11:38:17'),
(60,5,82,20,5,'2025-10-15 11:38:17'),
(61,6,11,1,5,'2025-10-16 10:18:55'),
(62,6,20,2,5,'2025-10-16 10:18:55'),
(63,6,16,3,5,'2025-10-16 10:18:55'),
(64,6,15,4,5,'2025-10-16 10:18:55'),
(65,6,10,5,5,'2025-10-16 10:18:55'),
(66,6,26,6,5,'2025-10-16 10:18:55'),
(67,6,30,7,5,'2025-10-16 10:18:55'),
(68,6,31,8,5,'2025-10-16 10:18:55'),
(69,6,35,9,5,'2025-10-16 10:18:55'),
(70,6,33,10,5,'2025-10-16 10:18:55'),
(71,6,34,11,5,'2025-10-16 10:18:55'),
(72,6,36,12,5,'2025-10-16 10:18:55'),
(73,6,32,13,5,'2025-10-16 10:18:55'),
(74,6,39,14,5,'2025-10-16 10:18:55'),
(75,6,38,15,5,'2025-10-16 10:18:55'),
(76,6,37,16,5,'2025-10-16 10:18:55'),
(77,6,40,17,5,'2025-10-16 10:18:55'),
(78,6,45,18,5,'2025-10-16 10:18:55'),
(79,6,42,19,5,'2025-10-16 10:18:55'),
(80,6,46,20,5,'2025-10-16 10:18:55'),
(81,7,56,1,5,'2025-10-17 10:18:33'),
(82,7,59,2,5,'2025-10-17 10:18:33'),
(83,7,54,3,5,'2025-10-17 10:18:33'),
(84,7,48,4,5,'2025-10-17 10:18:33'),
(85,7,50,5,5,'2025-10-17 10:18:33'),
(86,7,71,6,5,'2025-10-17 10:18:33'),
(87,7,68,7,5,'2025-10-17 10:18:33'),
(88,7,66,8,5,'2025-10-17 10:18:33'),
(89,7,74,9,5,'2025-10-17 10:18:33'),
(90,7,72,10,5,'2025-10-17 10:18:33'),
(91,7,73,11,5,'2025-10-17 10:18:33'),
(92,7,75,12,5,'2025-10-17 10:18:33'),
(93,7,76,13,5,'2025-10-17 10:18:33'),
(94,7,81,14,5,'2025-10-17 10:18:33'),
(95,7,78,15,5,'2025-10-17 10:18:33'),
(96,7,80,16,5,'2025-10-17 10:18:33'),
(97,7,79,17,5,'2025-10-17 10:18:33'),
(98,7,84,18,5,'2025-10-17 10:18:33'),
(99,7,83,19,5,'2025-10-17 10:18:33'),
(100,7,85,20,5,'2025-10-17 10:18:33'),
(136,13,9,1,5,'2025-10-17 10:36:56'),
(137,13,15,2,5,'2025-10-17 10:36:56'),
(138,13,20,3,5,'2025-10-17 10:36:56'),
(139,13,12,4,5,'2025-10-17 10:36:56'),
(140,13,11,5,5,'2025-10-17 10:36:56'),
(141,13,17,6,5,'2025-10-17 10:36:56'),
(142,13,8,7,5,'2025-10-17 10:36:56'),
(143,13,22,8,5,'2025-10-17 10:36:56'),
(144,13,30,9,5,'2025-10-17 10:36:56'),
(145,13,29,10,5,'2025-10-17 10:36:56'),
(146,13,26,11,5,'2025-10-17 10:36:56'),
(147,13,27,12,5,'2025-10-17 10:36:56'),
(148,13,34,13,5,'2025-10-17 10:36:56'),
(149,13,35,14,5,'2025-10-17 10:36:56'),
(150,13,32,15,5,'2025-10-17 10:36:56'),
(151,13,37,16,5,'2025-10-17 10:36:56'),
(152,13,40,17,5,'2025-10-17 10:36:56'),
(153,13,46,18,5,'2025-10-17 10:36:56'),
(154,13,44,19,5,'2025-10-17 10:36:56'),
(155,13,42,20,5,'2025-10-17 10:36:56');

/*Table structure for table `papers` */

DROP TABLE IF EXISTS `papers`;

CREATE TABLE `papers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
  `paper_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '试卷代码',
  `paper_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '试卷名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '试卷描述',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `teacher_id` bigint NOT NULL COMMENT '创建教师ID',
  `total_questions` int NOT NULL DEFAULT '0' COMMENT '总题目数',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '总分',
  `duration_minutes` int NOT NULL DEFAULT '120' COMMENT '考试时长(分钟)',
  `difficulty_distribution` json DEFAULT NULL COMMENT '难度分布配置',
  `question_type_distribution` json DEFAULT NULL COMMENT '题型分布配置',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_paper_code` (`paper_code`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_teacher_id` (`teacher_id`),
  CONSTRAINT `papers_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`),
  CONSTRAINT `papers_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `papers_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷表';

/*Data for the table `papers` */

insert  into `papers`(`id`,`paper_code`,`paper_name`,`description`,`class_id`,`course_id`,`teacher_id`,`total_questions`,`total_points`,`duration_minutes`,`difficulty_distribution`,`question_type_distribution`,`is_active`,`created_at`,`updated_at`) values 
(3,'PAPER_1760497928873','艾斯但','啊达瓦',2,2,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 2, \"SUBJECTIVE\": 0, \"TRUE_FALSE\": 3, \"SINGLE_CHOICE\": 10, \"MULTIPLE_CHOICE\": 5}',1,'2025-10-15 11:12:09','2025-10-15 11:12:09'),
(4,'PAPER_1760498852204','啊达瓦','啊达瓦',3,1,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 4, \"SUBJECTIVE\": 3, \"TRUE_FALSE\": 5, \"SINGLE_CHOICE\": 5, \"MULTIPLE_CHOICE\": 3}',1,'2025-10-15 11:27:32','2025-10-15 11:27:32'),
(5,'PAPER_1760499497356','阿达','啊达瓦',4,2,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 2, \"SUBJECTIVE\": 3, \"TRUE_FALSE\": 5, \"SINGLE_CHOICE\": 5, \"MULTIPLE_CHOICE\": 5}',1,'2025-10-15 11:38:17','2025-10-15 11:38:17'),
(6,'PAPER_1760581135392','阿瓦达伟大','挖的',1,1,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 4, \"SUBJECTIVE\": 3, \"TRUE_FALSE\": 5, \"SINGLE_CHOICE\": 5, \"MULTIPLE_CHOICE\": 3}',1,'2025-10-16 10:18:55','2025-10-16 10:18:55'),
(7,'PAPER_1760667513310','sfsfff','awdawd',1,2,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 4, \"SUBJECTIVE\": 3, \"TRUE_FALSE\": 5, \"SINGLE_CHOICE\": 5, \"MULTIPLE_CHOICE\": 3}',1,'2025-10-17 10:18:33','2025-10-17 10:18:33'),
(13,'PAPER_1760668616263','dawdawawfawf','awdawdawd',1,1,6,20,100,120,'{\"EASY\": 5, \"HARD\": 5, \"MEDIUM\": 10}','{\"FILL_BLANK\": 2, \"SUBJECTIVE\": 3, \"TRUE_FALSE\": 3, \"SINGLE_CHOICE\": 7, \"MULTIPLE_CHOICE\": 5}',1,'2025-10-17 10:36:56','2025-10-17 10:36:56');

/*Table structure for table `permissions` */

DROP TABLE IF EXISTS `permissions`;

CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限代码',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源类型',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '权限描述',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_code` (`permission_code`),
  KEY `idx_permissions_permission_code` (`permission_code`),
  KEY `idx_permissions_resource_type` (`resource_type`),
  KEY `idx_permissions_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

/*Data for the table `permissions` */

insert  into `permissions`(`id`,`permission_name`,`permission_code`,`resource_type`,`action`,`description`,`is_active`,`create_time`) values 
(1,'查看系统配置','system:config:read','system','read','查看系统配置信息',1,'2025-09-04 08:46:45'),
(2,'修改系统配置','system:config:update','system','update','修改系统配置信息',1,'2025-09-04 08:46:45'),
(3,'查看系统日志','system:log:read','system','read','查看系统日志信息',1,'2025-09-04 08:46:45'),
(4,'创建用户','user:create','user','create','创建新用户',1,'2025-09-04 08:46:45'),
(5,'查看用户','user:read','user','read','查看用户信息',1,'2025-09-04 08:46:45'),
(6,'修改用户','user:update','user','update','修改用户信息',1,'2025-09-04 08:46:45'),
(7,'删除用户','user:delete','user','delete','删除用户',1,'2025-09-04 08:46:45'),
(8,'分配角色','user:role:assign','user','update','为用户分配角色',1,'2025-09-04 08:46:45'),
(9,'重置密码','user:password:reset','user','update','重置用户密码',1,'2025-09-04 08:46:45'),
(10,'创建角色','role:create','role','create','创建新角色',1,'2025-09-04 08:46:45'),
(11,'查看角色','role:read','role','read','查看角色信息',1,'2025-09-04 08:46:45'),
(12,'修改角色','role:update','role','update','修改角色信息',1,'2025-09-04 08:46:45'),
(13,'删除角色','role:delete','role','delete','删除角色',1,'2025-09-04 08:46:45'),
(14,'分配权限','role:permission:assign','role','update','为角色分配权限',1,'2025-09-04 08:46:45'),
(15,'导出数据','data:export','data','read','导出系统数据',1,'2025-09-04 08:46:45'),
(16,'导入数据','data:import','data','create','导入系统数据',1,'2025-09-04 08:46:45'),
(17,'数据备份','data:backup','data','read','备份系统数据',1,'2025-09-04 08:46:45'),
(18,'查看菜单','menu:read','menu','read','查看菜单信息',1,'2025-09-16 09:30:00'),
(19,'创建菜单','menu:create','menu','create','创建新菜单',1,'2025-09-16 09:30:00'),
(20,'修改菜单','menu:edit','menu','update','修改菜单信息',1,'2025-09-16 09:30:00'),
(21,'删除菜单','menu:delete','menu','delete','删除菜单',1,'2025-09-16 09:30:00');

/*Table structure for table `question_answers` */

DROP TABLE IF EXISTS `question_answers`;

CREATE TABLE `question_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `answer_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '答案内容',
  `answer_type` enum('TEXT','NUMBER','FORMULA') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'TEXT' COMMENT '答案类型',
  `is_primary` tinyint(1) DEFAULT '1' COMMENT '是否为主要答案',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_is_primary` (`is_primary`),
  CONSTRAINT `question_answers_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目答案表';

/*Data for the table `question_answers` */

insert  into `question_answers`(`id`,`question_id`,`answer_content`,`answer_type`,`is_primary`,`sort_order`,`created_at`) values 
(1,1,'class','TEXT',1,1,'2025-10-14 09:03:48'),
(2,2,'数组随机访问;哈希表查找;栈入栈操作','TEXT',1,1,'2025-10-14 09:03:48'),
(3,3,'对','TEXT',1,1,'2025-10-14 09:03:48'),
(4,4,'时间复杂度是算法执行时间随输入规模增长的变化趋势，通常用大O记号表示。空间复杂度是算法执行过程中所需存储空间随输入规模增长的变化趋势。','TEXT',1,1,'2025-10-14 09:03:48'),
(5,5,'final','TEXT',1,1,'2025-10-14 09:03:48'),
(6,6,'class;interface;implements;extends','TEXT',1,1,'2025-10-14 09:03:48'),
(7,7,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(8,8,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(9,9,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(10,10,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(11,11,'D','TEXT',1,1,'2025-10-15 10:00:00'),
(12,12,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(13,13,'D','TEXT',1,1,'2025-10-15 10:00:00'),
(14,14,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(15,15,'D','TEXT',1,1,'2025-10-15 10:00:00'),
(16,16,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(17,17,'D','TEXT',1,1,'2025-10-15 10:00:00'),
(18,18,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(19,19,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(20,20,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(21,21,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(22,22,'ACDE','TEXT',1,1,'2025-10-15 10:00:00'),
(23,23,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(24,24,'ABD','TEXT',1,1,'2025-10-15 10:00:00'),
(25,25,'ABCDE','TEXT',1,1,'2025-10-15 10:00:00'),
(26,26,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(27,27,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(28,28,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(29,29,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(30,30,'ABCDE','TEXT',1,1,'2025-10-15 10:00:00'),
(31,31,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(32,32,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(33,33,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(34,34,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(35,35,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(36,36,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(37,37,'final','TEXT',1,1,'2025-10-15 10:00:00'),
(38,38,'extends','TEXT',1,1,'2025-10-15 10:00:00'),
(39,39,'implements','TEXT',1,1,'2025-10-15 10:00:00'),
(40,40,'new','TEXT',1,1,'2025-10-15 10:00:00'),
(41,41,'abstract','TEXT',1,1,'2025-10-15 10:00:00'),
(42,42,'面向对象的三大特性是封装、继承、多态。封装是指将数据和方法包装在类中，隐藏内部实现细节；继承是指子类可以继承父类的属性和方法；多态是指同一接口可以有多种不同的实现方式。','TEXT',1,1,'2025-10-15 10:00:00'),
(43,43,'Java异常处理机制通过try-catch-finally实现。try块包含可能抛出异常的代码，catch块用于捕获和处理异常，finally块无论是否发生异常都会执行，通常用于资源清理。','TEXT',1,1,'2025-10-15 10:00:00'),
(44,44,'String是不可变的字符串类，一旦创建就不能修改；StringBuffer是线程安全的可变字符串类，适合多线程环境；StringBuilder是非线程安全的可变字符串类，性能更好但只能在单线程环境中使用。','TEXT',1,1,'2025-10-15 10:00:00'),
(45,45,'Java集合框架以Collection接口为根，List和Set是其子接口，Map是独立的接口。List接口的实现类有ArrayList、LinkedList等；Set接口的实现类有HashSet、TreeSet等；Map接口的实现类有HashMap、TreeMap等。','TEXT',1,1,'2025-10-15 10:00:00'),
(46,46,'Java垃圾回收机制是自动内存管理，当对象不再被任何引用变量引用时，垃圾回收器会自动回收其占用的内存。主要算法包括标记-清除算法、复制算法、标记-整理算法等，不同算法适用于不同的内存区域。','TEXT',1,1,'2025-10-15 10:00:00'),
(47,47,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(48,48,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(49,49,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(50,50,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(51,51,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(52,52,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(53,53,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(54,54,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(55,55,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(56,56,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(57,57,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(58,58,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(59,59,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(60,60,'D','TEXT',1,1,'2025-10-15 10:00:00'),
(61,61,'C','TEXT',1,1,'2025-10-15 10:00:00'),
(62,62,'ABCD','TEXT',1,1,'2025-10-15 10:00:00'),
(63,63,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(64,64,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(65,65,'AB','TEXT',1,1,'2025-10-15 10:00:00'),
(66,66,'BCDE','TEXT',1,1,'2025-10-15 10:00:00'),
(67,67,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(68,68,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(69,69,'AB','TEXT',1,1,'2025-10-15 10:00:00'),
(70,70,'ABC','TEXT',1,1,'2025-10-15 10:00:00'),
(71,71,'AB','TEXT',1,1,'2025-10-15 10:00:00'),
(72,72,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(73,73,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(74,74,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(75,75,'A','TEXT',1,1,'2025-10-15 10:00:00'),
(76,76,'B','TEXT',1,1,'2025-10-15 10:00:00'),
(77,77,'后进先出','TEXT',1,1,'2025-10-15 10:00:00'),
(78,78,'先进先出','TEXT',1,1,'2025-10-15 10:00:00'),
(79,79,'O(n log n)','TEXT',1,1,'2025-10-15 10:00:00'),
(80,80,'O(log n)','TEXT',1,1,'2025-10-15 10:00:00'),
(81,81,'O(1)','TEXT',1,1,'2025-10-15 10:00:00'),
(82,82,'快速排序采用分治法，选择一个基准元素，将数组分为小于基准和大于基准的两部分，然后递归排序。时间复杂度：最好情况O(n log n)，最坏情况O(n²)，平均情况O(n log n)。','TEXT',1,1,'2025-10-15 10:00:00'),
(83,83,'二叉搜索树的性质：左子树所有节点的值都小于根节点的值，右子树所有节点的值都大于根节点的值。查找操作：从根节点开始，比较目标值与当前节点值，如果相等则找到，如果小于则向左子树查找，如果大于则向右子树查找。','TEXT',1,1,'2025-10-15 10:00:00'),
(84,84,'动态规划的基本思想是将复杂问题分解为相互重叠的子问题，通过解决子问题来解决原问题，并存储子问题的解以避免重复计算。典型例子包括斐波那契数列、最长公共子序列、背包问题等。','TEXT',1,1,'2025-10-15 10:00:00'),
(85,85,'深度优先搜索（DFS）使用栈或递归实现，优先访问深层节点，空间复杂度为O(h)（h为树的高度）；广度优先搜索（BFS）使用队列实现，优先访问同层节点，空间复杂度为O(w)（w为树的宽度）。','TEXT',1,1,'2025-10-15 10:00:00'),
(86,86,'哈希表通过哈希函数将键映射到数组的索引位置，使用链表法或开放地址法处理哈希冲突。优点：查找、插入、删除的平均时间复杂度都是O(1)；缺点：需要额外的空间存储哈希表，可能出现哈希冲突影响性能。','TEXT',1,1,'2025-10-15 10:00:00');

/*Table structure for table `question_courses` */

DROP TABLE IF EXISTS `question_courses`;

CREATE TABLE `question_courses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_question_course` (`question_id`,`course_id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_course_id` (`course_id`),
  CONSTRAINT `question_courses_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `question_courses_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目课程关联表';

/*Data for the table `question_courses` */

insert  into `question_courses`(`id`,`question_id`,`course_id`,`is_active`,`created_at`,`updated_at`) values 
(1,7,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(2,8,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(3,9,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(4,10,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(5,11,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(6,12,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(7,13,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(8,14,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(9,15,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(10,16,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(11,17,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(12,18,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(13,19,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(14,20,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(15,21,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(16,22,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(17,23,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(18,24,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(19,25,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(20,26,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(21,27,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(22,28,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(23,29,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(24,30,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(25,31,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(26,32,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(27,33,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(28,34,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(29,35,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(30,36,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(31,37,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(32,38,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(33,39,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(34,40,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(35,41,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(36,42,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(37,43,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(38,44,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(39,45,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(40,46,1,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(41,47,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(42,48,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(43,49,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(44,50,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(45,51,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(46,52,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(47,53,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(48,54,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(49,55,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(50,56,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(51,57,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(52,58,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(53,59,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(54,60,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(55,61,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(56,62,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(57,63,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(58,64,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(59,65,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(60,66,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(61,67,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(62,68,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(63,69,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(64,70,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(65,71,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(66,72,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(67,73,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(68,74,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(69,75,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(70,76,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(71,77,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(72,78,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(73,79,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(74,80,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(75,81,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(76,82,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(77,83,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(78,84,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(79,85,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(80,86,2,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(81,1,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(82,2,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(83,3,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(84,4,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(85,5,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05'),
(86,6,3,1,'2025-10-15 11:09:05','2025-10-15 11:09:05');

/*Table structure for table `question_options` */

DROP TABLE IF EXISTS `question_options`;

CREATE TABLE `question_options` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `option_key` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项标识(A,B,C,D等)',
  `option_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '选项内容',
  `is_correct` tinyint(1) DEFAULT '0' COMMENT '是否为正确答案',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_is_correct` (`is_correct`),
  CONSTRAINT `question_options_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=273 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目选项表';

/*Data for the table `question_options` */

insert  into `question_options`(`id`,`question_id`,`option_key`,`option_content`,`is_correct`,`sort_order`,`created_at`) values 
(1,1,'A','class',1,1,'2025-10-14 09:03:48'),
(2,1,'B','Class',0,2,'2025-10-14 09:03:48'),
(3,1,'C','CLASS',0,3,'2025-10-14 09:03:48'),
(4,1,'D','class1',0,4,'2025-10-14 09:03:48'),
(5,2,'A','数组随机访问',1,1,'2025-10-14 09:03:48'),
(6,2,'B','哈希表查找',1,2,'2025-10-14 09:03:48'),
(7,2,'C','链表查找',0,3,'2025-10-14 09:03:48'),
(8,2,'D','栈入栈操作',1,4,'2025-10-14 09:03:48'),
(9,3,'A','对',1,1,'2025-10-14 09:03:48'),
(10,3,'B','错',0,2,'2025-10-14 09:03:48'),
(11,3,'C','',0,3,'2025-10-14 09:03:48'),
(12,3,'D','',0,4,'2025-10-14 09:03:48'),
(13,7,'A','var',0,1,'2025-10-15 10:00:00'),
(14,7,'B','const',0,2,'2025-10-15 10:00:00'),
(15,7,'C','final',1,3,'2025-10-15 10:00:00'),
(16,7,'D','static',0,4,'2025-10-15 10:00:00'),
(17,8,'A','//',1,1,'2025-10-15 10:00:00'),
(18,8,'B','/*',0,2,'2025-10-15 10:00:00'),
(19,8,'C','#',0,3,'2025-10-15 10:00:00'),
(20,8,'D','--',0,4,'2025-10-15 10:00:00'),
(21,9,'A','int',0,1,'2025-10-15 10:00:00'),
(22,9,'B','String',1,2,'2025-10-15 10:00:00'),
(23,9,'C','boolean',0,3,'2025-10-15 10:00:00'),
(24,9,'D','char',0,4,'2025-10-15 10:00:00'),
(25,10,'A','extends',1,1,'2025-10-15 10:00:00'),
(26,10,'B','implements',0,2,'2025-10-15 10:00:00'),
(27,10,'C','inherits',0,3,'2025-10-15 10:00:00'),
(28,10,'D','super',0,4,'2025-10-15 10:00:00'),
(29,11,'A','int[] arr',0,1,'2025-10-15 10:00:00'),
(30,11,'B','int arr[]',0,2,'2025-10-15 10:00:00'),
(31,11,'C','array int arr',0,3,'2025-10-15 10:00:00'),
(32,11,'D','A和B都对',1,4,'2025-10-15 10:00:00'),
(33,12,'A','extends',0,1,'2025-10-15 10:00:00'),
(34,12,'B','implements',1,2,'2025-10-15 10:00:00'),
(35,12,'C','interface',0,3,'2025-10-15 10:00:00'),
(36,12,'D','abstract',0,4,'2025-10-15 10:00:00'),
(37,13,'A','public',0,1,'2025-10-15 10:00:00'),
(38,13,'B','private',0,2,'2025-10-15 10:00:00'),
(39,13,'C','protected',0,3,'2025-10-15 10:00:00'),
(40,13,'D','以上都是',1,4,'2025-10-15 10:00:00'),
(41,14,'A','new',1,1,'2025-10-15 10:00:00'),
(42,14,'B','create',0,2,'2025-10-15 10:00:00'),
(43,14,'C','make',0,3,'2025-10-15 10:00:00'),
(44,14,'D','build',0,4,'2025-10-15 10:00:00'),
(45,15,'A','try-catch',0,1,'2025-10-15 10:00:00'),
(46,15,'B','throw',0,2,'2025-10-15 10:00:00'),
(47,15,'C','throws',0,3,'2025-10-15 10:00:00'),
(48,15,'D','以上都是',1,4,'2025-10-15 10:00:00'),
(49,16,'A','Vector',0,1,'2025-10-15 10:00:00'),
(50,16,'B','Hashtable',0,2,'2025-10-15 10:00:00'),
(51,16,'C','ArrayList',1,3,'2025-10-15 10:00:00'),
(52,16,'D','ConcurrentHashMap',0,4,'2025-10-15 10:00:00'),
(53,17,'A','override',0,1,'2025-10-15 10:00:00'),
(54,17,'B','overload',0,2,'2025-10-15 10:00:00'),
(55,17,'C','extends',0,3,'2025-10-15 10:00:00'),
(56,17,'D','不需要关键字',1,4,'2025-10-15 10:00:00'),
(57,18,'A','int',0,1,'2025-10-15 10:00:00'),
(58,18,'B','Integer',1,2,'2025-10-15 10:00:00'),
(59,18,'C','Int',0,3,'2025-10-15 10:00:00'),
(60,18,'D','INT',0,4,'2025-10-15 10:00:00'),
(61,19,'A','static',1,1,'2025-10-15 10:00:00'),
(62,19,'B','final',0,2,'2025-10-15 10:00:00'),
(63,19,'C','abstract',0,3,'2025-10-15 10:00:00'),
(64,19,'D','private',0,4,'2025-10-15 10:00:00'),
(65,20,'A','StringBuffer',0,1,'2025-10-15 10:00:00'),
(66,20,'B','StringBuilder',0,2,'2025-10-15 10:00:00'),
(67,20,'C','String',1,3,'2025-10-15 10:00:00'),
(68,20,'D','以上都是',0,4,'2025-10-15 10:00:00'),
(69,21,'A','abstract',1,1,'2025-10-15 10:00:00'),
(70,21,'B','interface',0,2,'2025-10-15 10:00:00'),
(71,21,'C','class',0,3,'2025-10-15 10:00:00'),
(72,21,'D','extends',0,4,'2025-10-15 10:00:00'),
(73,22,'A','int',1,1,'2025-10-15 10:00:00'),
(74,22,'B','String',0,2,'2025-10-15 10:00:00'),
(75,22,'C','boolean',1,3,'2025-10-15 10:00:00'),
(76,22,'D','char',1,4,'2025-10-15 10:00:00'),
(77,22,'E','double',1,5,'2025-10-15 10:00:00'),
(78,23,'A','public',1,1,'2025-10-15 10:00:00'),
(79,23,'B','private',1,2,'2025-10-15 10:00:00'),
(80,23,'C','protected',1,3,'2025-10-15 10:00:00'),
(81,23,'D','default',1,4,'2025-10-15 10:00:00'),
(82,23,'E','static',0,5,'2025-10-15 10:00:00'),
(83,24,'A','Vector',1,1,'2025-10-15 10:00:00'),
(84,24,'B','Hashtable',1,2,'2025-10-15 10:00:00'),
(85,24,'C','ArrayList',0,3,'2025-10-15 10:00:00'),
(86,24,'D','ConcurrentHashMap',1,4,'2025-10-15 10:00:00'),
(87,24,'E','HashMap',0,5,'2025-10-15 10:00:00'),
(88,25,'A','RuntimeException',1,1,'2025-10-15 10:00:00'),
(89,25,'B','IOException',1,2,'2025-10-15 10:00:00'),
(90,25,'C','NullPointerException',1,3,'2025-10-15 10:00:00'),
(91,25,'D','ClassNotFoundException',1,4,'2025-10-15 10:00:00'),
(92,25,'E','Error',1,5,'2025-10-15 10:00:00'),
(93,26,'A','for',1,1,'2025-10-15 10:00:00'),
(94,26,'B','while',1,2,'2025-10-15 10:00:00'),
(95,26,'C','do-while',1,3,'2025-10-15 10:00:00'),
(96,26,'D','foreach',1,4,'2025-10-15 10:00:00'),
(97,26,'E','loop',0,5,'2025-10-15 10:00:00'),
(98,27,'A','封装',1,1,'2025-10-15 10:00:00'),
(99,27,'B','继承',1,2,'2025-10-15 10:00:00'),
(100,27,'C','多态',1,3,'2025-10-15 10:00:00'),
(101,27,'D','抽象',1,4,'2025-10-15 10:00:00'),
(102,27,'E','重载',0,5,'2025-10-15 10:00:00'),
(103,28,'A','String',1,1,'2025-10-15 10:00:00'),
(104,28,'B','StringBuffer',1,2,'2025-10-15 10:00:00'),
(105,28,'C','StringBuilder',1,3,'2025-10-15 10:00:00'),
(106,28,'D','StringArray',0,4,'2025-10-15 10:00:00'),
(107,28,'E','StringList',0,5,'2025-10-15 10:00:00'),
(108,29,'A','List',1,1,'2025-10-15 10:00:00'),
(109,29,'B','Set',1,2,'2025-10-15 10:00:00'),
(110,29,'C','Map',1,3,'2025-10-15 10:00:00'),
(111,29,'D','Collection',1,4,'2025-10-15 10:00:00'),
(112,29,'E','Array',0,5,'2025-10-15 10:00:00'),
(113,30,'A','class',1,1,'2025-10-15 10:00:00'),
(114,30,'B','interface',1,2,'2025-10-15 10:00:00'),
(115,30,'C','abstract',1,3,'2025-10-15 10:00:00'),
(116,30,'D','final',1,4,'2025-10-15 10:00:00'),
(117,30,'E','static',1,5,'2025-10-15 10:00:00'),
(118,31,'A','Integer',1,1,'2025-10-15 10:00:00'),
(119,31,'B','Double',1,2,'2025-10-15 10:00:00'),
(120,31,'C','Boolean',1,3,'2025-10-15 10:00:00'),
(121,31,'D','Character',1,4,'2025-10-15 10:00:00'),
(122,31,'E','String',0,5,'2025-10-15 10:00:00'),
(123,32,'A','正确',1,1,'2025-10-15 10:00:00'),
(124,32,'B','错误',0,2,'2025-10-15 10:00:00'),
(125,32,'C','',0,3,'2025-10-15 10:00:00'),
(126,32,'D','',0,4,'2025-10-15 10:00:00'),
(127,33,'A','正确',1,1,'2025-10-15 10:00:00'),
(128,33,'B','错误',0,2,'2025-10-15 10:00:00'),
(129,33,'C','',0,3,'2025-10-15 10:00:00'),
(130,33,'D','',0,4,'2025-10-15 10:00:00'),
(131,34,'A','正确',1,1,'2025-10-15 10:00:00'),
(132,34,'B','错误',0,2,'2025-10-15 10:00:00'),
(133,34,'C','',0,3,'2025-10-15 10:00:00'),
(134,34,'D','',0,4,'2025-10-15 10:00:00'),
(135,35,'A','正确',0,1,'2025-10-15 10:00:00'),
(136,35,'B','错误',1,2,'2025-10-15 10:00:00'),
(137,35,'C','',0,3,'2025-10-15 10:00:00'),
(138,35,'D','',0,4,'2025-10-15 10:00:00'),
(139,36,'A','正确',1,1,'2025-10-15 10:00:00'),
(140,36,'B','错误',0,2,'2025-10-15 10:00:00'),
(141,36,'C','',0,3,'2025-10-15 10:00:00'),
(142,36,'D','',0,4,'2025-10-15 10:00:00'),
(143,47,'A','栈',0,1,'2025-10-15 10:00:00'),
(144,47,'B','队列',1,2,'2025-10-15 10:00:00'),
(145,47,'C','链表',0,3,'2025-10-15 10:00:00'),
(146,47,'D','树',0,4,'2025-10-15 10:00:00'),
(147,48,'A','队列',0,1,'2025-10-15 10:00:00'),
(148,48,'B','栈',1,2,'2025-10-15 10:00:00'),
(149,48,'C','链表',0,3,'2025-10-15 10:00:00'),
(150,48,'D','树',0,4,'2025-10-15 10:00:00'),
(151,49,'A','快速排序',0,1,'2025-10-15 10:00:00'),
(152,49,'B','归并排序',0,2,'2025-10-15 10:00:00'),
(153,49,'C','冒泡排序',1,3,'2025-10-15 10:00:00'),
(154,49,'D','堆排序',0,4,'2025-10-15 10:00:00'),
(155,50,'A','冒泡排序',0,1,'2025-10-15 10:00:00'),
(156,50,'B','选择排序',0,2,'2025-10-15 10:00:00'),
(157,50,'C','快速排序',1,3,'2025-10-15 10:00:00'),
(158,50,'D','插入排序',0,4,'2025-10-15 10:00:00'),
(159,51,'A','数组',0,1,'2025-10-15 10:00:00'),
(160,51,'B','链表',1,2,'2025-10-15 10:00:00'),
(161,51,'C','栈',0,3,'2025-10-15 10:00:00'),
(162,51,'D','队列',0,4,'2025-10-15 10:00:00'),
(163,52,'A','B树',0,1,'2025-10-15 10:00:00'),
(164,52,'B','B+树',0,2,'2025-10-15 10:00:00'),
(165,52,'C','二叉树',1,3,'2025-10-15 10:00:00'),
(166,52,'D','红黑树',0,4,'2025-10-15 10:00:00'),
(167,53,'A','深度优先搜索',0,1,'2025-10-15 10:00:00'),
(168,53,'B','广度优先搜索',0,2,'2025-10-15 10:00:00'),
(169,53,'C','Dijkstra算法',1,3,'2025-10-15 10:00:00'),
(170,53,'D','冒泡排序',0,4,'2025-10-15 10:00:00'),
(171,54,'A','数组',0,1,'2025-10-15 10:00:00'),
(172,54,'B','链表',0,2,'2025-10-15 10:00:00'),
(173,54,'C','堆',1,3,'2025-10-15 10:00:00'),
(174,54,'D','栈',0,4,'2025-10-15 10:00:00'),
(175,55,'A','线性搜索',0,1,'2025-10-15 10:00:00'),
(176,55,'B','二分搜索',0,2,'2025-10-15 10:00:00'),
(177,55,'C','哈希表查找',1,3,'2025-10-15 10:00:00'),
(178,55,'D','冒泡排序',0,4,'2025-10-15 10:00:00'),
(179,56,'A','树',0,1,'2025-10-15 10:00:00'),
(180,56,'B','图',0,2,'2025-10-15 10:00:00'),
(181,56,'C','数组',1,3,'2025-10-15 10:00:00'),
(182,56,'D','堆',0,4,'2025-10-15 10:00:00'),
(183,57,'A','深度优先搜索',1,1,'2025-10-15 10:00:00'),
(184,57,'B','广度优先搜索',0,2,'2025-10-15 10:00:00'),
(185,57,'C','快速排序',0,3,'2025-10-15 10:00:00'),
(186,57,'D','归并排序',0,4,'2025-10-15 10:00:00'),
(187,58,'A','数组',0,1,'2025-10-15 10:00:00'),
(188,58,'B','链表',1,2,'2025-10-15 10:00:00'),
(189,58,'C','栈',0,3,'2025-10-15 10:00:00'),
(190,58,'D','队列',0,4,'2025-10-15 10:00:00'),
(191,59,'A','快速排序',0,1,'2025-10-15 10:00:00'),
(192,59,'B','堆排序',0,2,'2025-10-15 10:00:00'),
(193,59,'C','归并排序',1,3,'2025-10-15 10:00:00'),
(194,59,'D','选择排序',0,4,'2025-10-15 10:00:00'),
(195,60,'A','深度优先搜索',0,1,'2025-10-15 10:00:00'),
(196,60,'B','广度优先搜索',0,2,'2025-10-15 10:00:00'),
(197,60,'C','拓扑排序',0,3,'2025-10-15 10:00:00'),
(198,60,'D','A和C都对',1,4,'2025-10-15 10:00:00'),
(199,61,'A','数组',0,1,'2025-10-15 10:00:00'),
(200,61,'B','链表',0,2,'2025-10-15 10:00:00'),
(201,61,'C','哈希表+双向链表',1,3,'2025-10-15 10:00:00'),
(202,61,'D','栈',0,4,'2025-10-15 10:00:00'),
(203,62,'A','数组',1,1,'2025-10-15 10:00:00'),
(204,62,'B','链表',1,2,'2025-10-15 10:00:00'),
(205,62,'C','栈',1,3,'2025-10-15 10:00:00'),
(206,62,'D','队列',1,4,'2025-10-15 10:00:00'),
(207,62,'E','树',0,5,'2025-10-15 10:00:00'),
(208,63,'A','快速排序',1,1,'2025-10-15 10:00:00'),
(209,63,'B','归并排序',1,2,'2025-10-15 10:00:00'),
(210,63,'C','堆排序',1,3,'2025-10-15 10:00:00'),
(211,63,'D','冒泡排序',0,4,'2025-10-15 10:00:00'),
(212,63,'E','选择排序',0,5,'2025-10-15 10:00:00'),
(213,64,'A','冒泡排序',1,1,'2025-10-15 10:00:00'),
(214,64,'B','插入排序',1,2,'2025-10-15 10:00:00'),
(215,64,'C','归并排序',1,3,'2025-10-15 10:00:00'),
(216,64,'D','快速排序',0,4,'2025-10-15 10:00:00'),
(217,64,'E','选择排序',0,5,'2025-10-15 10:00:00'),
(218,65,'A','深度优先搜索',1,1,'2025-10-15 10:00:00'),
(219,65,'B','广度优先搜索',1,2,'2025-10-15 10:00:00'),
(220,65,'C','Dijkstra算法',0,3,'2025-10-15 10:00:00'),
(221,65,'D','拓扑排序',0,4,'2025-10-15 10:00:00'),
(222,65,'E','快速排序',0,5,'2025-10-15 10:00:00'),
(223,66,'A','数组',0,1,'2025-10-15 10:00:00'),
(224,66,'B','链表',1,2,'2025-10-15 10:00:00'),
(225,66,'C','栈',1,3,'2025-10-15 10:00:00'),
(226,66,'D','队列',1,4,'2025-10-15 10:00:00'),
(227,66,'E','哈希表',1,5,'2025-10-15 10:00:00'),
(228,67,'A','连通',1,1,'2025-10-15 10:00:00'),
(229,67,'B','无环',1,2,'2025-10-15 10:00:00'),
(230,67,'C','有根',1,3,'2025-10-15 10:00:00'),
(231,67,'D','有向',0,4,'2025-10-15 10:00:00'),
(232,67,'E','无向',0,5,'2025-10-15 10:00:00'),
(233,68,'A','线性搜索',1,1,'2025-10-15 10:00:00'),
(234,68,'B','二分搜索',1,2,'2025-10-15 10:00:00'),
(235,68,'C','哈希查找',1,3,'2025-10-15 10:00:00'),
(236,68,'D','深度优先搜索',0,4,'2025-10-15 10:00:00'),
(237,68,'E','广度优先搜索',0,5,'2025-10-15 10:00:00'),
(238,69,'A','完全二叉树',1,1,'2025-10-15 10:00:00'),
(239,69,'B','父节点大于子节点',1,2,'2025-10-15 10:00:00'),
(240,69,'C','父节点小于子节点',0,3,'2025-10-15 10:00:00'),
(241,69,'D','平衡树',0,4,'2025-10-15 10:00:00'),
(242,69,'E','有序',0,5,'2025-10-15 10:00:00'),
(243,70,'A','查找速度快',1,1,'2025-10-15 10:00:00'),
(244,70,'B','插入速度快',1,2,'2025-10-15 10:00:00'),
(245,70,'C','删除速度快',1,3,'2025-10-15 10:00:00'),
(246,70,'D','有序',0,4,'2025-10-15 10:00:00'),
(247,70,'E','空间效率高',0,5,'2025-10-15 10:00:00'),
(248,71,'A','最优子结构',1,1,'2025-10-15 10:00:00'),
(249,71,'B','重叠子问题',1,2,'2025-10-15 10:00:00'),
(250,71,'C','贪心选择',0,3,'2025-10-15 10:00:00'),
(251,71,'D','分治',0,4,'2025-10-15 10:00:00'),
(252,71,'E','递归',0,5,'2025-10-15 10:00:00'),
(253,72,'A','正确',0,1,'2025-10-15 10:00:00'),
(254,72,'B','错误',1,2,'2025-10-15 10:00:00'),
(255,72,'C','',0,3,'2025-10-15 10:00:00'),
(256,72,'D','',0,4,'2025-10-15 10:00:00'),
(257,73,'A','正确',0,1,'2025-10-15 10:00:00'),
(258,73,'B','错误',1,2,'2025-10-15 10:00:00'),
(259,73,'C','',0,3,'2025-10-15 10:00:00'),
(260,73,'D','',0,4,'2025-10-15 10:00:00'),
(261,74,'A','正确',1,1,'2025-10-15 10:00:00'),
(262,74,'B','错误',0,2,'2025-10-15 10:00:00'),
(263,74,'C','',0,3,'2025-10-15 10:00:00'),
(264,74,'D','',0,4,'2025-10-15 10:00:00'),
(265,75,'A','正确',1,1,'2025-10-15 10:00:00'),
(266,75,'B','错误',0,2,'2025-10-15 10:00:00'),
(267,75,'C','',0,3,'2025-10-15 10:00:00'),
(268,75,'D','',0,4,'2025-10-15 10:00:00'),
(269,76,'A','正确',0,1,'2025-10-15 10:00:00'),
(270,76,'B','错误',1,2,'2025-10-15 10:00:00'),
(271,76,'C','',0,3,'2025-10-15 10:00:00'),
(272,76,'D','',0,4,'2025-10-15 10:00:00');

/*Table structure for table `question_tags` */

DROP TABLE IF EXISTS `question_tags`;

CREATE TABLE `question_tags` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
  `color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '#409EFF' COMMENT '标签颜色',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目标签表';

/*Data for the table `question_tags` */

insert  into `question_tags`(`id`,`name`,`color`,`description`,`is_active`,`created_at`,`updated_at`) values 
(23,'基础题','#67C23A','基础知识点题目',1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(24,'重点题','#E6A23C','重点知识点题目',1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(25,'难点题','#F56C6C','难点知识点题目',1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(26,'综合题','#909399','综合应用题目',1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(27,'真题','#409EFF','历年真题',1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(28,'模拟题','#9C27B0','模拟考试题目',1,'2025-10-14 09:03:48','2025-10-14 09:03:48');

/*Table structure for table `questions` */

DROP TABLE IF EXISTS `questions`;

CREATE TABLE `questions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目内容',
  `type` enum('SINGLE_CHOICE','MULTIPLE_CHOICE','TRUE_FALSE','FILL_BLANK','SUBJECTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题目类型',
  `difficulty` enum('EASY','MEDIUM','HARD') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'MEDIUM' COMMENT '难度等级',
  `points` int DEFAULT '1' COMMENT '题目分值',
  `knowledge_point_id` bigint DEFAULT NULL COMMENT '知识点ID',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签，多个用逗号分隔',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '题目解析',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用',
  `created_by` bigint NOT NULL COMMENT '创建人ID',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_difficulty` (`difficulty`),
  KEY `idx_knowledge_point` (`knowledge_point_id`),
  KEY `idx_created_by` (`created_by`),
  CONSTRAINT `questions_ibfk_1` FOREIGN KEY (`knowledge_point_id`) REFERENCES `knowledge_points` (`id`),
  CONSTRAINT `questions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表';

/*Data for the table `questions` */

insert  into `questions`(`id`,`title`,`content`,`type`,`difficulty`,`points`,`knowledge_point_id`,`tags`,`explanation`,`is_active`,`created_by`,`created_at`,`updated_at`) values 
(1,'Java基础语法','以下哪个是Java中的关键字？','SINGLE_CHOICE','EASY',2,56,'基础题,重点题','Java关键字是Java语言中预定义的具有特殊含义的标识符。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(2,'数据结构复杂度','以下哪种数据结构的时间复杂度是O(1)？','MULTIPLE_CHOICE','MEDIUM',3,59,'重点题,难点题','不同数据结构的时间复杂度不同，需要理解各种操作的时间复杂度。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(3,'数据库事务','数据库事务具有ACID特性。','TRUE_FALSE','EASY',1,62,'基础题','ACID是数据库事务的四个基本特性：原子性、一致性、隔离性、持久性。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(4,'算法复杂度','请简述时间复杂度和空间复杂度的概念。','SUBJECTIVE','HARD',5,52,'难点题,综合题','算法复杂度是衡量算法效率的重要指标。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(5,'Java语法填空','Java中用于声明常量的关键字是____。','FILL_BLANK','EASY',2,56,'基础题','final关键字用于声明常量，表示该变量的值不能被修改。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(6,'Java面向对象填空','Java中，____关键字用于定义类，____关键字用于定义接口，____关键字用于实现接口，____关键字用于继承类。','FILL_BLANK','MEDIUM',4,56,'基础题,重点题','Java面向对象编程的基本概念：class定义类，interface定义接口，implements实现接口，extends继承类。',1,1,'2025-10-14 09:03:48','2025-10-14 09:03:48'),
(7,'Java关键字','Java中，以下哪个关键字用于定义常量？','SINGLE_CHOICE','EASY',2,NULL,'基础题,重点题','final关键字用于定义常量，一旦赋值后不能修改',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(8,'Java注释','Java中，以下哪个是单行注释的语法？','SINGLE_CHOICE','EASY',2,NULL,'基础题','// 是Java中的单行注释语法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(9,'Java数据类型','Java中，以下哪个不是基本数据类型？','SINGLE_CHOICE','MEDIUM',2,NULL,'基础题,重点题','String是引用类型，不是基本数据类型',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(10,'Java继承','Java中，以下哪个关键字用于继承？','SINGLE_CHOICE','EASY',2,NULL,'基础题,重点题','extends关键字用于类的继承',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(11,'Java数组','Java中，以下哪个是数组的正确声明方式？','SINGLE_CHOICE','MEDIUM',2,NULL,'基础题','Java中数组的两种声明方式都是正确的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(12,'Java接口','Java中，以下哪个关键字用于实现接口？','SINGLE_CHOICE','EASY',2,NULL,'基础题,重点题','implements关键字用于实现接口',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(13,'Java访问修饰符','Java中，以下哪个是访问修饰符？','SINGLE_CHOICE','EASY',2,NULL,'基础题','public、private、protected都是Java的访问修饰符',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(14,'Java对象创建','Java中，以下哪个关键字用于创建对象？','SINGLE_CHOICE','EASY',2,NULL,'基础题','new关键字用于创建对象实例',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(15,'Java异常处理','Java中，以下哪个是异常处理的关键字？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','try-catch、throw、throws都是异常处理的关键字',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(16,'Java集合类','Java中，以下哪个集合类不是线程安全的？','SINGLE_CHOICE','HARD',2,NULL,'难点题','ArrayList不是线程安全的，其他都是线程安全的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(17,'Java方法重写','Java中，以下哪个关键字用于方法重写？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','Java中方法重写不需要关键字，只需要方法签名相同',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(18,'Java包装类','Java中，以下哪个是包装类？','SINGLE_CHOICE','EASY',2,NULL,'基础题','Integer是int的包装类',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(19,'Java静态方法','Java中，以下哪个关键字用于静态方法？','SINGLE_CHOICE','EASY',2,NULL,'基础题','static关键字用于定义静态方法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(20,'Java字符串','Java中，以下哪个是字符串的不可变类？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','String是不可变类，StringBuffer和StringBuilder是可变的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(21,'Java抽象类','Java中，以下哪个关键字用于抽象类？','SINGLE_CHOICE','EASY',2,NULL,'基础题,重点题','abstract关键字用于定义抽象类',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(22,'Java基本数据类型','Java中，以下哪些是基本数据类型？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'基础题,重点题','int、boolean、char、double都是基本数据类型，String是引用类型',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(23,'Java访问修饰符','Java中，以下哪些是访问修饰符？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','public、private、protected、default是访问修饰符，static不是',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(24,'Java线程安全集合','Java中，以下哪些集合类是线程安全的？','MULTIPLE_CHOICE','HARD',3,NULL,'难点题','Vector、Hashtable、ConcurrentHashMap是线程安全的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(25,'Java异常类型','Java中，以下哪些是异常类型？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','这些都是Java中的异常类型',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(26,'Java循环关键字','Java中，以下哪些关键字用于循环？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','for、while、do-while、foreach都是循环关键字',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(27,'Java面向对象特性','Java中，以下哪些是面向对象的特性？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','封装、继承、多态、抽象是面向对象的四大特性',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(28,'Java字符串类','Java中，以下哪些是字符串类？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','String、StringBuffer、StringBuilder是字符串相关类',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(29,'Java集合接口','Java中，以下哪些是集合接口？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','List、Set、Map、Collection是Java集合框架的主要接口',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(30,'Java关键字','Java中，以下哪些是关键字？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','这些都是Java的关键字',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(31,'Java包装类','Java中，以下哪些是包装类？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','Integer、Double、Boolean、Character是包装类，String不是',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(32,'Java继承限制','Java中，一个类只能继承一个父类。','TRUE_FALSE','EASY',2,NULL,'基础题','Java中一个类只能继承一个父类，但可以实现多个接口',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(33,'Java字符串不可变性','Java中，String类是不可变的。','TRUE_FALSE','EASY',2,NULL,'基础题','String类是不可变的，一旦创建就不能修改',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(34,'Java抽象类实例化','Java中，抽象类不能被实例化。','TRUE_FALSE','EASY',2,NULL,'基础题','抽象类不能被实例化，只能被继承',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(35,'Java接口方法实现','Java中，接口可以包含具体方法的实现。','TRUE_FALSE','MEDIUM',2,NULL,'重点题','在Java 8之前，接口只能包含抽象方法，不能有具体实现',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(36,'Java数组长度','Java中，数组的长度是固定的。','TRUE_FALSE','EASY',2,NULL,'基础题','Java中数组的长度在创建时就确定了，不能改变',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(37,'Java常量关键字','Java中，用于定义常量的关键字是______。','FILL_BLANK','EASY',2,NULL,'基础题','final关键字用于定义常量',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(38,'Java继承关键字','Java中，用于继承的关键字是______。','FILL_BLANK','EASY',2,NULL,'基础题','extends关键字用于类的继承',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(39,'Java接口实现关键字','Java中，用于实现接口的关键字是______。','FILL_BLANK','EASY',2,NULL,'基础题','implements关键字用于实现接口',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(40,'Java对象创建关键字','Java中，用于创建对象的关键字是______。','FILL_BLANK','EASY',2,NULL,'基础题','new关键字用于创建对象实例',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(41,'Java抽象类关键字','Java中，用于定义抽象类的关键字是______。','FILL_BLANK','EASY',2,NULL,'基础题','abstract关键字用于定义抽象类',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(42,'Java面向对象特性','请简述Java中面向对象的三大特性，并举例说明。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','面向对象的三大特性是封装、继承、多态。封装是指将数据和方法包装在类中，隐藏内部实现细节；继承是指子类可以继承父类的属性和方法；多态是指同一接口可以有多种不同的实现方式。',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(43,'Java异常处理机制','请说明Java中异常处理机制，并写出try-catch-finally的基本语法。','SUBJECTIVE','MEDIUM',5,NULL,'重点题','Java异常处理通过try-catch-finally实现，try块包含可能抛出异常的代码，catch块处理异常，finally块无论是否发生异常都会执行',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(44,'Java字符串类比较','请比较String、StringBuffer和StringBuilder的区别。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','String是不可变的，StringBuffer是线程安全的可变字符串，StringBuilder是非线程安全的可变字符串',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(45,'Java集合框架','请说明Java中集合框架的层次结构。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','Collection接口是根接口，List和Set是其子接口，Map是独立的接口，ArrayList、LinkedList、HashSet、HashMap等是具体实现类',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(46,'Java垃圾回收机制','请说明Java中垃圾回收机制的基本原理。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','Java垃圾回收器自动回收不再被引用的对象，主要算法有标记-清除、复制、标记-整理等',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(47,'FIFO数据结构','以下哪种数据结构是先进先出（FIFO）的？','SINGLE_CHOICE','EASY',2,NULL,'基础题','队列是先进先出的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(48,'LIFO数据结构','以下哪种数据结构是后进先出（LIFO）的？','SINGLE_CHOICE','EASY',2,NULL,'基础题','栈是后进先出的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(49,'冒泡排序复杂度','以下哪种排序算法的时间复杂度是O(n²)？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','冒泡排序的时间复杂度是O(n²)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(50,'快速排序复杂度','以下哪种排序算法的时间复杂度是O(n log n)？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','快速排序的平均时间复杂度是O(n log n)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(51,'链表特性','以下哪种数据结构不支持随机访问？','SINGLE_CHOICE','EASY',2,NULL,'基础题','链表不支持随机访问，只能顺序访问',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(52,'二叉树特性','以下哪种树的每个节点最多有两个子节点？','SINGLE_CHOICE','EASY',2,NULL,'基础题','二叉树的每个节点最多有两个子节点',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(53,'最短路径算法','以下哪种算法用于查找图中的最短路径？','SINGLE_CHOICE','HARD',2,NULL,'难点题','Dijkstra算法用于查找图中的最短路径',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(54,'优先队列实现','以下哪种数据结构用于实现优先队列？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','堆通常用于实现优先队列',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(55,'哈希表查找','以下哪种算法的时间复杂度是O(1)？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','哈希表查找的平均时间复杂度是O(1)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(56,'线性数据结构','以下哪种数据结构是线性的？','SINGLE_CHOICE','EASY',2,NULL,'基础题','数组是线性数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(57,'拓扑排序算法','以下哪种算法用于拓扑排序？','SINGLE_CHOICE','HARD',2,NULL,'难点题','深度优先搜索用于拓扑排序',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(58,'链表操作','以下哪种数据结构支持快速插入和删除？','SINGLE_CHOICE','EASY',2,NULL,'基础题','链表支持O(1)时间复杂度的插入和删除',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(59,'稳定排序','以下哪种排序算法是稳定的？','SINGLE_CHOICE','MEDIUM',2,NULL,'重点题','归并排序是稳定的排序算法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(60,'环检测算法','以下哪种算法用于检测图中是否有环？','SINGLE_CHOICE','HARD',2,NULL,'难点题','深度优先搜索和拓扑排序都可以检测环',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(61,'LRU缓存实现','以下哪种数据结构用于实现LRU缓存？','SINGLE_CHOICE','HARD',2,NULL,'难点题','LRU缓存通常用哈希表+双向链表实现',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(62,'线性数据结构','以下哪些是线性数据结构？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','数组、链表、栈、队列都是线性数据结构，树是非线性的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(63,'O(n log n)排序算法','以下哪些排序算法的时间复杂度是O(n log n)？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','快速排序、归并排序、堆排序的时间复杂度是O(n log n)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(64,'稳定排序算法','以下哪些是稳定的排序算法？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','冒泡排序、插入排序、归并排序是稳定的排序算法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(65,'图遍历算法','以下哪些是图的遍历算法？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','深度优先搜索和广度优先搜索是图的遍历算法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(66,'动态大小数据结构','以下哪些数据结构支持动态大小？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','链表、栈、队列、哈希表都支持动态大小，数组大小固定',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(67,'树的性质','以下哪些是树的性质？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','树是连通的、无环的、有根的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(68,'查找算法','以下哪些算法用于查找？','MULTIPLE_CHOICE','EASY',3,NULL,'基础题','线性搜索、二分搜索、哈希查找是查找算法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(69,'堆的性质','以下哪些是堆的性质？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','堆是完全二叉树，且父节点大于子节点（最大堆）',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(70,'哈希表优点','以下哪些是哈希表的优点？','MULTIPLE_CHOICE','MEDIUM',3,NULL,'重点题','哈希表查找、插入、删除都很快，但通常不保持有序',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(71,'动态规划特点','以下哪些是动态规划的特点？','MULTIPLE_CHOICE','HARD',3,NULL,'难点题','动态规划具有最优子结构和重叠子问题的特点',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(72,'栈的FIFO特性','栈是先进先出的数据结构。','TRUE_FALSE','EASY',2,NULL,'基础题','栈是后进先出的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(73,'快速排序稳定性','快速排序是稳定的排序算法。','TRUE_FALSE','MEDIUM',2,NULL,'重点题','快速排序不是稳定的排序算法',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(74,'二叉搜索树中序遍历','二叉搜索树的中序遍历是有序的。','TRUE_FALSE','MEDIUM',2,NULL,'重点题','二叉搜索树的中序遍历结果是有序的',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(75,'哈希表查找复杂度','哈希表的查找时间复杂度是O(1)。','TRUE_FALSE','MEDIUM',2,NULL,'重点题','哈希表的平均查找时间复杂度是O(1)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(76,'动态规划与贪心算法','动态规划总是比贪心算法更优。','TRUE_FALSE','HARD',2,NULL,'难点题','动态规划和贪心算法各有适用场景，不能简单比较',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(77,'栈的特性','栈是______进______出的数据结构。','FILL_BLANK','EASY',2,NULL,'基础题','栈是后进先出的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(78,'队列的特性','队列是______进______出的数据结构。','FILL_BLANK','EASY',2,NULL,'基础题','队列是先进先出的数据结构',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(79,'快速排序复杂度','快速排序的平均时间复杂度是______。','FILL_BLANK','MEDIUM',2,NULL,'重点题','快速排序的平均时间复杂度是O(n log n)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(80,'二叉搜索树查找复杂度','二叉搜索树的查找时间复杂度是______。','FILL_BLANK','MEDIUM',2,NULL,'重点题','二叉搜索树的查找时间复杂度是O(log n)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(81,'哈希表查找复杂度','哈希表的平均查找时间复杂度是______。','FILL_BLANK','MEDIUM',2,NULL,'重点题','哈希表的平均查找时间复杂度是O(1)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(82,'快速排序算法原理','请说明快速排序算法的原理，并分析其时间复杂度。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','快速排序采用分治法，选择一个基准元素，将数组分为两部分，递归排序。时间复杂度：最好O(n log n)，最坏O(n²)，平均O(n log n)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(83,'二叉搜索树性质','请说明二叉搜索树的性质，并说明如何进行查找操作。','SUBJECTIVE','MEDIUM',5,NULL,'重点题','二叉搜索树左子树所有节点值小于根节点，右子树所有节点值大于根节点。查找时从根节点开始，比较目标值与当前节点值，决定向左或向右查找',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(84,'动态规划基本思想','请说明动态规划的基本思想，并举例说明。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','动态规划将复杂问题分解为子问题，通过解决子问题来解决原问题，避免重复计算。例如：斐波那契数列、最长公共子序列等',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(85,'图遍历算法比较','请说明图的深度优先搜索和广度优先搜索的区别。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','DFS使用栈或递归，优先访问深层节点；BFS使用队列，优先访问同层节点。DFS空间复杂度O(h)，BFS空间复杂度O(w)',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00'),
(86,'哈希表实现原理','请说明哈希表的实现原理，并分析其优缺点。','SUBJECTIVE','HARD',5,NULL,'综合题,难点题','哈希表通过哈希函数将键映射到数组索引，使用链表或开放地址法处理冲突。优点：查找快O(1)；缺点：空间浪费，哈希冲突',1,1,'2025-10-15 10:00:00','2025-10-15 10:00:00');

/*Table structure for table `role_menus` */

DROP TABLE IF EXISTS `role_menus`;

CREATE TABLE `role_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`),
  CONSTRAINT `fk_role_menus_menu` FOREIGN KEY (`menu_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_menus_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

/*Data for the table `role_menus` */

insert  into `role_menus`(`id`,`role_id`,`menu_id`,`create_time`) values 
(1,1,1,'2025-09-16 08:50:34'),
(3,1,3,'2025-09-16 08:50:34'),
(4,1,4,'2025-09-16 08:50:34'),
(5,1,5,'2025-09-16 08:50:34'),
(6,1,6,'2025-09-16 08:50:34'),
(7,1,7,'2025-09-16 08:50:34'),
(10,1,10,'2025-09-16 08:50:34'),
(11,1,11,'2025-09-16 08:50:34'),
(12,1,12,'2025-09-16 08:50:34'),
(13,1,13,'2025-09-16 08:50:34'),
(14,1,14,'2025-09-16 08:50:34'),
(15,1,15,'2025-09-16 08:50:34'),
(16,1,16,'2025-09-16 08:50:34'),
(17,1,17,'2025-09-16 08:50:34'),
(18,1,18,'2025-09-16 08:50:34'),
(19,1,19,'2025-09-16 08:50:34'),
(34,6,10,'2025-09-16 08:50:34');

/*Table structure for table `role_permissions` */

DROP TABLE IF EXISTS `role_permissions`;

CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_permissions_role_id` (`role_id`),
  KEY `idx_role_permissions_permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

/*Data for the table `role_permissions` */

insert  into `role_permissions`(`id`,`role_id`,`permission_id`,`create_time`) values 
(147,1,2,'2025-09-15 09:15:53'),
(148,1,3,'2025-09-15 09:15:53'),
(149,1,8,'2025-09-15 09:15:53'),
(150,1,10,'2025-09-15 09:15:53'),
(151,1,13,'2025-09-15 09:15:53'),
(152,1,7,'2025-09-15 09:15:53'),
(153,1,6,'2025-09-15 09:15:53'),
(154,1,11,'2025-09-15 09:15:53'),
(155,1,4,'2025-09-15 09:15:53'),
(156,1,17,'2025-09-15 09:15:53'),
(157,1,9,'2025-09-15 09:15:53'),
(158,1,15,'2025-09-15 09:15:53'),
(159,1,12,'2025-09-15 09:15:53'),
(160,1,14,'2025-09-15 09:15:53'),
(161,1,16,'2025-09-15 09:15:53'),
(162,1,5,'2025-09-15 09:15:53'),
(163,1,1,'2025-09-15 09:15:53'),
(164,1,18,'2025-09-16 09:30:00'),
(165,1,19,'2025-09-16 09:30:00'),
(166,1,20,'2025-09-16 09:30:00'),
(167,1,21,'2025-09-16 09:30:00'),
(177,6,6,'2025-09-28 20:32:01'),
(178,6,17,'2025-09-28 20:32:01'),
(179,6,9,'2025-09-28 20:32:01'),
(180,6,15,'2025-09-28 20:32:01'),
(181,6,16,'2025-09-28 20:32:01'),
(182,6,5,'2025-09-28 20:32:01'),
(183,7,4,'2025-09-30 09:22:05'),
(184,7,17,'2025-09-30 09:22:05'),
(185,7,9,'2025-09-30 09:22:05'),
(186,7,15,'2025-09-30 09:22:05'),
(187,7,16,'2025-09-30 09:22:05'),
(188,7,5,'2025-09-30 09:22:05');

/*Table structure for table `roles` */

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色代码',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '角色描述',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`),
  KEY `idx_roles_role_code` (`role_code`),
  KEY `idx_roles_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

/*Data for the table `roles` */

insert  into `roles`(`id`,`role_name`,`role_code`,`description`,`is_active`,`create_time`,`update_time`) values 
(1,'超级管理员','SUPER_ADMIN','拥有系统所有权限的超级管理员',1,'2025-09-04 08:46:45','2025-09-15 09:15:53'),
(6,'普通用户','USER','普通用户',1,'2025-09-08 08:42:18','2025-09-28 20:32:02'),
(7,'教师','TEACHER','老师，可以管理普通用户',1,'2025-09-30 09:22:06','2025-09-30 09:22:06');

/*Table structure for table `sms_codes` */

DROP TABLE IF EXISTS `sms_codes`;

CREATE TABLE `sms_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码类型：register-注册, login-登录',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已使用',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  PRIMARY KEY (`id`),
  KEY `idx_phone_type` (`phone`,`type`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';

/*Data for the table `sms_codes` */

insert  into `sms_codes`(`id`,`phone`,`code`,`type`,`is_used`,`expire_time`,`create_time`,`used_time`,`ip_address`,`user_agent`) values 
(1,'17531307055','7983','register',0,'2025-09-08 10:48:08','2025-09-08 10:43:08',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(2,'17531307055','4913','register',0,'2025-09-08 10:49:25','2025-09-08 10:44:25',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(3,'17531307055','1710','register',0,'2025-09-08 10:50:26','2025-09-08 10:45:26',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(4,'17531307055','8907','login',0,'2025-09-08 10:51:27','2025-09-08 10:46:27',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(5,'17531307055','1912','login',0,'2025-09-08 10:52:28','2025-09-08 10:47:28',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(6,'17531307055','6346','login',0,'2025-09-08 10:55:19','2025-09-08 10:50:19',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(7,'17531307055','9074','login',0,'2025-09-08 10:58:29','2025-09-08 10:53:29',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(8,'17531307055','8607','register',0,'2025-09-08 11:01:57','2025-09-08 10:56:57',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(9,'17531307055','1078','login',0,'2025-09-08 11:02:57','2025-09-08 10:57:57',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(10,'17531307055','9542','register',0,'2025-09-28 20:39:58','2025-09-28 20:34:58',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(11,'17531307055','8303','register',0,'2025-09-30 08:46:27','2025-09-30 08:41:27',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0');

/*Table structure for table `student_answers` */

DROP TABLE IF EXISTS `student_answers`;

CREATE TABLE `student_answers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `student_exam_id` bigint NOT NULL COMMENT '学生考试记录ID',
  `answer_content` json DEFAULT NULL COMMENT '学生整张试卷的答案(JSON格式)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '总分',
  `is_graded` tinyint(1) DEFAULT '0' COMMENT '是否已评分',
  `graded_at` timestamp NULL DEFAULT NULL COMMENT '评分时间',
  `graded_by` bigint DEFAULT NULL COMMENT '评分人ID',
  PRIMARY KEY (`id`),
  KEY `idx_student_exam_id` (`student_exam_id`),
  CONSTRAINT `student_answers_ibfk_1` FOREIGN KEY (`student_exam_id`) REFERENCES `student_exams` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生答题记录表(整张试卷)';

/*Data for the table `student_answers` */

insert  into `student_answers`(`id`,`student_exam_id`,`answer_content`,`created_at`,`updated_at`,`total_score`,`is_graded`,`graded_at`,`graded_by`) values 
(1,1,'{\"answers\": {\"0\": [\"extends\"], \"1\": [\"int arr[]\"], \"2\": [\"Hashtable\"], \"3\": [\"StringBuffer\"], \"4\": [\"throw\"], \"5\": [\"Character\", \"Boolean\", \"Double\"], \"6\": [\"abstract\", \"interface\", \"final\", \"static\"], \"7\": [\"do-while\", \"while\", \"for\"], \"8\": [\"正确\"], \"9\": [\"错误\"], \"10\": [\"错误\"], \"11\": [\"正确\"], \"12\": [\"正确\"], \"13\": \"adqw1\", \"14\": \"awdawd\", \"15\": \"awd\", \"16\": \"dawd1\", \"17\": \"dw1dwa\", \"18\": \"dwadw1\", \"19\": \"awdw1\"}}','2025-10-17 10:13:26','2025-10-17 10:13:26',NULL,0,NULL,NULL),
(2,2,'{\"answers\": {\"0\": [\"Vector\"], \"1\": [\"array int arr\"], \"2\": [\"StringBuilder\"], \"3\": [\"throws\"], \"4\": [\"implements\"], \"5\": [\"interface\", \"static\", \"final\"], \"6\": [\"Boolean\", \"Character\", \"Double\"], \"7\": [\"while\", \"do-while\", \"for\"], \"8\": [\"错误\"], \"9\": [\"正确\"], \"10\": [\"错误\"], \"11\": [\"错误\"], \"12\": [\"错误\"]}}','2025-10-17 10:15:28','2025-10-17 10:15:28',NULL,0,NULL,NULL);

/*Table structure for table `student_classes` */

DROP TABLE IF EXISTS `student_classes`;

CREATE TABLE `student_classes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `enrolled_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_class` (`student_id`,`class_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_class_id` (`class_id`),
  CONSTRAINT `student_classes_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`),
  CONSTRAINT `student_classes_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `classes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生班级关联表';

/*Data for the table `student_classes` */

insert  into `student_classes`(`id`,`student_id`,`class_id`,`enrolled_at`,`is_active`,`created_at`) values 
(21,2,1,NULL,1,'2025-10-15 10:43:31'),
(23,8,1,NULL,1,'2025-10-17 08:54:40');

/*Table structure for table `student_exams` */

DROP TABLE IF EXISTS `student_exams`;

CREATE TABLE `student_exams` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `exam_id` bigint NOT NULL COMMENT '考试ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `attempt_number` int NOT NULL DEFAULT '1' COMMENT '尝试次数',
  `start_time` timestamp NULL DEFAULT NULL COMMENT '开始答题时间',
  `submit_time` timestamp NULL DEFAULT NULL COMMENT '提交时间',
  `total_score` decimal(5,2) DEFAULT NULL COMMENT '总分',
  `status` enum('NOT_STARTED','IN_PROGRESS','SUBMITTED','GRADED') COLLATE utf8mb4_unicode_ci DEFAULT 'NOT_STARTED' COMMENT '答题状态',
  `time_spent_minutes` int DEFAULT NULL COMMENT '用时(分钟)',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否有效',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_graded` tinyint(1) DEFAULT '0' COMMENT '是否已判卷',
  `graded_at` timestamp NULL DEFAULT NULL COMMENT '判卷时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_exam_student_attempt` (`exam_id`,`student_id`,`attempt_number`),
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_student_id` (`student_id`),
  CONSTRAINT `student_exams_ibfk_1` FOREIGN KEY (`exam_id`) REFERENCES `exams` (`id`),
  CONSTRAINT `student_exams_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学生考试记录表';

/*Data for the table `student_exams` */

insert  into `student_exams`(`id`,`exam_id`,`student_id`,`attempt_number`,`start_time`,`submit_time`,`total_score`,`status`,`time_spent_minutes`,`is_active`,`created_at`,`updated_at`,`is_graded`,`graded_at`) values 
(1,1,2,1,'2025-10-17 09:20:25','2025-10-17 10:13:26',59.00,'SUBMITTED',53,1,'2025-10-16 10:28:06','2025-10-21 08:53:29',0,NULL),
(2,1,8,1,'2025-10-17 10:15:04','2025-10-17 10:15:28',14.00,'SUBMITTED',0,1,'2025-10-16 10:28:06','2025-10-21 08:59:03',0,NULL),
(3,2,2,1,'2025-10-17 10:29:46',NULL,NULL,'IN_PROGRESS',NULL,1,'2025-10-17 10:19:01','2025-10-17 10:29:46',0,NULL),
(4,2,8,1,'2025-10-17 10:30:11',NULL,NULL,'IN_PROGRESS',NULL,1,'2025-10-17 10:19:01','2025-10-17 10:30:11',0,NULL),
(5,3,2,1,'2025-10-17 10:38:06',NULL,NULL,'IN_PROGRESS',NULL,1,'2025-10-17 10:37:18','2025-10-17 10:38:06',0,NULL),
(6,3,8,1,'2025-10-17 10:39:06',NULL,NULL,'IN_PROGRESS',NULL,1,'2025-10-17 10:37:18','2025-10-17 10:39:06',0,NULL);

/*Table structure for table `user_roles` */

DROP TABLE IF EXISTS `user_roles`;

CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_roles_user_id` (`user_id`),
  KEY `idx_user_roles_role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

/*Data for the table `user_roles` */

insert  into `user_roles`(`id`,`user_id`,`role_id`,`create_time`) values 
(1,1,1,'2025-09-04 08:46:45'),
(6,2,6,'2025-09-08 08:42:33'),
(10,8,6,'2025-09-30 08:41:39'),
(11,6,7,'2025-09-30 09:31:24');

/*Table structure for table `user_sessions` */

DROP TABLE IF EXISTS `user_sessions`;

CREATE TABLE `user_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话令牌',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '刷新令牌',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_session_token` (`session_token`(255)),
  KEY `idx_refresh_token` (`refresh_token`(255)),
  KEY `idx_expires_at` (`expires_at`),
  KEY `idx_is_active` (`is_active`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

/*Data for the table `user_sessions` */

insert  into `user_sessions`(`id`,`user_id`,`session_token`,`refresh_token`,`ip_address`,`user_agent`,`is_active`,`expires_at`,`create_time`) values 
(1,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTQ3MTMyLCJleHAiOjE3NTcwMzM1MzJ9.wopD_Lfeo55J3DbYc4PXFDweIyifbMu574LkxioVSiINawwJc6pBtw4JNXP4yW1q','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTQ3MTMyLCJleHAiOjE3NTk1MzkxMzJ9.wUSMvh-ImoZMnXiT4ZtfUXd3MCCQaw55qg2v6flEKMuBnyWHXI33P01D0klppSAA','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 08:52:13','2025-09-04 08:52:13'),
(2,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTUyNDIxLCJleHAiOjE3NTcwMzg4MjF9.g7MbPM1ugAPRL9KdkhCpkBi80JmEGdBQqhl7ps2ON_GT7SPWyNXuOEASZeQu6ylp','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTUyNDIxLCJleHAiOjE3NTk1NDQ0MjF9.kbgAAjIUI9vhxfUUBv5ELa3TcPflsZXQNftMrC2iUoTnai-xhlMfJcBmsGO5ZoqJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 10:20:22','2025-09-04 10:20:22'),
(3,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0MjQwLCJleHAiOjE3NTcwNDA2NDB9.VWce0Cfmwnc4mf_7wQk9QH2_Qfhb4wCdxXhRJOUspVNwh0EW2Hoa0yb4PDzTwqZt','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0MjQwLCJleHAiOjE3NTk1NDYyNDB9.zG-CF5PDmAYdBPeN743o0Bd1uozF5TNiv1JgzheDn_V_DJe_m8Xne3XN0e7silEU','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 10:50:41','2025-09-04 10:50:41'),
(5,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0OTgxLCJleHAiOjE3NTcwNDEzODF9.IASMeRBALJTyDZPEBsvxgScDKceXZy8Er0sYJgEKFCxDE7SxUSt2LABH0ivriG1Y','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0OTgxLCJleHAiOjE3NTk1NDY5ODF9.uHWP4xIUeHLAO5tVEoEQYl1xki-FFM7DG8aBY1vWW9XLz_u4a1ZRttl_exx-cR8c','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 11:03:02','2025-09-04 11:03:02'),
(7,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU1MDU2LCJleHAiOjE3NTcwNDE0NTZ9.7_JubNVcr9rCwp8TfsLCoVZsp2MJiSZWC3Uzpx1Gzgr8OzSghZgr4NuZ4tCdlYIE','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU1MDU2LCJleHAiOjE3NTk1NDcwNTZ9.iyTtsg-6Wxz7l-kqJmk3aUsP7vHZW6KIoEZhH0oU7zPdNRIjxXfhiL14mmxBQzvu','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 11:04:16','2025-09-04 11:04:16'),
(8,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MDM2ODI3LCJleHAiOjE3NTcxMjMyMjd9.7SZ9NRbigvX5KuhjOGP7poM7w2DccYW_5b9lj2O2znNh9ineoTqbDvdmwwYtJeA8','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MDM2ODI3LCJleHAiOjE3NTk2Mjg4Mjd9.cAvX5Kszc_lEdplzz7gS7yxXhqKv_ptTWs__h_klrEgO-KNrSz_GZuPZYdGQJk__','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-05 09:47:07','2025-09-05 09:47:07'),
(9,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxNzc4LCJleHAiOjE3NTczNzgxNzh9.KKhKcaHNp1zUu5W846_pVxfUFwiIRuwau5d_u_JqpIeBBwByBGjb0UM9LRg1VzVy','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxNzc4LCJleHAiOjE3NTk4ODM3Nzh9.fBvYpFnFEKximiQfS1KaBGj222SCYeiBjP9bUQHHarpMutOuh_tkvsRGEMpPACsb','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:36:18','2025-09-08 08:36:18'),
(11,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxOTYzLCJleHAiOjE3NTczNzgzNjN9.EpnknYj0hf6QOukzLJFbl0xvUtJ5U4DzGJA3QgwiwYMKjL8FCuHz5E4cmXfQqXMs','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxOTYzLCJleHAiOjE3NTk4ODM5NjN9.kiy8bG15_19t7WKHGxaxP7iIGIOkzKQSoG_jF5-O7Nz6fQKEoIVtfz1OwFVtwqvJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:39:23','2025-09-08 08:39:23'),
(12,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNjA5LCJleHAiOjE3NTczNzkwMDl9.yrrgNLPBvrp2jbSkWVvmzlHl0vU03AMMvlmZgrvyCa1CWINSldNkfugVQ5Zqv-UA','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNjA5LCJleHAiOjE3NTk4ODQ2MDl9.vrKZ18F5iuzhrmICwva8W4HXVkDfRf4lMAMlAxvUix3axWHHjA3G4TS2-6v1yqD9','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:50:09','2025-09-08 08:50:09'),
(13,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNzUxLCJleHAiOjE3NTczNzkxNTF9.xwlDvsOmFyDMqNFfAFVO12JzPOpOkXNYIS1gQUvF_i8WRlVnqnngrphkot2Qk-J8','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNzUxLCJleHAiOjE3NTk4ODQ3NTF9.UFYtsw_sOh1NAdNS6QL1lP3FY6Doac_DB-IEGf9dqst99kPWvA04mhvfK_ijEnKO','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:52:32','2025-09-08 08:52:32'),
(14,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkzMzg2LCJleHAiOjE3NTczNzk3ODZ9.4K9yhnboor3kNOV4C1eek5jRiyT5QPjbCwrwl3gw4Wg3ZdjKYyHmfG1hFEwUeDi7','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkzMzg2LCJleHAiOjE3NTk4ODUzODZ9.syPipG95bymMk8_erEPP_3LdTooJ3tYMzTPi_oPpnswE_g8k3_beI1wfwGpgWHjt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:03:07','2025-09-08 09:03:07'),
(15,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTM0MDYsImV4cCI6MTc1NzM3OTgwNn0.sV9nFV9WKoYhKRMfMPH_Knd8222NZuYLPd3XMi75OQODtBiKO8qdIVw16UhF32m_','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTM0MDYsImV4cCI6MTc1OTg4NTQwNn0.RS8HDAHyfNzQOI8_E-hwhRGA3Q2WQlkJWUfwCtjsNQasuAqTrr1qr-DUFJYo-vgK','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:03:27','2025-09-08 09:03:27'),
(16,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQwMzksImV4cCI6MTc1NzM4MDQzOX0.NAzhgW0IVibSOCjwun_jnPBGHIxO6Z8AgHUGF-bHwfgcoyDUFoVJ6AeDMEDLQBIK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQwMzksImV4cCI6MTc1OTg4NjAzOX0.P1Uxb6GHroBptC3SZC19ZkD407UgGEpqA9736Jl00SCXS0k4HsXIijD6qIVsAN_l','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:13:59','2025-09-08 09:13:59'),
(17,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQxMTksImV4cCI6MTc1NzM4MDUxOX0.TfD7pfTObWc4fURYdBPixrYUJXDCven5unxCSi4CiFyHrG8z-o15cpyz8ALnzGSN','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQxMTksImV4cCI6MTc1OTg4NjExOX0.8GYcY6q8Fla_owEiS6YBl4xtlGLtt7rbdbGJ2tCNOI2ZKmWHdK3aoU1LzISYq39L','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:15:20','2025-09-08 09:15:20'),
(18,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQ1MzYsImV4cCI6MTc1NzM4MDkzNn0.aJQlJ58PhFD0MdNPnu9kkMuJ7MfyM0bAtLWaCz_mGMvy8W9owzk7AQTjPPi7Ngi6','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQ1MzYsImV4cCI6MTc1OTg4NjUzNn0.2aKF3FJXOAZYMnMhbo5B-ZB6ay3vowT0eLi1hYpTdaR4B2puYp2XhGsrhoMcofAv','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:22:16','2025-09-08 09:22:16'),
(19,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MDYxLCJleHAiOjE3NTczODE0NjF9.MjfKw0KVcIeq0Py9PT7OLweOSx6fvO0mz3pzYE-echL1PMr3YYHWIp7N2RSU9AuA','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MDYxLCJleHAiOjE3NTk4ODcwNjF9.8a8eKlwzdgcjNUYPgCyHq8ib90OharjPM-5URT9XJTlgSO7J7BF8eG-BGWh5-yCC','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 09:31:01','2025-09-08 09:31:01'),
(20,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUxOTcsImV4cCI6MTc1NzM4MTU5N30.M30QypZ2o6jhzHUspzDA2olvoECFjCf9-a5YwiOBbpculGU-NiBo6IoPlkgMCsiN','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUxOTcsImV4cCI6MTc1OTg4NzE5N30.UGlJb6p9lZ9IgRLf5WmSIF2NAdY0qbEtVC78SHSAiTfBTfZQCsA8yE2YEndkfy-Z','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:33:18','2025-09-08 09:33:18'),
(21,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MzU4LCJleHAiOjE3NTczODE3NTh9.g3g8f70oaP5n8bwtb9qHz6Le8WVK4KAJj0Ip6VJ8bA4nGIbTQKb32K8Iof1-M9FL','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MzU4LCJleHAiOjE3NTk4ODczNTh9.9_AW1fzH3tBDQALoxtZhbc22Fohjrv5MMjEoagqke1IGun95Oqis7yKYp01CL9Ra','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:35:59','2025-09-08 09:35:59'),
(22,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUzNzIsImV4cCI6MTc1NzM4MTc3Mn0.nAigvq9mzWyNHREzha5wnXABRHu8z2KG5qoxNNargie2cR2PqUXOzyQcflvJ4vMn','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUzNzIsImV4cCI6MTc1OTg4NzM3Mn0.0PsdceN5sMFh42Vr21mu9zJ0ToUKJLjPDlzGvvjfRyDd6XYAFLkPQaJGmFglSwIL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:36:13','2025-09-08 09:36:13'),
(23,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1NTE4LCJleHAiOjE3NTczODE5MTh9.rDrY6Fr1afLAxlaQg16xr_Rw2P0WD4p8_ec0HnNfENOW2Tyjh-9hjItnyUFB9936','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1NTE4LCJleHAiOjE3NTk4ODc1MTh9.wkm_lQI5RGR-z5_3RkYTTCbzVkrvAjSJnB_zwuV4QsnqBvx_O96QNQKLunSubYds','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 09:38:38','2025-09-08 09:38:38'),
(24,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTU1MzksImV4cCI6MTc1NzM4MTkzOX0.S-ULS6cCdl8DOyj0e8D4oYH9lLHzg-awnOk6FeZ-rXvDPBcCSA01CiDicFRmPRmW','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTU1MzksImV4cCI6MTc1OTg4NzUzOX0.QT31znIFWfMgfumNzdN2XG50DS6Stp3BeZ6Uc1IQqocC5CWCpR295Gr5N2IEJghL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:39:00','2025-09-08 09:39:00'),
(28,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMTgyLCJleHAiOjE3NTczODY1ODJ9.bJ8VOPeECaYceutdDb5WpVhJni8d2B_lRP3y-u8DxdMYpIHBnNo9LXpYYRAWm35C','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMTgyLCJleHAiOjE3NTk4OTIxODJ9.kchZqhN9P9htABNtXLbiQwU57m1RzJIHPEywnE84UcbwfTaZPdSWTIe9ToXKZMiB','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 10:56:23','2025-09-08 10:56:23'),
(29,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1NzMwMDI4NSwiZXhwIjoxNzU3Mzg2Njg1fQ.2i7g4S4ZIC3d41ibd8ecnctInJPxPgriGwPMZhS7Z6l3q2fdS7QguBnT50iaxbp6','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1NzMwMDI4NSwiZXhwIjoxNzU5ODkyMjg1fQ.rQTTeC8HXRtYXmZFInPVgKgTy936gx0gTFLRoGtG_L0QdIQnDJtS8wjuuGjcyuPR','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 10:58:06','2025-09-08 10:58:06'),
(30,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMjk3LCJleHAiOjE3NTczODY2OTd9.7heR9gAbYeCmgrjAqYo-NrCJgTFrDzPj3hocQAdJX9Ym84wkHZhl6hEVfBxOnsHi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMjk3LCJleHAiOjE3NTk4OTIyOTd9.6mTKpoM2Jg3tDWVEskyHk8QOQjtnpp29ivAP7aSLoanpW9XUv5AaXt3mOWrAB_7x','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 10:58:18','2025-09-08 10:58:18'),
(31,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczMDAzNzksImV4cCI6MTc1NzM4Njc3OX0.DIXFb1cZaMBt1WWCEaAy4QXG9cytSUroiMo-qlCDZuSd869Hn8zRPuwbPkY-B-nT','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczMDAzNzksImV4cCI6MTc1OTg5MjM3OX0.Tf8PHkLFJKKgXhcFKtgjIfb8bHBLmt33CuaC64e_0qyc38kjdqG84cBPwcilKUac','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 10:59:39','2025-09-08 10:59:39'),
(32,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMzk5LCJleHAiOjE3NTczODY3OTl9.7dyjxQh-0cABwJQBC227e8TDcx6HRu660rgMXPvTd06WDn08bJZg8dXGE9lIotrJ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMzk5LCJleHAiOjE3NTk4OTIzOTl9.Kllb4waIb6TGRp-XThhKl82Hxa2Z9GDUCUKPcL0an9oe49gigX7bhY6jfplwhLGS','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 11:00:00','2025-09-08 11:00:00'),
(33,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzg5MjcsImV4cCI6MTc1NzQ2NTMyN30.iIh1DvCgRJLCygi7Eul_PwgtXVYAvEiq1iEXyCVObXuEvspS7wgv1vcCB-q4vYhv','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzg5MjcsImV4cCI6MTc1OTk3MDkyN30.Ys8eoi7l48BU71vN6pkaev3xkn7dgqJwl2MSI53p3O8gB2KCE7KMlBNv0ZS7ky_M','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:48:47','2025-09-09 08:48:47'),
(34,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc4OTY3LCJleHAiOjE3NTc0NjUzNjd9.Gx-NUOodQuHVIn_vjwEWElVbDX7N4T3UfgJtPTpxi0HDyx5pkuhuc0tsPeFGIZdR','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc4OTY3LCJleHAiOjE3NTk5NzA5Njd9.R294nyIjitTjScjzgDyONAfA25M8ugsT77Ogq57u78f1Yfzv21UAgBASS3rIcZsJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:49:28','2025-09-09 08:49:28'),
(35,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzkzNTMsImV4cCI6MTc1NzQ2NTc1M30.IDztpyxP6rIZJAo1kMGghGee-1zqNi2x94_8DkPq3jUyLuA11yeNUFU0YrW1SEEd','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzkzNTMsImV4cCI6MTc1OTk3MTM1M30.Tmhh_PGspJD9-y0vjDW7J9HSqWYaklgFH3WblaxyNGj6ugdKuwvjJPkNa_b2Rk_6','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:55:54','2025-09-09 08:55:54'),
(36,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc5Mzc2LCJleHAiOjE3NTc0NjU3NzZ9.9ffqUeXqcbF4BaXlB9FknlV4ohJM9ttpyapkkXyJEuYN_Sn_uAYsvNJTD03fAH3T','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc5Mzc2LCJleHAiOjE3NTk5NzEzNzZ9.du_9TWE4lZk_KvB0sgT_VE7M6v6Fhtch5eEn4cKjYB3r2HvwJcEyxMHh_eB86tJJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:56:16','2025-09-09 08:56:16'),
(37,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzk1ODEsImV4cCI6MTc1NzQ2NTk4MX0.e-YW8KLRoN3AMGchG-pdZnMbLZrRmARcQuGRZUcHceEIlnPhkPTRNWwF7H49i_7Z','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzk1ODEsImV4cCI6MTc1OTk3MTU4MX0.gCYu-40BSRjRZduAebiYw2IcFBJrx_HUeRzo4kUAhC-g5M4S9uX13enE6TzRpTOY','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:59:42','2025-09-09 08:59:42'),
(38,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODA4ODcsImV4cCI6MTc1NzQ2NzI4N30.iDEzZYeBxE1CHf79kxtu1B4IoUHmuhck_lVeT-1ykraf53MXBVRPAMbBzmSz01aM','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODA4ODcsImV4cCI6MTc1OTk3Mjg4N30.M75UMqA2ftjfIDN2DGxKokOKB653PS5L24EncgK9fDPhWP5ybH0w6pT3ehghWYzt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:21:27','2025-09-09 09:21:27'),
(39,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzgxMTE1LCJleHAiOjE3NTc0Njc1MTV9.C2RI7Jcw1NuvOywyx_bGqdg0Po-ZIbU6RvKvBf8-iF0tQgDsssNnZyB2PgPAyZpC','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzgxMTE1LCJleHAiOjE3NTk5NzMxMTV9.BDPHtBU0Uy1WR__ze2MhjYw0dbE77cstlcjJFE0xqtFPMBEcaWhxV6nAXMzEfU_H','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:25:15','2025-09-09 09:25:15'),
(40,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODExMzgsImV4cCI6MTc1NzQ2NzUzOH0.rgIh0N6E0JnZmmAySBU0jgEmIalyccclo-CITf21h_jANcM1zYCZi8RFBgLTCpA9','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODExMzgsImV4cCI6MTc1OTk3MzEzOH0.H2rFVFK38J_GYOpu4apO-IWLLE-mRUxCuV-sQ_vot0vjMwJQEcnmnyEO06FVGR-j','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:25:39','2025-09-09 09:25:39'),
(41,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY1MzA0LCJleHAiOjE3NTc1NTE3MDR9.aOowgbfy8vCjR8OSuimUqgLNDbD3dxSHtxhro08X_aNLwJZ3YC-hagWIQI7yWjXK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY1MzA0LCJleHAiOjE3NjAwNTczMDR9.K9Ze-bBbEyPZ-v9jhrRqqaM2TN63MK8ZBWw_Kc3-YTWtWJB7mxieUW7_D_O7-tOl','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 08:48:24','2025-09-10 08:48:24'),
(42,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NjUzMjgsImV4cCI6MTc1NzU1MTcyOH0.viwTZrt6epn-Cp0tiR8doLjX_FNQJKh-oVeBGa3k07h-b9r1iirvfP5ZJBOw8G52','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NjUzMjgsImV4cCI6MTc2MDA1NzMyOH0.45_QYJcyOSAZlZjTSf5xtqLHzRVLrstH3uXZJ4-YWq9DlJIklRQDb1HEgXK0U3_C','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 08:48:49','2025-09-10 08:48:49'),
(43,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY4NDYxLCJleHAiOjE3NTc1NTQ4NjF9.C10Z8_7S-7Y7plJtXOMqJx7QuWLZtYCDFvnPEhzr4Dg1aXLJdDPB-LaBhXwJriBZ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY4NDYxLCJleHAiOjE3NjAwNjA0NjF9.GK1CpqeC4b1qkEguzagjkaYuNxT3e3fbtTp1SBnPuG2CW53r84mKDq5GotOm2E6f','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 09:41:02','2025-09-10 09:41:02'),
(44,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0Njg0NzYsImV4cCI6MTc1NzU1NDg3Nn0.tebDV_CX-8ETqepGckTStk45FN8Uk7e_OAzc3qTHSAQ81BrudrWECYbPdz5YVwcf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0Njg0NzYsImV4cCI6MTc2MDA2MDQ3Nn0.XKERgZVcq4IVuygzWIisXwghRb43Pwc8PiISvoprr0pfXD2pO4hYH1ZkHVJ4jX-G','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 09:41:17','2025-09-10 09:41:17'),
(45,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcwNTgyLCJleHAiOjE3NTc1NTY5ODJ9.vNso6atgwY7hiHhOA6izQ29P4wuBt4mRVPbouE7GWg4nik_tyLPJCdSwFeqzdq8P','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcwNTgyLCJleHAiOjE3NjAwNjI1ODJ9.xbyxQaD5h-uzinlp5YLviz8aQyQJQuRRxJxboquymLMO52TSb6KKemhs6BF0tcQ4','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:16:23','2025-09-10 10:16:23'),
(46,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzA2MDQsImV4cCI6MTc1NzU1NzAwNH0.gGNal-fH5DlP_Gw_C02sWy_UuovssltnOhydm5HmU8wSCo74zsFb4qbo91hCbPXQ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzA2MDQsImV4cCI6MTc2MDA2MjYwNH0.rDa5bs8XmWgTn1EHPXbRYF7HrWACYn-u9pl9EaIQlgwQ3l4bW-4KXEtkcmwu4UFH','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:16:44','2025-09-10 10:16:44'),
(47,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMDg1LCJleHAiOjE3NTc1NTc0ODV9.PBo_mkTkGnsDuy2vhZZGqh9X-0SzwqKvHFH_HXaelencmX6NQXRnJGHwTQBn7XFf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMDg1LCJleHAiOjE3NjAwNjMwODV9.UfvO9PddSTgVnSFIZSnmGngE2PMYwAd01YH4vNyElh2tD0bbD5cWjOTIOEXH8_DQ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:24:46','2025-09-10 10:24:46'),
(48,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMjg1LCJleHAiOjE3NTc1NTc2ODV9.cYN1AXqZSvoXK1WRispiMo8yoxRLqPwlsxAoIQYWg-PH4FlYneDzl8CDGrk5rSQZ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMjg1LCJleHAiOjE3NjAwNjMyODV9.8Kzq_7TZCQ_Nda11kHi2_e36_-INYRUoKSDm1rrZvjDTSu-ItNIiFVEPFtqzCTo-','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:28:05','2025-09-10 10:28:06'),
(49,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxNzkxLCJleHAiOjE3NTc1NTgxOTF9.2V6-UgB75Zveu70nvDxvdv59kjk3WEpIk_DlBMh_DdytQTJzdOaKqyn0OZye9QND','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxNzkxLCJleHAiOjE3NjAwNjM3OTF9.lc4c4lDXbsBhfl8DY56rM2GozWp2T1rJMDYRJu7iomhU4u7cAUA4WFhhng2NBDZf','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:36:32','2025-09-10 10:36:32'),
(50,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcyMjEzLCJleHAiOjE3NTc1NTg2MTN9.o5Gc2z60ERbNtVABaaebWVW7cUnYGsFIX9ebXWX2V-zUWq1baLQkc41G29gyWmTn','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcyMjEzLCJleHAiOjE3NjAwNjQyMTN9.JutKTHwzSEYOAj5UZTFogr2n1U3Rr4wtJyUW3IsUQPXKesp3Zx7A4Fvam5b--rtP','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:43:33','2025-09-10 10:43:33'),
(51,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzIyMjUsImV4cCI6MTc1NzU1ODYyNX0.2cTAig0WeA3KR8IxohzytHlHCI1OhrnWHUwdT3w61adK94oroKfNYjH7rn5C5LUf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzIyMjUsImV4cCI6MTc2MDA2NDIyNX0.MktTHnx_HSOu5-fcxVjwsZWMoIxwsuHON-FTFvgVTN9T-enHc-OzjNNfXWm9VXby','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:43:45','2025-09-10 10:43:45'),
(52,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMDE0LCJleHAiOjE3NTc2Mzg0MTR9.5m6ryz-Y4niH3h6YEkq2hmklkAaP8KHg5zLajHWzlStgI0rjUOU-MM3iwPCcpFDp','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMDE0LCJleHAiOjE3NjAxNDQwMTR9.0E4BdrHo_do75Ch9rqwjyz7pCYQCWE5Xw5_tnneiD9ROM7j9m56iwP_2jT5vxPn_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:53:34','2025-09-11 08:53:34'),
(53,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMzM0LCJleHAiOjE3NTc2Mzg3MzR9.xfQvk_zdX3cxFGzpRZzdT4Iq5TzWO2M_0bQAvFKri182aJYMA8hcMSrPjHlo3VaY','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMzM0LCJleHAiOjE3NjAxNDQzMzR9.VYpbkCDEb_UIBwSLfDOLy-wcbDxMZbHAxy-G7qLJWqlzSazZ9uCjCIrC05u_3rB5','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:58:55','2025-09-11 08:58:55'),
(54,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc1NTIzNTAsImV4cCI6MTc1NzYzODc1MH0.ZI6KD5KahiHFqKBGWvWepMWGxegsgbHlUydtpSh993962uw80x0A4yIv_M2Tds3A','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc1NTIzNTAsImV4cCI6MTc2MDE0NDM1MH0.1G_Gbb7ch0V3uU1NcQrcF-RGoURJ5goADKEU0A4yyhgHDNFFC7u4IpWCmnD9JsWT','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:59:11','2025-09-11 08:59:11'),
(55,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTU0MTU4LCJleHAiOjE3NTc2NDA1NTh9.jjeDTPVcaZCfB0TSBE25BfhuM5PLCsriF4dqb2t0iWu10mya30LZslnJUlrB8lGc','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTU0MTU4LCJleHAiOjE3NjAxNDYxNTh9.LEzAZEixqugRqjKgHTZSj1y-d5laPXF7hWb0b3tY_Map7YsJOdLyFoQd2YlUqqsq','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 09:29:18','2025-09-11 09:29:18'),
(56,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzc3NDAsImV4cCI6MTc1NzcyNDE0MH0.8Zx83vfjp9gc4WIEEX5J5uEb0wB_iXkRYT3IL_xzKcyf7N-7tjJDjKBf3VjcDJIc','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzc3NDAsImV4cCI6MTc2MDIyOTc0MH0.viGg0XmEOcNzHjLMX2LCvlVzk656ygeKArqDt1wyyPsoRMPS4jqFSnpqG1xda6Dd','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-13 08:42:21','2025-09-12 08:42:21'),
(57,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzk1NDAsImV4cCI6MTc1NzcyNTk0MH0.Gf_5g_z-AlYO_af6kbInDa4348lHeu4rnM1IAOc0iwMnp2Z4w4wReqvkbSc5IcC4','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzk1NDAsImV4cCI6MTc2MDIzMTU0MH0.ArfO-x9QTzq-Y55lTQNtXSw2rMYh4XLA-hAVerMY4hgFHk5p2kjmdS6OY_A5oGxb','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-13 09:12:20','2025-09-12 09:12:20'),
(58,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg0NDcsImV4cCI6MTc1Nzk4NDg0N30.ukXU81b7Yaz8c6zaKVXTBbLzf01KdGxH3gAmXzEW0k9UKKPCXn0YK55rvlMw8wAF','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg0NDcsImV4cCI6MTc2MDQ5MDQ0N30.-hKWKb_tuuGp4JdJ5Ipcl2T10TRRXw19PLfeTb6mPDIxPmQ8DU-pY40_8LAw60Yt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:07:28','2025-09-15 09:07:28'),
(59,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg4MjMsImV4cCI6MTc1Nzk4NTIyM30.HQIA3c8_8ArXCqmJljfctyc3Shf5dcaYyppBKaKq_V7VMaTod7eiRc-Xx2438x1J','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg4MjMsImV4cCI6MTc2MDQ5MDgyM30.mLiVAmu5igMacorVe7AHArP7-VBCZ0hczl-3z27nQNx07wfoidFydSqg3xP9B3HP','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:13:44','2025-09-15 09:13:44'),
(60,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3ODk4OTIzLCJleHAiOjE3NTc5ODUzMjN9.RnbHaA8W2lsYvLhzUqqh6TV34cTVsXbRQ7q2VS5XkP30APSIZt7WOw9sAoo5qqsF','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3ODk4OTIzLCJleHAiOjE3NjA0OTA5MjN9.bcf4WJAQzHnqmLvW_SX1jxv_U0pduHYqy6UjNazEpC0r5P4Vx0jZBVE8xwibF64T','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:15:23','2025-09-15 09:15:23'),
(61,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTA2Njg4LCJleHAiOjE3NTc5OTMwODh9.6HUuOU4e3ubZ6BeVcOzU94uq4eR8AMgcqo86nB7LO5VyVnJ_Coutof1vBDYeoFck','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTA2Njg4LCJleHAiOjE3NjA0OTg2ODh9.3CKpouJdXhnOPxHVddMvAVBtDpMKCVZipOQEEfGgD7jaDTOeUUqAkOnpolZS1ICf','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 11:24:49','2025-09-15 11:24:49'),
(62,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5MDY3MTEsImV4cCI6MTc1Nzk5MzExMX0.5cFxRFO_u6oOoGQmX6zVh4AishcaFKSa9vhg11gzER_i-nYBbM2N8srMAWah-dEC','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5MDY3MTEsImV4cCI6MTc2MDQ5ODcxMX0.U3oHETl7veEG86hbF0r8zv7zssKOiXNWshHO5GuT1hy_2Cpa4dRFeFxModDnjoo_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 11:25:12','2025-09-15 11:25:12'),
(63,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTgzMDY2LCJleHAiOjE3NTgwNjk0NjZ9.6WKhPbHCIoisTnIrDQsjg8z2BJFjmBT_7zSTYnz0V0DBLNDzSVvFO-szLKHAIJDo','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTgzMDY2LCJleHAiOjE3NjA1NzUwNjZ9._YB7vdpFJfH3SM89GGTskHkkP5qqkS_iyZxESQCHpic6rnuJkSk6Nt9bKnYOqEO0','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 08:37:47','2025-09-16 08:37:47'),
(65,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg1MDIzLCJleHAiOjE3NTgwNzE0MjN9.Fph26TcEGQCvFPOPtBz_Py2_qfHu3pSMX6H-T1QLoG0n1y7M4gEuvQ5fzUjXoQiK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg1MDIzLCJleHAiOjE3NjA1NzcwMjN9.MexhY9j16PQ6B6aX029-yfpoGYXqsqPTg6D8OoyPWQiQEiE2lYMUH0_b32DvInpS','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:10:24','2025-09-16 09:10:24'),
(66,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MDUxLCJleHAiOjE3NTgwNzI0NTF9.9YyfpztXbsjicx0wz6iYy_-hkGMbceG8Kscytq5hmPK5ApXMNRMTviOYI22KECIt','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MDUxLCJleHAiOjE3NjA1NzgwNTF9.0zK6T-a5N2kG49aoWIP0bVSHQnVBMuZjYiDFPweR0hk3GILTwGtMaz2u5FBQC9Ky','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:27:32','2025-09-16 09:27:32'),
(67,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5ODYwNjMsImV4cCI6MTc1ODA3MjQ2M30.3MKSXSdLECP_Vu5mVLeYXObdjP-fSjnIFCZvtabs3b_xW8xnr6bTsDMN_lHtSqL4','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5ODYwNjMsImV4cCI6MTc2MDU3ODA2M30.vEPYpW7phZKLQFbpIpWhAjZZumsix6fHXYVBI1JluYxcu4-1spPNycdRrxfQP49D','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:27:44','2025-09-16 09:27:44'),
(68,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MjQ1LCJleHAiOjE3NTgwNzI2NDV9.Uq0776kkEOMIzWY3GApE7GjU1FB1hKk5RNbqP94T3fDyb0tPEHI3YW__pgIWZrIl','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MjQ1LCJleHAiOjE3NjA1NzgyNDV9.G2iw6S-paGSrg6V7CZrwq1r2hzK9rYZ3TFsSgiu8UxOI2EfThssvXSsJNVOU-0hU','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:30:46','2025-09-16 09:30:46'),
(69,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2NDUzLCJleHAiOjE3NTgwNzI4NTN9.pVvv1v0vusjgW51XULS17Ww2eLqyPFEn846o2C4Pj3r9bmWnlYQ2LNGAl9l7pbMi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2NDUzLCJleHAiOjE3NjA1Nzg0NTN9.qSLPIX2pleMMtiE5svS6p5MPOkKmC-dqHrUTD9GqZhTX14hKpfBKS31sI7EZyGHH','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:34:13','2025-09-16 09:34:13'),
(70,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4NTA3LCJleHAiOjE3NTgwNzQ5MDd9.rHBLxf2hEe4T5CbFdVlvWEAFWbwJ9QFSZqcC30fcqR8kCwHfXnkUaDNU7SUoSgvY','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4NTA3LCJleHAiOjE3NjA1ODA1MDd9.VMIpjYmdo7SqsS09C_PteZEUV9w1c7Z6CsfBaNBIWwjOBIG6DtOXMFheHzH4CQjD','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:08:28','2025-09-16 10:08:28'),
(71,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODcwLCJleHAiOjE3NTgwNzUyNzB9.yYw12Jr9tNLTqtm5S9Upomk4XiXTQDHSGXfj3NggYiW1ye3XTb8NgSjhMRRiW-8f','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODcwLCJleHAiOjE3NjA1ODA4NzB9.RAT6g6cvy1fQcZ9glSNPwe-xl4M5qt13EY5Xxu7bCZQqKKE6oOlq8NbXOStXSn1Z','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:14:30','2025-09-16 10:14:30'),
(72,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODk2LCJleHAiOjE3NTgwNzUyOTZ9.jrOeMh-hmy0ILWvnkvg5UDaH1ur4NPVWIIheyLhtiEkfsv0uk-rDIa3l8jJXiXzU','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODk2LCJleHAiOjE3NjA1ODA4OTZ9.k-H7Kwy3_gv81KWWJrcZQI1I8vOJagBAzlEnLFsHWnGChQQC36VKXJ_6HTLy5mJv','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:14:56','2025-09-16 10:14:56'),
(73,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyMzc3LCJleHAiOjE3NTgxNTg3Nzd9.G5PwjjHLGYKElQL6xwpenaOjIErxTLO_83Ol0f4xv6U8vCMcGnIOdcPdecUyqQqD','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyMzc3LCJleHAiOjE3NjA2NjQzNzd9.9FWIVfIXtriiEhCGsRZWDkBZcLzyFwFqfdoKa8hD7tKGUOpWtJXX6oijKqprvkux','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-18 09:26:17','2025-09-17 09:26:17'),
(74,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyNDgyLCJleHAiOjE3NTgxNTg4ODJ9.W_aCyI_F8vLzkTsFm39Lig84wOZH-V4xh2OsbZkf9Zfg_hW3fwkwTIfcJVOg2H01','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyNDgyLCJleHAiOjE3NjA2NjQ0ODJ9.DK6IF4vAX-LHO0B36Y0CWXa5S9F-BXtm5rMgQywk3EeM-B-DCdQvFVnqtrE0tWuX','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-18 09:28:03','2025-09-17 09:28:03'),
(75,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTAxODQzLCJleHAiOjE3NTg1ODgyNDN9.VQVtOXcE10Qs0a8l9ghs7dnA8iaXLYvz-vza5i8IcapaA69-FjWe__SPK1DH8L-o','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTAxODQzLCJleHAiOjE3NjEwOTM4NDN9.5IInyyrLN-WaNlHKwrdjUB7Pw3dR1F7oAoOqU9i-ZWsIpb8D3B3HEqKQGt70DSqL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-22 08:44:04','2025-09-22 08:44:04'),
(76,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTg4OTAyLCJleHAiOjE3NTg2NzUzMDJ9.x9a-VNuyYXVz4A65mbVn7CwGNmVcu8alTRtRm26EW_LqehnuMM3oWUBelbTzIV61','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTg4OTAyLCJleHAiOjE3NjExODA5MDJ9.St_BhB1o_zODZ-kkxXlbGGc8SgpqVTygZy-tQ4r5wcySjBMj5BR51S6eGXAh9q4o','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-24 08:55:02','2025-09-23 08:55:02'),
(77,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyMzg1LCJleHAiOjE3NTkxNDg3ODV9.Xtht8hDdmgWHXy5DRJp7qc3ub9c8ZZ4KR4bKQ1hOmMMMbym4qdIHwIjNGWL8yXuM','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyMzg1LCJleHAiOjE3NjE2NTQzODV9.qtJ-qEYWr902RWbhcCg4YoUIIIlRgyI6yyjg7TtZ87nPfF8zPSZBoYesu51naMwn','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:26:26','2025-09-28 20:26:26'),
(78,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkwNjI1MDYsImV4cCI6MTc1OTE0ODkwNn0.zwcZtIH874Bhw7HDYMX2myTidQmeN4M4HCfX0-eGylXFC2ZzrLilRhE2wrd0RXrG','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkwNjI1MDYsImV4cCI6MTc2MTY1NDUwNn0.vVSTBv_IrHR0yLW7bAlUiK4uNnos-6tElDWWsaDWYFBKLAzqN4-RfDHeHTb2yWw8','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:28:27','2025-09-28 20:28:27'),
(79,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyNTkxLCJleHAiOjE3NTkxNDg5OTF9.WjJbQeXW1UyvyqQKsB2Avn2XcG68YuGYe-e0LdPyxd1NDw-n9YxZJ3zYOry8NmDW','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyNTkxLCJleHAiOjE3NjE2NTQ1OTF9.Eo0Iubx5PyyYL9NIQp7df8etzBya6yFgln43d00hG2ugnkLPT67BaGzvlCZSkGBG','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:29:52','2025-09-28 20:29:52'),
(81,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYzMDIyLCJleHAiOjE3NTkxNDk0MjJ9.9RArdGZFS6Oq2iCiPcF_bP36DnQyhV6GhkmpaJD7NknChqzn-nTJzpj72bX7hOt7','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYzMDIyLCJleHAiOjE3NjE2NTUwMjJ9.05_vUCQT3lqLnu5ItgYVtWE7iG3HipQ0DhEGMpLuJp7ZxxCMbQX9yHNTdK3iwpUA','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:37:02','2025-09-28 20:37:02'),
(82,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NTkxOTI5MTEsImV4cCI6MTc1OTI3OTMxMX0.9nzMXQ4ggjQXIoSWWk_V1YjTolN70m9VZLyEoVoLAJ38vc09mogteutiOlyDyjcy','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NTkxOTI5MTEsImV4cCI6MTc2MTc4NDkxMX0.9IQAvmxtNTa1fN7bWg95OjMlRDp7_MhXUOd-_zIQCwEpcKugJa9folBq4o0eRVkW','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:41:51','2025-09-30 08:41:51'),
(83,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTkzMzE1LCJleHAiOjE3NTkyNzk3MTV9.v-8lNMFR3SnM2Vb0JQfHkMq8Nuo03xKa_0DuR7o4kj2jyytyJKBOPE09qQL96uiG','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTkzMzE1LCJleHAiOjE3NjE3ODUzMTV9.itNSsyeaDfUYWQ6Ku7UKtXroWdhxzx4GIULF66pw1FaviAMEh3QbxDpCzScZffe_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:48:36','2025-09-30 08:48:36'),
(84,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM0ODIsImV4cCI6MTc1OTI3OTg4Mn0.0-uIwSBMbzKjr6J9JChqPOooGWLB10q5Qlly0S0VR1Tf3rePntYLDUgje6kK69gq','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM0ODIsImV4cCI6MTc2MTc4NTQ4Mn0.1Mw6ZztS6t-mRBS8p1JTdKPQ_LHmS5BajnHlMKwaUuzT7uoxvtOHUNZzlh0auRJd','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:51:23','2025-09-30 08:51:23'),
(85,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM2MjYsImV4cCI6MTc1OTI4MDAyNn0.uqnWe5wndCRSYqeQ-yI36nz-jgf2_QSJfMNI1meWVm_l_U9owtmaGCJQXqfr1uAm','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM2MjYsImV4cCI6MTc2MTc4NTYyNn0.LdNtgLXAHbU_sQJuGIlbNxZkZakknf0rQTyupuud7y4CTitCgh3Z9mkHb3SCCEx0','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:53:47','2025-09-30 08:53:47'),
(86,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM3OTQsImV4cCI6MTc1OTI4MDE5NH0.hTBrC9OUWslZ_4KdZevqUJu2E7v20TQZO24NfApGABbMDdShm2u4_Zir7ly4sjKe','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM3OTQsImV4cCI6MTc2MTc4NTc5NH0.escAkxV5Auwydjb1ob7OvspGhgZiBRec-7_phkitTIfJY1g4PYifZtl9v2JQyxsu','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:56:35','2025-09-30 08:56:35'),
(87,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0MTIxLCJleHAiOjE3NTkyODA1MjF9.ivnWnptVqe5EHuYS8_8_WwRDm1-2-W64HLb3PB_tKzmhFxGgfJ76GRNpQKqzip_G','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0MTIxLCJleHAiOjE3NjE3ODYxMjF9.nh7iA7lce2O20DEPHxeojtKfZjt0RWm6vOLbUyIEUQn8P5tOWFSo6jOxYFEisFjY','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:02:02','2025-09-30 09:02:02'),
(88,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQxOTAsImV4cCI6MTc1OTI4MDU5MH0.5x4IuNibLM0bbudBIQumdb0T1vYzZiFNqjkdDM3pR_5pOxXz0uu9WB9eDvdkFNF3','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQxOTAsImV4cCI6MTc2MTc4NjE5MH0.4Ffbv3BxjvnhUPDY3RTuP4CwY0No-CP9vpiLXiLp636dMNsRYyEbzaenk8hN2SOe','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:03:10','2025-09-30 09:03:10'),
(89,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0NTMzLCJleHAiOjE3NTkyODA5MzN9.whqVKlx_b4CCpGCn8UrNF0oNq44l6246GjSjIG5WsxV18okRtNxYkNfQ_XT__wRX','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0NTMzLCJleHAiOjE3NjE3ODY1MzN9.q2B17V4JxvZHZ5RWCjZXPY4C2sfVmuZrnP62lCFzfo6KZVtCVdEDqJQqgSkhCpEa','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:08:54','2025-09-30 09:08:54'),
(90,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQ2NjYsImV4cCI6MTc1OTI4MTA2Nn0.XFbe2pbFQErd5wom2vCQGep4j24XBqCsb0HDFJh2Gio4_Fc0ObzzJphB7CpZmh-l','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQ2NjYsImV4cCI6MTc2MTc4NjY2Nn0.6721G2ZHZ1hzSP7DZoqMCPTcLQr7i4CI-m-2pQjriaV7wqBY3yXAuD-YZHonvEV_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:11:06','2025-09-30 09:11:06'),
(91,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0ODIzLCJleHAiOjE3NTkyODEyMjN9.hu2Z5n2QdbVSg2ugRynBII8WKhRFT4WjYNOpj3to8d3dwNNL6qEnStiFo_p5GgQo','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0ODIzLCJleHAiOjE3NjE3ODY4MjN9.HfK-wfaRiEv6B-lfalZzhoOivKl9jPwOfoztoDQ8uSh_2uxioYeV4jAXvZiWZ6F_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:13:44','2025-09-30 09:13:44'),
(92,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTE5NjA2MSwiZXhwIjoxNzU5MjgyNDYxfQ.fXt_dAfLzQOc_rowClzrt63EWDdYec35bbd5U4i2IWdwJEL7Yc1ffMj5uC_rgdP0','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTE5NjA2MSwiZXhwIjoxNzYxNzg4MDYxfQ.JUL4uEblt-n-80QSoZeDqBH5F8drEF55IdiFy0VWqzBm5IwX03xrTrOOTVugSS9K','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:34:21','2025-09-30 09:34:21'),
(93,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk2MDk1LCJleHAiOjE3NTkyODI0OTV9.ho-FVD5P2koOP82xTkf5a6xIKXob6SEGHXmKYVk4Pqs9F7BPDMftDwL-Jv-ys25w','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk2MDk1LCJleHAiOjE3NjE3ODgwOTV9.j7u-91Ja5bMUb5UxaDB5eSXPens9mV6ILFyXN1baZzvRhtdaimACSBM5ZMQ7cUdA','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:34:55','2025-09-30 09:34:55'),
(94,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5OTcxMzMwLCJleHAiOjE3NjAwNTc3MzB9.oKTEZrDydqMXAWhw3N_yGz9iD_8oN98Bu0qJt-4BT1mMmeiCZFH7PmAjxkqKaHLL','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5OTcxMzMwLCJleHAiOjE3NjI1NjMzMzB9.UROelWqW33JtGKAeSU7lKScmx5yfwU2IG1Ix1TWL1BPQSMTEXTr3f8C9OlT8by4_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 08:55:31','2025-10-09 08:55:31'),
(95,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTk5NzEzNDUsImV4cCI6MTc2MDA1Nzc0NX0.BQJ0pesN8-puvTz6Rtp-qxTKJyv1tIjwQObb4ObHuT0cdoOGV43Vj3g-6fN0k0iR','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTk5NzEzNDUsImV4cCI6MTc2MjU2MzM0NX0.CT5dNM6zPLdS_k22qGDSCEvqvl9fYtkRYW2XMOH2xq0FuZNMxD4wbCIjUpSNYQtq','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 08:55:46','2025-10-09 08:55:46'),
(96,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MTM3NSwiZXhwIjoxNzYwMDU3Nzc1fQ.TJguPPmuog_agXySEhiozh8IU0AL7h0EWTRxuEs8Q13H7PUTvIZhSobLuKRZrgeo','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MTM3NSwiZXhwIjoxNzYyNTYzMzc1fQ.tHmkIliYn5uUKqZgKotRG9pqe99ySr7WcMI5aeuGIWdGalLKg0ounq9BXOcj1vzo','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 08:56:16','2025-10-09 08:56:16'),
(97,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5OTcyNDUxLCJleHAiOjE3NjAwNTg4NTF9.RlbNeRgloUN2vjJqAawrZ3AtHSZ7Bh6uvsg1R7JJloXKykC5XCBaW0bxMGRd6tA5','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5OTcyNDUxLCJleHAiOjE3NjI1NjQ0NTF9.xB5RAmOc-VtjJlFoAj-ATdGeVkk267jrPXFGJaILcJaexESFEecn5GL0lScl1Jcz','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 09:14:12','2025-10-09 09:14:12'),
(98,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MjQ4MSwiZXhwIjoxNzYwMDU4ODgxfQ.gbcpMIlfJ-ryvtjJU-l7o_oi4aiCE0N9yceparbeWG2mqZeRiMxtr7spUPH-BLra','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MjQ4MSwiZXhwIjoxNzYyNTY0NDgxfQ.2tnXhyjRXb4jG4LH55AdtE1mIV2m8YvEVrvKyhneEIcAOb1o2bFMYl2uStqAHN2D','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 09:14:42','2025-10-09 09:14:42'),
(99,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MzMwOCwiZXhwIjoxNzYwMDU5NzA4fQ.hGODfBS4MrtGRlowy_WyxNpVmT2RwEqX_Fpy3qlhLG9zUd9HAtVzRdt4NSbXGhMi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3MzMwOCwiZXhwIjoxNzYyNTY1MzA4fQ.7Nn6qc1EET4kglg4Drs0t-yhHnaDO5FZLGscPJyFGv1fWFmkQURXaBuXDLTqHVji','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 09:28:29','2025-10-09 09:28:29'),
(100,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTk5NzMzMzksImV4cCI6MTc2MDA1OTczOX0.G1khVyYYiodPBXB54wqP0qOlb6Z6Lqqu47Br4emt3LJWXoSYy51QBuFEfZDopJbc','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTk5NzMzMzksImV4cCI6MTc2MjU2NTMzOX0.ULgSIxCriHkXRWEiSzK7Tfl0X3ineJm-UOwN-WFSUJdn7we_-7DrhVbI3imE93hc','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 09:28:59','2025-10-09 09:28:59'),
(101,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3Nzg2OSwiZXhwIjoxNzYwMDY0MjY5fQ.fp99JaBoUV48F8NqidoLT64A3Cal1-M4RhkJ9JO7oML-U2LDM6Ido3jNO_efIhch','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1OTk3Nzg2OSwiZXhwIjoxNzYyNTY5ODY5fQ.4BUPA0cjPGlRChcerc4R4HfA5GNMBIZkaUVcKUTOaGBtM4-Iw9hyajHIQ7VflNAv','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-10 10:44:29','2025-10-09 10:44:29'),
(102,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjAzMTg2NjMsImV4cCI6MTc2MDQwNTA2M30.1EfPVfK64fIaUnBUElpQ_Q5g9dEimnWjEVYliVHTPZdOUob-jcBKUTkZ_Rd41xV8','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjAzMTg2NjMsImV4cCI6MTc2MjkxMDY2M30.ruyPw2hH1qJofl6O0kfaeYtcrpjcnr0ZvSj9tOp7XfUFZVNq1bGfwYxtoE9GGoru','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-14 09:24:24','2025-10-13 09:24:24'),
(103,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDQwMzIxMywiZXhwIjoxNzYwNDg5NjEzfQ._LhdVuAmLeqflnBDBuY3doa2S_UwHD7zp9iHJr9NrhsVs0LUXwJDv-M7LNufxYUZ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDQwMzIxMywiZXhwIjoxNzYyOTk1MjEzfQ.wufsdDGQi5TN21B9EFSk16m3fNF9VWMa3ZUoAgkIDl2XdMw46FlShZxNYDadoXms','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-15 08:53:34','2025-10-14 08:53:34'),
(104,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDQ4OTE2NywiZXhwIjoxNzYwNTc1NTY3fQ.hy5xsT5i8dNExce_rYIG-JQ1xB82FbENc3OUVdNIdxNDmL74FzwVTP77n69n1huU','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDQ4OTE2NywiZXhwIjoxNzYzMDgxMTY3fQ.ctlcZt7FH_eZG_ckpJ9c6FPKj2QliQrVQgXnIX0qd4xK8LIJA9nHrkhEsh66BOev','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-16 08:46:07','2025-10-15 08:46:07'),
(105,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDU4MDE4NywiZXhwIjoxNzYwNjY2NTg3fQ.kc9K_IEI4TgslWcHdenQ9oZBQGxHwsbJLAvJxoXE2bUJQWph5HPMTmJgXbxLiKE9','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDU4MDE4NywiZXhwIjoxNzYzMTcyMTg3fQ.-AgnHGK4fCt7m2Goee5sPNh5bLePIiy-64gpyOeBQoManBBF2yJq1aiJj3QwvJ68','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-17 10:03:08','2025-10-16 10:03:08'),
(106,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA1ODI1ODksImV4cCI6MTc2MDY2ODk4OX0.9Cgi12N1yCMYbTt2Zt9Pi2yHVvbqx-XERxbE6DRzzY5KXrovPcycjTV1Whttowiv','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA1ODI1ODksImV4cCI6MTc2MzE3NDU4OX0.qVr87TqslpqdM46Sye3ZRyzAyaPywLZFntMDNtWAM0_8E6sPPP0SmLCw9EtvQ1XB','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-17 10:43:09','2025-10-16 10:43:09'),
(107,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDU4MzY0NywiZXhwIjoxNzYwNjcwMDQ3fQ.VDiXAzn28R64O_HXlG2rG7g0MaH6J47qCC7MTWOr8IzV_lSQcv-iUl8blpmN_9ka','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDU4MzY0NywiZXhwIjoxNzYzMTc1NjQ3fQ.JV4RFb75Q8TFFAdxfYCieq0d3E-XMNI9_uVXarYlP5uwK7w9yEgeP1kN3VzaggWj','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-17 11:00:48','2025-10-16 11:00:48'),
(108,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA1ODQ5MjUsImV4cCI6MTc2MDY3MTMyNX0.OLVVm4NnnSwBvBmM-YGYpio1XszrNXIN-zG_r-n17TJBeSmgzZsfn954RAjLsaZD','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA1ODQ5MjUsImV4cCI6MTc2MzE3NjkyNX0.Btv5q70bQJi_MlqN-fh-5K4GNR_qpVcPpIIcqgMNfA1L3Z9TEjfVXu2XzRR03Tvg','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-17 11:22:05','2025-10-16 11:22:05'),
(109,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2MjEyNywiZXhwIjoxNzYwNzQ4NTI3fQ.hgaJfDlAftEAT2L5lUfBEIM9WNKj_uRwOKEekFMbKR3qceRlcL3Q434YuO6-ZHH1','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2MjEyNywiZXhwIjoxNzYzMjU0MTI3fQ.cTNU2yotaVdSoYih4uuJTMALd7A4g4bIU1gS0c8w0kC8ZeFPQfApRfHEYb_7sk91','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 08:48:48','2025-10-17 08:48:48'),
(110,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjIxNzIsImV4cCI6MTc2MDc0ODU3Mn0.2MIYSkzr34qiSdZ2-3PR7rOS9n3W51iU8FjaGBloTPwfJXp_1-KCphSN_kYIrGR0','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjIxNzIsImV4cCI6MTc2MzI1NDE3Mn0.Bbsx8VNcbs1FlwG2Z8aK-F9EH1wUNRqpAEp4zExqkFskIKLKqA74FeEOaWkDDSej','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 08:49:33','2025-10-17 08:49:33'),
(111,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2MjQ0NSwiZXhwIjoxNzYwNzQ4ODQ1fQ.UpBJHhhXKz6dpi2M_w7FCzcvDHESJslOdPf7btW5He-LTKjidNjulxWzCi_IejuF','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2MjQ0NSwiZXhwIjoxNzYzMjU0NDQ1fQ.rFHueD-ANsBgKcqJIBiTjbzcch3q6l7e0PNBiqKwDApvUR21_J-es4wNF1PMLqTQ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 08:54:06','2025-10-17 08:54:06'),
(112,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjI0OTgsImV4cCI6MTc2MDc0ODg5OH0.7d9RAvc2eY-MjV1pvrCx8nqhrPJXIUZSUfAqAMoL6kNE8odkvHmApQH8_TeymJTq','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjI0OTgsImV4cCI6MTc2MzI1NDQ5OH0.5NtBrwpQbRhpR58cpjWVFQCzsXyhVMdfU2G3NsZ8FCyFvVWGniicC-ipVeySAw9x','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 08:54:58','2025-10-17 08:54:58'),
(113,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2NzI3NiwiZXhwIjoxNzYwNzUzNjc2fQ.n7YS8d5fet1U2YZL-PfKtDf7T6WaJbzc10iBWcShh4ibORP2Q06QXn9vVvB4SYwJ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2NzI3NiwiZXhwIjoxNzYzMjU5Mjc2fQ.NWdOaIIHWIa7sHOviWoGvlYgF22xdgefu_U6dZbzrJfNFewKyfPbS3cIaqQ6BC80','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:14:37','2025-10-17 10:14:37'),
(114,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjcyOTksImV4cCI6MTc2MDc1MzY5OX0.RckMj42eW0_ekFPBPizFNOo1-vlh4s0bsY9vNCP6ZpQ4GwWKcM0UnQnT65sGlr5v','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjcyOTksImV4cCI6MTc2MzI1OTI5OX0.MDC3bUGAFFzhzgLX8UBG294QflcgP6zU-4jTkKUv8Vqjr34VuVWIyXCQFuhSHDGD','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:14:59','2025-10-17 10:14:59'),
(115,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2NzQ3OCwiZXhwIjoxNzYwNzUzODc4fQ.swVopP4n75o0X8zAJUjNUulNYqmdhWYQYYH6EODZY8MY8u6o_U9eyW0brqJ9qUon','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2NzQ3OCwiZXhwIjoxNzYzMjU5NDc4fQ.xAsfnlXT9pNoDhZZSFT3PxB3LbEBLMiX1cSFMFLY01NiwkzuhkcurOIdnyLGVN7C','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:17:58','2025-10-17 10:17:58'),
(116,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njc1NzcsImV4cCI6MTc2MDc1Mzk3N30.SQ_4o70JIorjtqW2tGw6UOkyxylV21JTdYWRAR4jsmoWQXHUE_3Eg1RrrMH9afdL','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njc1NzcsImV4cCI6MTc2MzI1OTU3N30.YoTXT-YU16nezo0pOtV9cseGj9_fDxzfXkpw6u4e-K0MWEofBONrOyReAp9a-Vxt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:19:38','2025-10-17 10:19:38'),
(117,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njc2NTcsImV4cCI6MTc2MDc1NDA1N30.8uR9RB1NLcfReEhIlStGjB8J6zIiP-rIXpd64pic6sCsewcqd8nMVpG9uTAyratw','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njc2NTcsImV4cCI6MTc2MzI1OTY1N30.oUz6jqTXE_T06yDbh1l_xplSWhMKSWDf40fOH8lJQx9Gaa5mp2BFDkoN0rpx5zwM','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:20:58','2025-10-17 10:20:58'),
(118,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2Njc2NzcsImV4cCI6MTc2MDc1NDA3N30.yUQzDMSUYJ8rS-isiuVtt54ZmsTcfuF8ig_2tSQfYw2K45064gOuYp9ym4MEsZr_','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2Njc2NzcsImV4cCI6MTc2MzI1OTY3N30.b0Fn2m1MhIwiUIuspIRBQrrhfNgPAjTxAc3lcAY4P1S4DFOwbbmNpDWfBU9g2Zlo','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:21:17','2025-10-17 10:21:17'),
(119,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgwMTgsImV4cCI6MTc2MDc1NDQxOH0.bKAeJ6zI_eAyO3yvS9nYgTDi1AT6H2oX5T52to8pArU-sdClH9rxwoo3OaoIU7jw','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgwMTgsImV4cCI6MTc2MzI2MDAxOH0.GHqHPAHcj4sZKJxUt2agXfG65jcH0CjZHS7-hKZc38YRx35HQO0uGXYGLKv0Oe1e','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:26:58','2025-10-17 10:26:58'),
(120,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgwNDYsImV4cCI6MTc2MDc1NDQ0Nn0.lyUDZcnWmqrW81uSYrj_gbCzDwnEeGybaGJ3Kega_v15Vzdqk1sfz4xUQraOj67_','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgwNDYsImV4cCI6MTc2MzI2MDA0Nn0.X2o086_FwU97MhoyzPNKE3Adp9-OBp0ZewghmVYXgNGY-rNVYF1jShcT_pXtOE7C','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:27:27','2025-10-17 10:27:27'),
(121,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjgwODIsImV4cCI6MTc2MDc1NDQ4Mn0.ZfG0GDxmXIbADTGOwAWXLfaRjtibTsORB5qRv4GSEXo8kzR4ko6RLfXOknyq0Edn','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjgwODIsImV4cCI6MTc2MzI2MDA4Mn0.5sD7w7CoKzvmq7HZ8mE2pIdSi8Vg3RmCBwcDvy-417P_PGqX5VwUxpsUxb3zA5YJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:28:03','2025-10-17 10:28:03'),
(122,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgxODEsImV4cCI6MTc2MDc1NDU4MX0.P2DUvPbBl6twQpVe5046VE9vWbGn-t6M17slOMYYRpIoZlGeXZYvY8AXzrevEVrQ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2NjgxODEsImV4cCI6MTc2MzI2MDE4MX0.zZV6ZvpxMVKRkjRiSyGw97AxZsVFvVT2NptsyeQDwEcG8U_H7V_euO26yd2ba79f','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:29:41','2025-10-17 10:29:41'),
(123,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjgyMDQsImV4cCI6MTc2MDc1NDYwNH0.vc1fPGLwTS_YuBzhX9Jni_9CwaZRjaKYr1pxVc8YlMDYOXLGhAIGH0bEakiyPacI','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2NjgyMDQsImV4cCI6MTc2MzI2MDIwNH0.6kBUREHLocPr39vR_qA0EFSHkT5-mhudHI7RzindIyJu_KEBWZ0BonLtwLV_c_SB','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:30:05','2025-10-17 10:30:05'),
(124,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2ODI3NCwiZXhwIjoxNzYwNzU0Njc0fQ.1Hy9uQPWlvAxqCCQbun-XlTwRz1nRu7kr02ehdLVmgjLf2RKx-ilxXTzibyHCC7E','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2ODI3NCwiZXhwIjoxNzYzMjYwMjc0fQ.jd6lJFwIzpC7nyfYz1YjXUwrSlhkUi8PaZJawoCXA3z49ahkcYVYZORrn2UEO1sF','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:31:15','2025-10-17 10:31:15'),
(125,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njg2NzgsImV4cCI6MTc2MDc1NTA3OH0.ck_pozEbU7YVfS2RjrTquZfcY4-fObQRB1c9w3gz_n92bj_b0dYR-quJnNSMYdE5','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA2Njg2NzgsImV4cCI6MTc2MzI2MDY3OH0.4_5kXULLsnMK86kCkt0-Ruhgu0ncVxQGlDv5G5Ttjw4N8MjqPPAjkKwrjwwD3-uf','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:37:58','2025-10-17 10:37:58'),
(126,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2Njg3MzYsImV4cCI6MTc2MDc1NTEzNn0.cIdWukZPq2W8A5FV6gbwifHbBNBDSb9tbTOhTV9V3uH8VCbDtaqNk0UIs94H4wqT','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NjA2Njg3MzYsImV4cCI6MTc2MzI2MDczNn0.7xoFTtbwk4pmzllrhsi0llCxABmTpChWaZj4If18oJilnIRF9HXLpU4Cir26KLVn','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:38:57','2025-10-17 10:38:57'),
(127,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2ODg2OCwiZXhwIjoxNzYwNzU1MjY4fQ.-irJIk0169LyNHBifmSJLy46srtnb3LvVwNJ3l9211C8FpYEjFEXAdKVTAl6_kiz','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDY2ODg2OCwiZXhwIjoxNzYzMjYwODY4fQ.LLIJ3BC2TIKMU6FbtsXxNxyR09fK2ycBU8PCoN6FeYWmLpQc8nU-ugbbQF5Hpdxd','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-18 10:41:08','2025-10-17 10:41:08'),
(128,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDkyMDcxNCwiZXhwIjoxNzYxMDA3MTE0fQ.cwx6OYv71VCRHXA1YTkFI6L0V3KBJFb03EWADpjN2DWEuv5OiUYXCMok90OJE431','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDkyMDcxNCwiZXhwIjoxNzYzNTEyNzE0fQ.xD9x1qCned0YSJELwuNKSbM4GvYjLQpw7YgpCv4oDtg2Vd4Kjn1xt_Sebrwg5RvH','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-21 08:38:34','2025-10-20 08:38:34'),
(129,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA5MjA4NjQsImV4cCI6MTc2MTAwNzI2NH0.6Kv69-hY_12hubBt-js_PSkErQ_nIQWRPXPUtAUwi-vjiGdLNj0EbVRLOKRRzWCs','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjA5MjA4NjQsImV4cCI6MTc2MzUxMjg2NH0.dhQsvhtgrCIHFIFg_OsgX_DPANUWOzN61ziSxD3k-Pq6EjLwo_8U7vIjT_RFUSY-','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-21 08:41:05','2025-10-20 08:41:05'),
(130,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzYwOTIwOTAwLCJleHAiOjE3NjEwMDczMDB9.iXisV-kav8aFyiPyFAfb9GkZm9g1HOaucwpvEWvWx4DAby1eDedl7WSJNYyYRpXi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzYwOTIwOTAwLCJleHAiOjE3NjM1MTI5MDB9.KI9vQ8Ga0IWgc0WIeYJnWN9FPxKnthowzJEG5SR9boP2LGrdHUoEUUSF0JbQLci4','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',1,'2025-10-21 08:41:41','2025-10-20 08:41:41'),
(131,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDkyMDkyOCwiZXhwIjoxNzYxMDA3MzI4fQ.oXJryqVnkBCgC0jQi1PGFDa5hRdVp4ca2JPjPmolCecAHLF1LG2ek8m3Zev_U1yX','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MDkyMDkyOCwiZXhwIjoxNzYzNTEyOTI4fQ.PvwIfxlvakHH29cP6um2_UouBW3ldwX-givRDEmH7ObXf1dr5QtKlIfpoPKFDO8c','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-21 08:42:08','2025-10-20 08:42:08'),
(132,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MTAwNzkzNywiZXhwIjoxNzYxMDk0MzM3fQ.fdPwcyr0_ZnhhUJ0oSvdqWSm-6o9RAXXEmcFR7y7XVE0pQ5TQz0SlOrr2hQOydCS','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MTAwNzkzNywiZXhwIjoxNzYzNTk5OTM3fQ.jWF2ufmEF5RQNhtXZvyI1bEQDIYP49G4iZ5vwbWuwRRX7Au-8MPLrbjIy9JaxeVg','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-22 08:52:17','2025-10-21 08:52:17'),
(133,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjEwMDg1NTksImV4cCI6MTc2MTA5NDk1OX0.GXWLWpk32d5_BDQWJciyPB5eELB8nIHVcepCpUNwNoARQ-oUX9TZwUX1H6EY2fUG','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NjEwMDg1NTksImV4cCI6MTc2MzYwMDU1OX0.WLnr8JxFNyUfjUyz3wDYVSFpU4Z0vdnMutnjwEhEl7M_IgOMXvBGKo3iJbGNFELi','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',0,'2025-10-22 09:02:39','2025-10-21 09:02:39'),
(134,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MTAwOTA3MSwiZXhwIjoxNzYxMDk1NDcxfQ.fwd2vC_YKdtvWvwYzwZUQ6TE2gTAKJefFsT22X5Iw53k7iavH6feFOWaztBYsxkT','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc2MTAwOTA3MSwiZXhwIjoxNzYzNjAxMDcxfQ.kUKK1g8czPf49a7Hg05_WQcPjhhw4ccHnXTqYulihyuambZZxRFoWNwOeHCgUNOW','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0',1,'2025-10-22 09:11:11','2025-10-21 09:11:11');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码盐值',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_users_class_id` (`class_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`,`email`,`salt`,`is_active`,`last_login_time`,`create_time`,`update_time`,`phone`,`class_id`) values 
(1,'admin','Wib17jaGy+f+dqRRLlU31LtkyMXXhRH5P7mBqVJSWZA=','3310530272@qq.com','VENfs5/WIp94Y3epBDQPz3lIHiL+ldNUQq4hn1TXgAk=',1,'2025-10-20 08:41:41','2025-09-04 08:51:54','2025-10-20 08:41:41',NULL,NULL),
(2,'user','kOVDpxTPMCxh154PMx2L/llhfHP2GouehIqgfxRUNLE=','3310530279@qq.com','CAyH7i3GuDBGp5dTu/6pa8x2XQG7oNx7u0Hj2SxlNeU=',1,'2025-10-21 09:02:39','2025-09-04 10:50:56','2025-10-21 09:02:39','17531307050',1),
(6,'admin1','GTOr1vxr5noxerTXtqffVJzzEVefZDZnIihbw1lizFU=','3310530270@qq.com','iNA1TSxIOglENt+5axq4cIuFVeyHYQT1kNSH+/UIrBA=',1,'2025-10-21 09:11:11','2025-09-08 10:57:08','2025-10-21 09:11:11','17531307057',NULL),
(8,'user123','VEcZdfPU8OkbKyUYHXZI+7wGVwiiq5zL1H8tFtu9zzE=','3310530275@qq.com','lM5NWTBXSBnqD85Z132j82IKiH9y1dhTCCjzaGZMqtc=',1,'2025-10-17 10:38:57','2025-09-30 08:41:40','2025-10-17 10:38:57','17531307055',1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
