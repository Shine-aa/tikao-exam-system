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