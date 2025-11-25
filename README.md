# 钛考在线考试系统
墨刀原型地址（待精细化） https://modao.cc/proto/3zU4Zfvt3brq34yd7uKyT/sharing?view_mode=read_only&screen=rbpUy8w80FzUXrH4k #钛考原型 1-分享
## 项目简介

钛考是一个基于Spring Boot + Vue.js的在线考试系统，旨在为高校、企业及认证机构提供安全、智能的在线考试解决方案。

## 核心功能

### 🎯 考试管理
- **智能组卷**：支持按题型、难度、知识点自动组卷
- **考试监控**：实时监控考试状态，防止作弊
- **时间控制**：灵活的考试时间设置和倒计时功能
- **题目乱序**：自动打乱题目和选项顺序，确保考试公平性

### 👨‍🎓 学生端功能
- **在线考试**：支持多种题型（单选、多选、判断、填空等）
- **自动保存**：答题过程自动保存，防止数据丢失
- **成绩查看**：考试结束后可查看详细成绩和错题分析
- **考试历史**：查看历史考试记录和成绩趋势

### 👨‍🏫 教师端功能
- **题库管理**：支持题目分类、标签管理、批量导入
- **智能判卷**：自动判卷和手动判卷相结合
- **成绩分析**：详细的成绩统计和分析报告
- **学生管理**：班级管理和学生信息维护

### 🔐 安全特性
- **JWT认证**：基于Token的安全认证机制
- **权限控制**：细粒度的角色权限管理
- **防作弊**：页面切换检测、答题时间监控
- **数据加密**：敏感数据加密存储和传输

## 技术栈

### 后端
- **框架**: Spring Boot 3.x
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA
- **缓存**: Redis
- **消息队列**: RabbitMQ
- **构建工具**: Maven

### 前端
- **框架**: Vue 3
- **路由**: Vue Router
- **状态管理**: Pinia
- **UI组件**: Element Plus
- **HTTP客户端**: Axios
- **构建工具**: Vite

## 项目结构

```
zonghexiangmu/
├── manger/              # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/manger/
│   │   │   │       ├── config/         # 配置类
│   │   │   │       ├── controller/     # 控制器
│   │   │   │       ├── dto/           # 数据传输对象
│   │   │   │       ├── entity/        # 实体类
│   │   │   │       ├── repository/    # 数据访问层
│   │   │   │       ├── security/      # 安全相关
│   │   │   │       ├── service/       # 业务逻辑层
│   │   │   │       └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/
│   │   └── test/                       # 测试代码
│   └── pom.xml
├── xiangmu/             # 前端Vue项目
│   ├── src/
│   │   ├── api/         # API接口
│   │   ├── assets/      # 静态资源
│   │   ├── components/  # 组件
│   │   ├── router/      # 路由配置
│   │   ├── stores/      # 状态管理
│   │   ├── views/       # 页面
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   └── vite.config.js
└── README.md            # 项目说明文档
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+（可选）

### 数据库配置

默认数据库配置（在 `manger/src/main/resources/application.yml` 中）：
- **数据库名**: `user_auth_db`
- **用户名**: `root`
- **密码**: `123456`
- **端口**: `3306`
- **字符集**: `utf8mb4`

如需修改数据库配置，请编辑 `manger/src/main/resources/application.yml` 文件。

### 后端启动

1. 导入数据库
```sql
-- 创建数据库
CREATE DATABASE user_auth_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构和初始数据
-- 执行 manger/data.sql 文件
```

2. 启动Redis服务（可选）
```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
```

3. 启动后端服务
```bash
cd manger
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd xiangmu
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## API文档

### Swagger UI
访问 http://localhost:8080/swagger-ui/index.html 查看完整的API文档

### 主要功能模块

#### 认证管理
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户退出
- `GET /api/user/info` - 获取用户信息

#### 考试管理
- `POST /api/exams` - 创建考试
- `GET /api/exams/page` - 分页查询考试列表
- `PUT /api/exams/{id}` - 更新考试
- `DELETE /api/exams/{id}` - 删除考试
- `POST /api/exams/{id}/start` - 开始考试
- `POST /api/exams/{id}/end` - 结束考试

#### 学生考试
- `GET /api/student/exams` - 获取学生考试列表
- `GET /api/student/exam/{examId}/paper` - 获取考试试卷
- `POST /api/student/exam/{examId}/submit` - 提交考试答案
- `GET /api/exams/{examId}/student-result` - 查看考试结果

#### 考试判卷
- `GET /api/exams/grading` - 获取判卷考试列表
- `GET /api/exams/{examId}/grading/{studentId}` - 获取学生答案
- `POST /api/exams/{examId}/grading/{studentId}` - 保存判卷结果

#### 题库管理
- `GET /api/questions` - 获取题目列表
- `POST /api/questions` - 创建题目
- `PUT /api/questions/{id}` - 更新题目
- `DELETE /api/questions/{id}` - 删除题目

#### 仪表盘统计
- `GET /api/exams/dashboard/stats` - 获取仪表盘统计数据
- `GET /api/exams/dashboard/activities` - 获取最近活动

### 健康检查
- `GET /api/health` - 服务健康检查

## 开发规范

### 代码规范
- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue.js官方风格指南
- 使用ESLint进行代码检查

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建过程或辅助工具的变动

## 许可证

MIT License

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 联系方式

- 项目维护者: 钛考团队
- 邮箱: taikao@example.com

