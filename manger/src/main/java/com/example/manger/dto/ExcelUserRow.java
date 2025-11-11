package com.example.manger.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelUserRow {
    @ExcelProperty("用户姓名")
    private String name;

    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("默认密码")
    private String password;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("班级ID")
    private Long classId;

    @ExcelProperty("用户角色（“超级管理员”、“教师”、“普通用户”）")
    private String roleName;
}
