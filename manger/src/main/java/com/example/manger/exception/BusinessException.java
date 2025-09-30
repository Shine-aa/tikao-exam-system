package com.example.manger.exception;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {
    private final Integer code;  // 错误码（如1001用户不存在）

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 500; // 默认错误码
    }

    public Integer getCode() {
        return code;
    }
}
