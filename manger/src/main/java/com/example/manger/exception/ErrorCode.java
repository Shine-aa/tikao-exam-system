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
    
    // 系统相关错误码 (5000-5999)
    public static final int SYSTEM_ERROR = 5001;
    public static final int DATABASE_ERROR = 5002;
    public static final int NETWORK_ERROR = 5003;
}
