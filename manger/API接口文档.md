# API 接口文档

## 目录

- [1. 认证管理](#1-认证管理)
- [2. 用户管理](#2-用户管理)
- [3. 题库管理](#3-题库管理)
- [4. 考试管理](#4-考试管理)
- [5. 学生考试](#5-学生考试)
- [6. 角色管理](#6-角色管理)
- [7. 权限管理](#7-权限管理)
- [8. 菜单管理](#8-菜单管理)
- [9. 个人资料](#9-个人资料)
- [10. 试卷生成](#10-试卷生成)
- [11. 专业管理](#11-专业管理)
- [12. 班级管理](#12-班级管理)
- [13. 课程管理](#13-课程管理)
- [14. 班级课程关联](#14-班级课程关联)
- [15. 仪表盘](#15-仪表盘)
- [16. 代码执行](#16-代码执行)
- [17. 通用工具](#17-通用工具)

---

## 基础信息

### 基础URL
```
http://localhost:8080/api
```

### 认证方式
大部分接口需要 JWT Token 认证，在请求头中添加：
```
Authorization: Bearer {token}
```

### 通用响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 状态码说明
- `200`: 操作成功
- `400`: 请求参数错误
- `401`: 未授权/未登录
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 1. 认证管理

**基础路径**: `/api/auth`

### 1.1 用户注册

**接口**: `POST /api/auth/register`

**描述**: 新用户注册，支持用户名、邮箱、手机号注册

**请求参数**:
```json
{
  "username": "string",
  "email": "string",
  "phone": "string",
  "password": "string",
  "captcha": "string",
  "captchaId": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": null
}
```

---

### 1.2 用户登录

**接口**: `POST /api/auth/login`

**描述**: 用户通过用户名密码或短信验证码登录

**请求参数**:
```json
{
  "username": "string",
  "password": "string",
  "captcha": "string",
  "captchaId": "string",
  "loginType": "PASSWORD | SMS"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "string",
    "refreshToken": "string",
    "user": {
      "id": 1,
      "username": "string",
      "email": "string"
    }
  }
}
```

---

### 1.3 获取验证码

**接口**: `GET /api/auth/captcha`

**描述**: 获取图形验证码，用于注册和登录验证

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "captchaId": "string",
    "captchaImage": "base64_string"
  }
}
```

---

### 1.4 退出登录

**接口**: `POST /api/auth/logout`

**描述**: 用户退出登录，使Token失效

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "退出登录成功",
  "data": null
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "令牌刷新成功",
  "data": "new_token_string"
}
```

---

### 1.6 发送短信验证码

**接口**: `POST /api/auth/sms/send`

**描述**: 发送短信验证码到指定手机号，支持注册和登录两种类型

**请求参数**:
```json
{
  "phone": "string",
  "type": "register | login"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码发送成功",
  "data": {
    "success": true,
    "message": "验证码已发送"
  }
}
```

---

### 1.7 验证短信验证码

**接口**: `POST /api/auth/sms/verify`

**描述**: 验证短信验证码是否正确且未过期

**请求参数**:
```json
{
  "phone": "string",
  "code": "string",
  "type": "register | login"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "验证码验证成功",
  "data": true
}
```

---

## 2. 用户管理

**基础路径**: `/api/user`

### 2.1 检查用户名可用性

**接口**: `GET /api/user/check-username`

**描述**: 检查指定用户名是否已被使用

**请求参数**:
```
username: string
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "available": true
  }
}
```

---

### 2.2 检查邮箱可用性

**接口**: `GET /api/user/check-email`

**描述**: 检查指定邮箱是否已被使用

**请求参数**:
```
email: string
```

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "available": true
  }
}
```

---

### 2.3 获取用户信息

**接口**: `GET /api/user/{id}`

**描述**: 根据用户ID获取用户详细信息

**路径参数**:
- `id`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "phone": "string",
    "isActive": true,
    "classId": 1,
    "createTime": "2024-01-01T00:00:00",
    "updateTime": "2024-01-01T00:00:00",
    "lastLoginTime": "2024-01-01T00:00:00",
    "roleNames": ["USER"]
  }
}
```

---

## 3. 题库管理

**基础路径**: `/api/questions`

**权限要求**: 需要认证

### 3.1 创建题目

**接口**: `POST /api/questions`

**描述**: 创建新的题目

**请求参数**:
```json
{
  "title": "string",
  "content": "string",
  "type": "SINGLE_CHOICE | MULTIPLE_CHOICE | TRUE_FALSE | FILL_BLANK | SUBJECTIVE | PROGRAMMING",
  "difficulty": "EASY | MEDIUM | HARD",
  "points": 5,
  "tags": "string",
  "explanation": "string",
  "options": [
    {
      "optionKey": "A",
      "optionContent": "string",
      "isCorrect": 1
    }
  ],
  "answers": [
    {
      "answerContent": "string"
    }
  ],
  "images": "string",
  "programmingLanguage": "JAVA | PYTHON | CPP | C"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "题目创建成功",
  "data": {
    "id": 1,
    "title": "string",
    "content": "string",
    "type": "SINGLE_CHOICE",
    "difficulty": "MEDIUM",
    "points": 5
  }
}
```

---

### 3.2 更新题目

**接口**: `PUT /api/questions/{id}`

**描述**: 更新指定题目

**路径参数**:
- `id`: 题目ID

**请求参数**: 同创建题目

**响应示例**:
```json
{
  "code": 200,
  "message": "题目更新成功",
  "data": {}
}
```

---

### 3.3 删除题目

**接口**: `DELETE /api/questions/{id}`

**描述**: 删除指定题目

**路径参数**:
- `id`: 题目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "题目删除成功",
  "data": null
}
```

---

### 3.4 获取题目详情

**接口**: `GET /api/questions/{id}`

**描述**: 获取指定题目的详细信息

**路径参数**:
- `id`: 题目ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目详情成功",
  "data": {
    "id": 1,
    "title": "string",
    "content": "string",
    "type": "SINGLE_CHOICE",
    "difficulty": "MEDIUM",
    "points": 5,
    "options": [],
    "answers": [],
    "tags": "string",
    "explanation": "string"
  }
}
```

---

### 3.5 分页查询题目

**接口**: `GET /api/questions`

**描述**: 分页查询题目列表

**请求参数**:
- `type`: 题目类型（可选）
- `difficulty`: 难度等级（可选）
- `keyword`: 关键词（可选）
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `sortBy`: 排序字段，默认createdAt
- `sortDir`: 排序方向，默认DESC

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": {
    "content": [],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10
  }
}
```

---

### 3.6 批量删除题目

**接口**: `DELETE /api/questions/batch`

**描述**: 批量删除多个题目

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

### 3.7 获取题目统计

**接口**: `GET /api/questions/statistics`

**描述**: 获取题目统计信息

**响应示例**:
```json
{
  "code": 200,
  "message": "获取统计信息成功",
  "data": {
    "totalQuestions": 100,
    "singleChoiceCount": 30,
    "multipleChoiceCount": 20,
    "trueFalseCount": 15,
    "fillBlankCount": 15,
    "subjectiveCount": 10,
    "programmingCount": 10
  }
}
```

---

### 3.8 根据课程获取题目列表

**接口**: `GET /api/questions/course/{courseId}`

**描述**: 获取指定课程的所有题目

**路径参数**:
- `courseId`: 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取题目列表成功",
  "data": []
}
```

---

### 3.9 导入题库

**接口**: `POST /api/questions/import`

**描述**: 从Excel文件导入题目

**请求参数**:
- `file`: Excel文件（.xlsx或.xls）

**响应示例**:
```json
{
  "code": 200,
  "message": "导入完成",
  "data": {
    "successCount": 50,
    "errorCount": 2,
    "errors": ["第3行：题目内容不能为空"]
  }
}
```

---

## 4. 考试管理

**基础路径**: `/api/exams`

**权限要求**: `TEACHER` 或 `ADMIN`

### 4.1 创建考试

**接口**: `POST /api/exams`

**描述**: 老师或管理员创建新的考试

**请求参数**:
```json
{
  "title": "string",
  "description": "string",
  "classId": 1,
  "paperId": 1,
  "startTime": "2024-01-01T00:00:00",
  "endTime": "2024-01-01T02:00:00",
  "duration": 120,
  "maxAttempts": 1,
  "isRandomOrder": true,
  "showAnswerAfterSubmit": false
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "考试创建成功",
  "data": {
    "id": 1,
    "title": "string",
    "status": "NOT_STARTED"
  }
}
```

---

### 4.2 分页查询考试列表

**接口**: `GET /api/exams/page`

**描述**: 获取考试列表，支持分页和关键词搜索

**请求参数**:
- `page`: 页码，从1开始，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试列表成功",
  "data": {
    "content": [],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 4.3 获取考试详情

**接口**: `GET /api/exams/{id}`

**描述**: 根据考试ID获取考试详细信息

**路径参数**:
- `id`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试详情成功",
  "data": {
    "id": 1,
    "title": "string",
    "description": "string",
    "status": "IN_PROGRESS",
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-01-01T02:00:00"
  }
}
```

---

### 4.4 更新考试

**接口**: `PUT /api/exams/{id}`

**描述**: 更新指定考试的信息

**路径参数**:
- `id`: 考试ID

**请求参数**: 同创建考试

**响应示例**:
```json
{
  "code": 200,
  "message": "考试更新成功",
  "data": {}
}
```

---

### 4.5 删除考试

**接口**: `DELETE /api/exams/{id}`

**描述**: 删除指定ID的考试

**路径参数**:
- `id`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "考试删除成功",
  "data": null
}
```

---

### 4.6 开始考试

**接口**: `POST /api/exams/{id}/start`

**描述**: 将考试状态设置为进行中

**路径参数**:
- `id`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "考试已开始",
  "data": null
}
```

---

### 4.7 结束考试

**接口**: `POST /api/exams/{id}/end`

**描述**: 将考试状态设置为已结束

**路径参数**:
- `id`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "考试已结束",
  "data": null
}
```

---

### 4.8 获取考试学生列表

**接口**: `GET /api/exams/{id}/students`

**描述**: 获取指定考试的所有学生列表

**路径参数**:
- `id`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取学生列表成功",
  "data": [
    {
      "studentId": 1,
      "studentName": "string",
      "status": "NOT_STARTED"
    }
  ]
}
```

---

### 4.9 获取判卷考试列表

**接口**: `GET /api/exams/grading`

**描述**: 获取需要判卷的考试列表

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）
- `classId`: 班级ID（可选）
- `status`: 状态（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取判卷考试列表成功",
  "data": {
    "content": [],
    "total": 10
  }
}
```

---

### 4.10 获取考试学生列表（判卷）

**接口**: `GET /api/exams/{id}/students/grading`

**描述**: 获取指定考试的学生列表，用于判卷，支持分页和筛选

**路径参数**:
- `id`: 考试ID

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）
- `status`: 状态（可选）
- `gradingStatus`: 判卷状态（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取学生列表成功",
  "data": {
    "content": [],
    "total": 30
  }
}
```

---

### 4.11 获取学生答案

**接口**: `GET /api/exams/{examId}/students/{studentId}/answers`

**描述**: 获取指定学生在指定考试中的答案，包含题目、学生答案和参考答案

**路径参数**:
- `examId`: 考试ID
- `studentId`: 学生ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取学生答案成功",
  "data": {
    "examId": 1,
    "studentId": 1,
    "questions": [],
    "studentAnswers": {},
    "correctAnswers": {}
  }
}
```

---

### 4.12 保存判卷结果

**接口**: `POST /api/exams/grading/save`

**描述**: 保存判卷结果（临时保存，不提交）

**请求参数**:
```json
{
  "examId": 1,
  "studentId": 1,
  "gradingResults": [
    {
      "questionId": 1,
      "score": 5,
      "comment": "string"
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "判卷结果保存成功",
  "data": null
}
```

---

### 4.13 提交判卷结果

**接口**: `POST /api/exams/grading/submit`

**描述**: 提交判卷结果（最终提交，会计算总分并更新学生成绩）

**请求参数**: 同保存判卷结果

**响应示例**:
```json
{
  "code": 200,
  "message": "判卷结果提交成功",
  "data": null
}
```

---

### 4.14 获取学生考试结果

**接口**: `GET /api/exams/{examId}/student-result`

**描述**: 学生查看考试结果，仅显示分数不显示题目内容

**路径参数**:
- `examId`: 考试ID

**权限要求**: `USER`、`TEACHER` 或 `ADMIN`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试结果成功",
  "data": {
    "examId": 1,
    "totalScore": 100,
    "obtainedScore": 85,
    "status": "COMPLETED"
  }
}
```

---

### 4.15 获取老师端仪表盘统计数据

**接口**: `GET /api/exams/dashboard/stats`

**描述**: 获取老师端仪表盘的各种统计数据（题库数量、考试数量、学生数量等）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取仪表盘统计数据成功",
  "data": {
    "totalQuestions": 100,
    "totalExams": 20,
    "totalStudents": 50
  }
}
```

---

### 4.16 获取老师端最近活动

**接口**: `GET /api/exams/dashboard/activities`

**描述**: 获取老师端最近的考试活动记录

**响应示例**:
```json
{
  "code": 200,
  "message": "获取最近活动成功",
  "data": [
    {
      "activityType": "EXAM_CREATED",
      "description": "创建了考试",
      "time": "2024-01-01T00:00:00"
    }
  ]
}
```

---

## 5. 学生考试

**基础路径**: `/api/student/exams`

**权限要求**: `USER`

### 5.1 获取学生考试列表

**接口**: `GET /api/student/exams`

**描述**: 获取学生的考试列表，按状态分类（未开始、进行中、已结束）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试列表成功",
  "data": {
    "notStarted": [],
    "inProgress": [],
    "completed": []
  }
}
```

---

### 5.2 获取学生考试详情

**接口**: `GET /api/student/exams/{examId}`

**描述**: 获取指定考试的详细信息，包括考试信息、试卷信息等

**路径参数**:
- `examId`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试详情成功",
  "data": {
    "id": 1,
    "title": "string",
    "description": "string",
    "startTime": "2024-01-01T00:00:00",
    "endTime": "2024-01-01T02:00:00",
    "duration": 120
  }
}
```

---

### 5.3 开始考试

**接口**: `POST /api/student/exams/{examId}/start`

**描述**: 学生开始考试，创建考试记录并开始计时

**路径参数**:
- `examId`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "考试已开始",
  "data": null
}
```

---

### 5.4 提交考试答案

**接口**: `POST /api/student/exams/{examId}/submit`

**描述**: 学生提交考试答案，完成考试（最终提交）

**路径参数**:
- `examId`: 考试ID

**请求参数**:
```json
{
  "answers": {
    "1": "A",
    "2": ["A", "B"],
    "3": "正确",
    "4": "答案内容",
    "5": "代码内容"
  }
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "考试提交成功",
  "data": null
}
```

---

### 5.5 保存答卷草稿

**接口**: `POST /api/student/exams/{examId}/save`

**描述**: 学生保存答卷草稿（临时保存，不提交）

**路径参数**:
- `examId`: 考试ID

**请求参数**: 同提交考试答案

**响应示例**:
```json
{
  "code": 200,
  "message": "答卷已保存",
  "data": null
}
```

---

### 5.6 获取学生考试统计

**接口**: `GET /api/student/exams/stats`

**描述**: 获取学生的考试统计数据，包括已参加、已完成、待参加等

**响应示例**:
```json
{
  "code": 200,
  "message": "获取考试统计成功",
  "data": {
    "totalExams": 10,
    "completedExams": 5,
    "pendingExams": 3,
    "inProgressExams": 2
  }
}
```

---

### 5.7 获取学生考试试卷

**接口**: `GET /api/student/exam/{examId}/paper`

**描述**: 获取学生的考试试卷，包含题目和选项（已乱序）

**路径参数**:
- `examId`: 考试ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取试卷成功",
  "data": {
    "examId": 1,
    "questions": [
      {
        "id": 1,
        "title": "string",
        "content": "string",
        "type": "SINGLE_CHOICE",
        "options": []
      }
    ]
  }
}
```

---

## 6. 角色管理

**基础路径**: `/api/admin/roles`

**权限要求**: `SUPER_ADMIN`

### 6.1 创建角色

**接口**: `POST /api/admin/roles`

**请求参数**:
```json
{
  "roleName": "string",
  "roleCode": "string",
  "description": "string",
  "isActive": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "角色创建成功",
  "data": {
    "id": 1,
    "roleName": "string",
    "roleCode": "string"
  }
}
```

---

### 6.2 更新角色

**接口**: `PUT /api/admin/roles/{roleId}`

**路径参数**:
- `roleId`: 角色ID

**请求参数**: 同创建角色

**响应示例**:
```json
{
  "code": 200,
  "message": "角色更新成功",
  "data": {}
}
```

---

### 6.3 删除角色

**接口**: `DELETE /api/admin/roles/{roleId}`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "角色删除成功",
  "data": null
}
```

---

### 6.4 获取角色详情

**接口**: `GET /api/admin/roles/{roleId}`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "roleName": "string",
    "roleCode": "string",
    "permissions": []
  }
}
```

---

### 6.5 获取所有角色

**接口**: `GET /api/admin/roles`

**请求参数**:
- `roleName`: 角色名称（可选）
- `roleCode`: 角色代码（可选）
- `isActive`: 是否启用（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 6.6 分页查询角色

**接口**: `GET /api/admin/roles/page`

**请求参数**:
- `roleName`: 角色名称（模糊查询，可选）
- `roleCode`: 角色代码（模糊查询，可选）
- `isActive`: 角色状态（可选）
- `page`: 页码，从1开始，默认1
- `size`: 每页大小，默认10
- `sortBy`: 排序字段（可选）
- `sortOrder`: 排序顺序，asc或desc，默认asc

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

### 6.7 批量删除角色

**接口**: `DELETE /api/admin/roles/batch`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

### 6.8 获取启用的角色

**接口**: `GET /api/admin/roles/active`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 6.9 启用/禁用角色

**接口**: `PUT /api/admin/roles/{roleId}/toggle-status`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "角色状态更新成功",
  "data": {}
}
```

---

### 6.10 为角色分配权限

**接口**: `PUT /api/admin/roles/{roleId}/assign-permissions`

**路径参数**:
- `roleId`: 角色ID

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "权限分配成功",
  "data": {}
}
```

---

### 6.11 从角色移除权限

**接口**: `DELETE /api/admin/roles/{roleId}/remove-permissions`

**路径参数**:
- `roleId`: 角色ID

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "权限移除成功",
  "data": {}
}
```

---

## 7. 权限管理

**基础路径**: `/api/admin/permissions`

**权限要求**: `SUPER_ADMIN`

### 7.1 获取所有权限

**接口**: `GET /api/admin/permissions`

**请求参数**:
- `permissionName`: 权限名称（可选）
- `permissionCode`: 权限代码（可选）
- `resourceType`: 资源类型（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 7.2 分页查询权限

**接口**: `GET /api/admin/permissions/page`

**请求参数**:
- `permissionName`: 权限名称（模糊查询，可选）
- `permissionCode`: 权限代码（模糊查询，可选）
- `resourceType`: 资源类型（可选）
- `isActive`: 权限状态（可选）
- `page`: 页码，从1开始，默认1
- `size`: 每页大小，默认10
- `sortBy`: 排序字段（可选）
- `sortOrder`: 排序顺序，asc或desc，默认asc

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 7.3 批量删除权限

**接口**: `DELETE /api/admin/permissions/batch`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

### 7.4 根据资源类型获取权限

**接口**: `GET /api/admin/permissions/resource-type/{resourceType}`

**路径参数**:
- `resourceType`: 资源类型

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 7.5 根据角色ID获取权限

**接口**: `GET /api/admin/permissions/role/{roleId}`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 7.6 根据用户ID获取权限

**接口**: `GET /api/admin/permissions/user/{userId}`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 7.7 检查用户是否有特定权限

**接口**: `GET /api/admin/permissions/check/{userId}/{permissionCode}`

**路径参数**:
- `userId`: 用户ID
- `permissionCode`: 权限代码

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 7.8 获取用户所有权限代码

**接口**: `GET /api/admin/permissions/codes/user/{userId}`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": ["permission:read", "permission:write"]
}
```

---

### 7.9 检查用户是否有角色

**接口**: `GET /api/admin/permissions/check-role/{userId}/{roleCode}`

**路径参数**:
- `userId`: 用户ID
- `roleCode`: 角色代码

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": true
}
```

---

### 7.10 获取用户所有角色代码

**接口**: `GET /api/admin/permissions/role-codes/user/{userId}`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": ["USER", "TEACHER"]
}
```

---

## 8. 菜单管理

**基础路径**: `/api/menus`

### 8.1 创建菜单

**接口**: `POST /api/menus`

**权限要求**: `menu:create`

**请求参数**:
```json
{
  "menuName": "string",
  "menuCode": "string",
  "parentId": 0,
  "path": "string",
  "component": "string",
  "icon": "string",
  "sortOrder": 1,
  "isActive": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "菜单创建成功",
  "data": {}
}
```

---

### 8.2 更新菜单

**接口**: `PUT /api/menus/{id}`

**权限要求**: `menu:edit`

**路径参数**:
- `id`: 菜单ID

**请求参数**: 同创建菜单

**响应示例**:
```json
{
  "code": 200,
  "message": "菜单更新成功",
  "data": {}
}
```

---

### 8.3 删除菜单

**接口**: `DELETE /api/menus/{id}`

**权限要求**: `menu:delete`

**路径参数**:
- `id`: 菜单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "菜单删除成功",
  "data": null
}
```

---

### 8.4 获取菜单详情

**接口**: `GET /api/menus/{id}`

**权限要求**: `menu:read`

**路径参数**:
- `id`: 菜单ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

---

### 8.5 获取所有菜单（树形结构）

**接口**: `GET /api/menus`

**权限要求**: `menu:read`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "menuName": "string",
      "children": []
    }
  ]
}
```

---

### 8.6 获取启用的菜单（树形结构）

**接口**: `GET /api/menus/active`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 8.7 根据角色ID获取菜单

**接口**: `GET /api/menus/role/{roleId}`

**权限要求**: `role:read`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 8.8 根据用户ID获取菜单

**接口**: `GET /api/menus/user/{userId}`

**权限要求**: `user:read`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 8.9 为角色分配菜单

**接口**: `POST /api/menus/assign`

**权限要求**: `role:permission:assign`

**请求参数**:
```json
{
  "roleId": 1,
  "menuIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "菜单分配成功",
  "data": null
}
```

---

### 8.10 获取角色的菜单ID列表

**接口**: `GET /api/menus/role/{roleId}/ids`

**权限要求**: `role:read`

**路径参数**:
- `roleId`: 角色ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [1, 2, 3]
}
```

---

## 9. 个人资料

**基础路径**: `/api/user`

**权限要求**: `USER` 或 `SUPER_ADMIN`

### 9.1 获取当前用户信息

**接口**: `GET /api/user/info`

**描述**: 获取当前登录用户的详细信息

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "string",
    "email": "string",
    "phone": "string"
  }
}
```

---

### 9.2 更新个人资料

**接口**: `PUT /api/user/profile`

**描述**: 更新当前登录用户的个人信息

**请求参数**:
```json
{
  "email": "string",
  "phone": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "资料更新成功",
  "data": {}
}
```

---

### 9.3 修改密码

**接口**: `POST /api/user/change-password`

**描述**: 修改当前登录用户的密码

**请求参数**:
```json
{
  "currentPassword": "string",
  "newPassword": "string",
  "confirmPassword": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

---

## 10. 试卷生成

**基础路径**: `/api/papers`

### 10.1 生成试卷

**接口**: `POST /api/papers/generate`

**描述**: 根据配置参数智能生成试卷

**请求参数**:
```json
{
  "title": "string",
  "description": "string",
  "courseId": 1,
  "questionConfigs": [
    {
      "type": "SINGLE_CHOICE",
      "difficulty": "EASY",
      "count": 10,
      "points": 5
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "试卷生成成功",
  "data": {
    "id": 1,
    "title": "string",
    "totalScore": 100,
    "questions": []
  }
}
```

---

### 10.2 分页获取试卷列表

**接口**: `GET /api/papers/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取试卷列表成功",
  "data": {
    "content": [],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 10.3 获取试卷详情

**接口**: `GET /api/papers/{id}`

**路径参数**:
- `id`: 试卷ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取试卷成功",
  "data": {
    "id": 1,
    "title": "string",
    "questions": []
  }
}
```

---

### 10.4 删除试卷

**接口**: `DELETE /api/papers/{id}`

**路径参数**:
- `id`: 试卷ID

**响应示例**:
```json
{
  "code": 200,
  "message": "试卷删除成功",
  "data": null
}
```

---

## 11. 专业管理

**基础路径**: `/api/majors`

### 11.1 创建专业

**接口**: `POST /api/majors`

**请求参数**:
```json
{
  "majorName": "string",
  "majorCode": "string",
  "description": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "专业创建成功",
  "data": {
    "id": 1,
    "majorName": "string"
  }
}
```

---

### 11.2 更新专业

**接口**: `PUT /api/majors/{id}`

**路径参数**:
- `id`: 专业ID

**请求参数**: 同创建专业

**响应示例**:
```json
{
  "code": 200,
  "message": "专业更新成功",
  "data": {}
}
```

---

### 11.3 删除专业

**接口**: `DELETE /api/majors/{id}`

**路径参数**:
- `id`: 专业ID

**响应示例**:
```json
{
  "code": 200,
  "message": "专业删除成功",
  "data": null
}
```

---

### 11.4 批量删除专业

**接口**: `DELETE /api/majors/batch`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "专业批量删除成功",
  "data": null
}
```

---

### 11.5 获取专业详情

**接口**: `GET /api/majors/{id}`

**路径参数**:
- `id`: 专业ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取专业详情成功",
  "data": {
    "id": 1,
    "majorName": "string"
  }
}
```

---

### 11.6 获取所有专业

**接口**: `GET /api/majors`

**描述**: 获取所有启用的专业列表

**响应示例**:
```json
{
  "code": 200,
  "message": "获取专业列表成功",
  "data": []
}
```

---

### 11.7 分页获取专业

**接口**: `GET /api/majors/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 关键词（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取专业分页数据成功",
  "data": {
    "content": [],
    "total": 10,
    "page": 1,
    "size": 10
  }
}
```

---

## 12. 班级管理

**基础路径**: `/api/classes`

### 12.1 创建班级

**接口**: `POST /api/classes`

**请求参数**:
```json
{
  "className": "string",
  "classCode": "string",
  "majorId": 1,
  "grade": "string",
  "description": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "班级创建成功",
  "data": {
    "id": 1,
    "className": "string"
  }
}
```

---

### 12.2 更新班级

**接口**: `PUT /api/classes/{id}`

**路径参数**:
- `id`: 班级ID

**请求参数**: 同创建班级

**响应示例**:
```json
{
  "code": 200,
  "message": "班级更新成功",
  "data": {}
}
```

---

### 12.3 删除班级

**接口**: `DELETE /api/classes/{id}`

**路径参数**:
- `id`: 班级ID

**响应示例**:
```json
{
  "code": 200,
  "message": "班级删除成功",
  "data": null
}
```

---

### 12.4 获取班级详情

**接口**: `GET /api/classes/{id}`

**路径参数**:
- `id`: 班级ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级成功",
  "data": {
    "id": 1,
    "className": "string"
  }
}
```

---

### 12.5 分页获取班级

**接口**: `GET /api/classes/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）
- `majorId`: 专业ID（可选）
- `grade`: 年级（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级列表成功",
  "data": {
    "content": [],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 12.6 获取所有班级

**接口**: `GET /api/classes`

**描述**: 获取当前教师的所有班级

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级列表成功",
  "data": []
}
```

---

### 12.7 根据专业获取班级

**接口**: `GET /api/classes/major/{majorId}`

**路径参数**:
- `majorId`: 专业ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级列表成功",
  "data": []
}
```

---

### 12.8 批量删除班级

**接口**: `DELETE /api/classes/batch`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

## 13. 课程管理

**基础路径**: `/api/courses`

### 13.1 创建课程

**接口**: `POST /api/courses`

**请求参数**:
```json
{
  "courseName": "string",
  "courseCode": "string",
  "description": "string",
  "credits": 3
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "课程创建成功",
  "data": {
    "id": 1,
    "courseName": "string"
  }
}
```

---

### 13.2 更新课程

**接口**: `PUT /api/courses/{id}`

**路径参数**:
- `id`: 课程ID

**请求参数**: 同创建课程

**响应示例**:
```json
{
  "code": 200,
  "message": "课程更新成功",
  "data": {}
}
```

---

### 13.3 删除课程

**接口**: `DELETE /api/courses/{id}`

**路径参数**:
- `id`: 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "课程删除成功",
  "data": null
}
```

---

### 13.4 获取课程详情

**接口**: `GET /api/courses/{id}`

**路径参数**:
- `id`: 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取课程成功",
  "data": {
    "id": 1,
    "courseName": "string"
  }
}
```

---

### 13.5 分页获取课程

**接口**: `GET /api/courses/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 搜索关键词（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取课程列表成功",
  "data": {
    "content": [],
    "total": 15,
    "page": 1,
    "size": 10
  }
}
```

---

### 13.6 获取所有课程

**接口**: `GET /api/courses`

**描述**: 获取当前教师的所有课程

**响应示例**:
```json
{
  "code": 200,
  "message": "获取课程列表成功",
  "data": []
}
```

---

## 14. 班级课程关联

**基础路径**: `/api/class-courses`

### 14.1 创建班级课程关联

**接口**: `POST /api/class-courses`

**请求参数**:
```json
{
  "classId": 1,
  "courseId": 1,
  "semester": "string",
  "academicYear": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "班级课程关联创建成功",
  "data": {
    "id": 1,
    "classId": 1,
    "courseId": 1
  }
}
```

---

### 14.2 更新班级课程关联

**接口**: `PUT /api/class-courses/{id}`

**路径参数**:
- `id`: 关联ID

**请求参数**: 同创建关联

**响应示例**:
```json
{
  "code": 200,
  "message": "班级课程关联更新成功",
  "data": {}
}
```

---

### 14.3 删除班级课程关联

**接口**: `DELETE /api/class-courses/{id}`

**路径参数**:
- `id`: 关联ID

**响应示例**:
```json
{
  "code": 200,
  "message": "班级课程关联删除成功",
  "data": null
}
```

---

### 14.4 批量删除班级课程关联

**接口**: `DELETE /api/class-courses/batch`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "班级课程关联批量删除成功",
  "data": null
}
```

---

### 14.5 删除班级所有课程关联

**接口**: `DELETE /api/class-courses/class/{classId}`

**路径参数**:
- `classId`: 班级ID

**响应示例**:
```json
{
  "code": 200,
  "message": "班级课程关联删除成功",
  "data": null
}
```

---

### 14.6 删除课程所有班级关联

**接口**: `DELETE /api/class-courses/course/{courseId}`

**路径参数**:
- `courseId`: 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "课程班级关联删除成功",
  "data": null
}
```

---

### 14.7 获取班级课程关联详情

**接口**: `GET /api/class-courses/{id}`

**路径参数**:
- `id`: 关联ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级课程关联详情成功",
  "data": {}
}
```

---

### 14.8 获取班级课程列表

**接口**: `GET /api/class-courses/class/{classId}`

**路径参数**:
- `classId`: 班级ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级课程列表成功",
  "data": []
}
```

---

### 14.9 获取课程班级列表

**接口**: `GET /api/class-courses/course/{courseId}`

**路径参数**:
- `courseId`: 课程ID

**响应示例**:
```json
{
  "code": 200,
  "message": "获取课程班级列表成功",
  "data": []
}
```

---

### 14.10 分页获取班级课程关联

**接口**: `GET /api/class-courses/page`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `classId`: 班级ID（可选）
- `courseId`: 课程ID（可选）
- `semester`: 学期（可选）
- `academicYear`: 学年（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取班级课程关联分页数据成功",
  "data": {
    "content": [],
    "total": 20,
    "page": 1,
    "size": 10
  }
}
```

---

### 14.11 获取所有课程列表

**接口**: `GET /api/class-courses`

**描述**: 获取所有可用课程列表，用于添加课程时选择

**响应示例**:
```json
{
  "code": 200,
  "message": "获取所有课程列表成功",
  "data": []
}
```

---

## 15. 仪表盘

**基础路径**: `/api/admin/dashboard`

**权限要求**: `SUPER_ADMIN`

### 15.1 获取仪表盘统计数据

**接口**: `GET /api/admin/dashboard/stats`

**描述**: 获取系统仪表盘的各种统计数据

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalUsers": 100,
    "totalExams": 50,
    "totalQuestions": 500,
    "totalClasses": 20
  }
}
```

---

## 16. 代码执行

**基础路径**: `/api/code`

### 16.1 执行代码

**接口**: `POST /api/code/execute`

**描述**: 在 Docker 容器中安全执行代码

**请求参数**:
```json
{
  "language": "JAVA | PYTHON | CPP | C",
  "code": "string",
  "input": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "执行完成",
  "data": {
    "output": "string",
    "error": "string",
    "executionTime": 100
  }
}
```

---

## 17. 通用工具

**基础路径**: `/api/common`

### 17.1 获取客户端IP地址

**接口**: `GET /api/common/client-ip`

**描述**: 获取请求客户端的真实IP地址，支持代理环境

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "192.168.1.1"
}
```

---

### 17.2 获取服务器时间

**接口**: `GET /api/common/server-time`

**描述**: 获取服务器当前时间，用于客户端时间同步，防止客户端时间被修改

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "serverTime": "2024-01-01T00:00:00",
    "timestamp": 1704067200000
  }
}
```

---

## 18. 用户管理（管理员）

**基础路径**: `/api/admin/users`

**权限要求**: `SUPER_ADMIN` 或 `TEACHER`

### 18.1 创建用户

**接口**: `POST /api/admin/users`

**权限要求**: `SUPER_ADMIN`

**请求参数**:
```json
{
  "username": "string",
  "email": "string",
  "phone": "string",
  "password": "string",
  "roleIds": [1, 2]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "用户创建成功",
  "data": {}
}
```

---

### 18.2 更新用户

**接口**: `PUT /api/admin/users/{userId}`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**请求参数**:
```json
{
  "email": "string",
  "phone": "string",
  "isActive": true
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "用户更新成功",
  "data": {}
}
```

---

### 18.3 删除用户

**接口**: `DELETE /api/admin/users/{userId}`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "用户删除成功",
  "data": null
}
```

---

### 18.4 批量删除用户

**接口**: `DELETE /api/admin/users/batch`

**权限要求**: `SUPER_ADMIN`

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量删除成功",
  "data": null
}
```

---

### 18.5 获取用户详情

**接口**: `GET /api/admin/users/{userId}`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

---

### 18.6 获取所有用户

**接口**: `GET /api/admin/users`

**权限要求**: `SUPER_ADMIN` 或 `TEACHER`

**请求参数**:
- `username`: 用户名（可选）
- `email`: 邮箱（可选）
- `isActive`: 用户状态（可选）
- `role`: 角色（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 18.7 分页查询用户

**接口**: `GET /api/admin/users/page`

**权限要求**: `SUPER_ADMIN`

**请求参数**:
- `username`: 用户名（模糊查询，可选）
- `email`: 邮箱（模糊查询，可选）
- `isActive`: 用户状态（可选）
- `page`: 页码，从1开始，默认1
- `size`: 每页大小，默认10
- `sortBy`: 排序字段（可选）
- `sortOrder`: 排序顺序，asc或desc，默认asc

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [],
    "total": 100,
    "page": 1,
    "size": 10
  }
}
```

---

### 18.8 获取启用的用户

**接口**: `GET /api/admin/users/active`

**权限要求**: `SUPER_ADMIN`

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": []
}
```

---

### 18.9 启用/禁用用户

**接口**: `PUT /api/admin/users/{userId}/toggle-status`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**响应示例**:
```json
{
  "code": 200,
  "message": "用户状态更新成功",
  "data": {}
}
```

---

### 18.10 为用户分配角色

**接口**: `PUT /api/admin/users/{userId}/assign-roles`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**请求参数**:
```json
{
  "roleIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "角色分配成功",
  "data": {}
}
```

---

### 18.11 从用户移除角色

**接口**: `DELETE /api/admin/users/{userId}/remove-roles`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**请求参数**:
```json
[1, 2, 3]
```

**响应示例**:
```json
{
  "code": 200,
  "message": "角色移除成功",
  "data": {}
}
```

---

### 18.12 重置用户密码

**接口**: `PUT /api/admin/users/{userId}/reset-password`

**权限要求**: `SUPER_ADMIN`

**路径参数**:
- `userId`: 用户ID

**请求参数**:
```json
{
  "newPassword": "string"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

---

### 18.13 获取学生列表

**接口**: `GET /api/admin/users/students`

**权限要求**: `TEACHER`

**响应示例**:
```json
{
  "code": 200,
  "message": "获取学生列表成功",
  "data": []
}
```

---

### 18.14 分页获取学生列表

**接口**: `GET /api/admin/users/students/page`

**权限要求**: `TEACHER`

**请求参数**:
- `page`: 页码，默认1
- `size`: 每页大小，默认10
- `keyword`: 关键词（可选）
- `classId`: 班级ID（可选）
- `status`: 状态（可选）

**响应示例**:
```json
{
  "code": 200,
  "message": "获取学生列表成功",
  "data": {
    "content": [],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

---

### 18.15 分配学生到班级

**接口**: `POST /api/admin/users/students/assign`

**权限要求**: `TEACHER`

**请求参数**:
```json
{
  "studentId": 1,
  "classId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "分配成功",
  "data": null
}
```

---

### 18.16 批量分配学生到班级

**接口**: `POST /api/admin/users/students/batch-assign`

**权限要求**: `TEACHER`

**请求参数**:
```json
{
  "studentIds": [1, 2, 3],
  "classId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量分配成功",
  "data": null
}
```

---

### 18.17 将学生移出班级

**接口**: `DELETE /api/admin/users/students/{studentId}/remove-from-class`

**权限要求**: `TEACHER`

**路径参数**:
- `studentId`: 学生ID

**响应示例**:
```json
{
  "code": 200,
  "message": "移出成功",
  "data": null
}
```

---

### 18.18 批量将学生移出班级

**接口**: `DELETE /api/admin/users/students/batch-remove-from-class`

**权限要求**: `TEACHER`

**请求参数**:
```json
{
  "studentIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "批量移出成功",
  "data": null
}
```

---

## 附录

### 题目类型枚举
- `SINGLE_CHOICE`: 单选题
- `MULTIPLE_CHOICE`: 多选题
- `TRUE_FALSE`: 判断题
- `FILL_BLANK`: 填空题
- `SUBJECTIVE`: 主观题
- `PROGRAMMING`: 程序题

### 难度等级枚举
- `EASY`: 简单
- `MEDIUM`: 中等
- `HARD`: 困难

### 考试状态枚举
- `NOT_STARTED`: 未开始
- `IN_PROGRESS`: 进行中
- `ENDED`: 已结束

### 编程语言枚举
- `JAVA`: Java
- `PYTHON`: Python
- `CPP`: C++
- `C`: C

### 角色代码
- `SUPER_ADMIN`: 超级管理员
- `TEACHER`: 教师
- `USER`: 普通用户（学生）

---

## 更新日志

- 2025-11-07: 初始版本文档

---

## 联系方式

如有问题，请联系钛考开发团队。

