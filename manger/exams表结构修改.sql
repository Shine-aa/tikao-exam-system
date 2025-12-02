-- 给 exams 表添加 course_id 字段（非空）
ALTER TABLE exams 
ADD COLUMN course_id BIGINT NOT NULL COMMENT '课程ID' 
AFTER teacher_id;

-- 添加索引提升查询性能（推荐）
CREATE INDEX idx_exam_course_id ON exams(course_id);
CREATE INDEX idx_exam_class_course ON exams(class_id, course_id); -- 联合索引，优化筛选