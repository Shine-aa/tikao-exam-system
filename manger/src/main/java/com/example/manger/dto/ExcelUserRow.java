package com.example.manger.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelUserRow {

    @ExcelProperty("用户名")
    private String username;

//    @ExcelProperty("密码")
//    private String password;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String phone;

    @ExcelProperty("班级ID")
    private Long classId;

//    @ExcelProperty("角色")
//    private String roleName;
}
