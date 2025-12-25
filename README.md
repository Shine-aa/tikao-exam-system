使用说明：
前端文件夹-xiangmu
后端文件夹-manger

导入数据库：
下载安装mysql
下载安装navicat
导入TaiKao/data.sql文件
导入完毕后再次导入TaiKao/exams表结构修改.sql

智能体组卷数据库：
详细请看TaiKao/NEO4J_SETUP.md

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