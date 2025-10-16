package com.example.manger.exception;

/**
 * 错误码常量类
 */
public class ErrorCode {
    
    // 用户相关错误码 (1000-1999)
    public static final int USERNAME_EXISTS = 1001;
    public static final int EMAIL_EXISTS = 1002;
    public static final int PHONE_EXISTS = 1003;
    public static final int CAPTCHA_ERROR = 1004;
    public static final int LOGIN_FAILED = 1005;
    public static final int USER_DISABLED = 1006;
    public static final int USER_NOT_FOUND = 1007;
    public static final int USERNAME_USED = 1008;
    public static final int EMAIL_USED = 1009;
    public static final int PHONE_USED = 1010;
    public static final int PASSWORD_ERROR = 1011;
    public static final int PASSWORD_SAME = 1012;
    
    // 货物相关错误码 (2000-2999)
    public static final int GOODS_CODE_EXISTS = 2001;
    public static final int GOODS_NOT_FOUND = 2002;
    public static final int GOODS_CODE_USED = 2003;
    
    // 权限相关错误码 (3000-3999)
    public static final int PERMISSION_DENIED = 3001;
    public static final int ROLE_NOT_FOUND = 3002;
    
    // 菜单相关错误码 (4000-4999)
    public static final int MENU_NOT_FOUND = 4001;
    public static final int MENU_CODE_EXISTS = 4002;
    public static final int PARENT_MENU_NOT_FOUND = 4003;
    public static final int MENU_HAS_CHILDREN = 4004;
    public static final int MENU_CANNOT_BE_PARENT_OF_ITSELF = 4005;
    
    // 题库相关错误码 (6000-6999)
    public static final int QUESTION_NOT_FOUND = 6001;
    public static final int KNOWLEDGE_POINT_NOT_FOUND = 6002;
    public static final int KNOWLEDGE_POINT_NAME_EXISTS = 6003;
    public static final int QUESTION_TAG_NOT_FOUND = 6004;
    public static final int QUESTION_TAG_NAME_EXISTS = 6005;
    public static final int ACCESS_DENIED = 6006;
    
    // 课程相关错误码 (7000-7999)
    public static final int COURSE_NOT_FOUND = 7001;
    public static final int COURSE_CODE_EXISTS = 7002;
    public static final int COURSE_HAS_CLASSES = 7003;
    
    // 班级相关错误码 (8000-8999)
    public static final int CLASS_NOT_FOUND = 8001;
    public static final int CLASS_CODE_EXISTS = 8002;
    public static final int CLASS_FULL = 8003;
    public static final int STUDENT_ALREADY_IN_CLASS = 8004;
    public static final int STUDENT_NOT_IN_CLASS = 8005;
    
    // 专业相关错误码 (9000-9999)
    public static final int MAJOR_NOT_FOUND = 9001;
    public static final int MAJOR_CODE_EXISTS = 9002;
    
    // 班级课程关联相关错误码 (10000-10999)
    public static final int CLASS_COURSE_NOT_FOUND = 10001;
    public static final int CLASS_COURSE_EXISTS = 10002;
    
    // 系统相关错误码 (5000-5999)
    public static final int SYSTEM_ERROR = 5001;
    public static final int DATABASE_ERROR = 5002;
    public static final int NETWORK_ERROR = 5003;
    public static final int INVALID_OPERATION = 5004;
    public static final int PAPER_NOT_FOUND = 5005;
}
