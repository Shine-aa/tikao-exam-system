# 钛考在线考试系统
墨刀原型地址（待精细化） https://modao.cc/proto/3zU4Zfvt3brq34yd7uKyT/sharing?view_mode=read_only&screen=rbpUy8w80FzUXrH4k #钛考原型 1-分享
## 项目简介

钛考是一个基于Spring Boot + Vue.js的在线考试系统，旨在为高校、企业及认证机构提供安全、智能的在线考试解决方案。

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
taikao/
├── taikaojava/          # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/example/taikaojava/
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
├── tiakao_vue/          # 前端Vue项目
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
└── redis/               # Redis服务文件
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 后端启动

1. 导入数据库
```sql
-- 创建数据库
CREATE DATABASE taikao CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构和初始数据
-- 执行 taikao-database.sql 文件
```

2. 启动Redis服务
```bash
# Windows
redis-server.exe

# Linux/Mac
redis-server
```

3. 启动后端服务
```bash
cd taikaojava
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd tiakao_vue
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## API文档

### 认证接口

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户退出
- `GET /api/user/info` - 获取用户信息

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

- 邮箱: 2020246016@qq.com


前端文件夹-xiangmu
后端文件夹-manger

导入数据库：
下载安装mysql
下载安装navicat
导入TaiKao/user_auth_db.sql文件

智能体组卷数据库：
neo4j部署请看TaiKao/NEO4J_SETUP.md
之后安装sqlite
导入TaiKao/Spring AI/target/classes/chat-memory.db
进入Spring AI目录
控制台输入mvn spring-boot:run并运行

启动后端：
打开控制台
进入manger目录
输入mvn dependency:resolve并运行
等待jar包全部下载完毕后
输入mvn spring-boot:run并运行

启动前端：
打开控制台
进入xiangmu目录
输入npm install并运行
等待下载完毕后
输入npm run dev并运行

启动redis：
打开redis文件夹
双击启动redis-server.exe

浏览器输入localhost:3000即可

初始学生：用户名user1，密码123456
初始老师：用户名admin1，密码123456
初始管理员：用户名amdin，密码123456
测试老师：用户名04011021，密码04011021
测试学生：用户名20221819401006，密码20221819401006
