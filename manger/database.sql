/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 8.1.0 : Database - user_auth_db
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`user_auth_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `user_auth_db`;

/*Table structure for table `menus` */

DROP TABLE IF EXISTS `menus`;

CREATE TABLE `menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单代码',
  `parent_id` bigint DEFAULT NULL COMMENT '父菜单ID',
  `menu_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'menu' COMMENT '菜单类型：menu-菜单，button-按钮',
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_menu_code` (`menu_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

/*Data for the table `menus` */

insert  into `menus`(`id`,`menu_name`,`menu_code`,`parent_id`,`menu_type`,`path`,`component`,`icon`,`sort_order`,`is_active`,`description`,`create_time`,`update_time`) values 
(1,'钛考管理','system',NULL,'menu','/admin',NULL,'Setting',1,1,'钛考在线考试系统管理模块','2025-09-16 08:50:34','2025-09-30 09:22:50'),
(3,'仪表盘','dashboard',1,'menu','/admin/dashboard','admin/Dashboard','House',1,1,'系统仪表盘','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(4,'用户管理','user_management',1,'menu','/admin/users','admin/UserManagement','User',2,1,'用户管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(5,'角色管理','role_management',1,'menu','/admin/roles','admin/RoleManagement','UserFilled',3,1,'角色管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(6,'权限管理','permission_management',1,'menu','/admin/permissions','admin/PermissionManagement','Lock',4,1,'权限管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(7,'菜单管理','menu_management',1,'menu','/admin/menus','admin/MenuManagement','Menu',5,1,'菜单管理','2025-09-16 08:50:34','2025-09-16 08:50:34'),
(10,'个人资料','user_profile',1,'menu','/user/profile','user/Profile','User',6,1,'个人资料管理','2025-09-16 08:50:34','2025-09-30 09:22:50'),
(11,'新增用户','user:create',4,'button',NULL,NULL,NULL,1,1,'新增用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(12,'编辑用户','user:edit',4,'button',NULL,NULL,NULL,2,1,'编辑用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(13,'删除用户','user:delete',4,'button',NULL,NULL,NULL,3,1,'删除用户按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(14,'新增角色','role:create',5,'button',NULL,NULL,NULL,1,1,'新增角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(15,'编辑角色','role:edit',5,'button',NULL,NULL,NULL,2,1,'编辑角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(16,'删除角色','role:delete',5,'button',NULL,NULL,NULL,3,1,'删除角色按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(17,'新增菜单','menu:create',7,'button',NULL,NULL,NULL,1,1,'新增菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(18,'编辑菜单','menu:edit',7,'button',NULL,NULL,NULL,2,1,'编辑菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00'),
(19,'删除菜单','menu:delete',7,'button',NULL,NULL,NULL,3,1,'删除菜单按钮','2025-09-16 08:50:34','2025-09-16 09:49:00');

/*Table structure for table `permissions` */

DROP TABLE IF EXISTS `permissions`;

CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限代码',
  `resource_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资源类型',
  `action` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '权限描述',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_code` (`permission_code`),
  KEY `idx_permissions_permission_code` (`permission_code`),
  KEY `idx_permissions_resource_type` (`resource_type`),
  KEY `idx_permissions_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

/*Data for the table `permissions` */

insert  into `permissions`(`id`,`permission_name`,`permission_code`,`resource_type`,`action`,`description`,`is_active`,`create_time`) values 
(1,'查看系统配置','system:config:read','system','read','查看系统配置信息',1,'2025-09-04 08:46:45'),
(2,'修改系统配置','system:config:update','system','update','修改系统配置信息',1,'2025-09-04 08:46:45'),
(3,'查看系统日志','system:log:read','system','read','查看系统日志信息',1,'2025-09-04 08:46:45'),
(4,'创建用户','user:create','user','create','创建新用户',1,'2025-09-04 08:46:45'),
(5,'查看用户','user:read','user','read','查看用户信息',1,'2025-09-04 08:46:45'),
(6,'修改用户','user:update','user','update','修改用户信息',1,'2025-09-04 08:46:45'),
(7,'删除用户','user:delete','user','delete','删除用户',1,'2025-09-04 08:46:45'),
(8,'分配角色','user:role:assign','user','update','为用户分配角色',1,'2025-09-04 08:46:45'),
(9,'重置密码','user:password:reset','user','update','重置用户密码',1,'2025-09-04 08:46:45'),
(10,'创建角色','role:create','role','create','创建新角色',1,'2025-09-04 08:46:45'),
(11,'查看角色','role:read','role','read','查看角色信息',1,'2025-09-04 08:46:45'),
(12,'修改角色','role:update','role','update','修改角色信息',1,'2025-09-04 08:46:45'),
(13,'删除角色','role:delete','role','delete','删除角色',1,'2025-09-04 08:46:45'),
(14,'分配权限','role:permission:assign','role','update','为角色分配权限',1,'2025-09-04 08:46:45'),
(15,'导出数据','data:export','data','read','导出系统数据',1,'2025-09-04 08:46:45'),
(16,'导入数据','data:import','data','create','导入系统数据',1,'2025-09-04 08:46:45'),
(17,'数据备份','data:backup','data','read','备份系统数据',1,'2025-09-04 08:46:45'),
(18,'查看菜单','menu:read','menu','read','查看菜单信息',1,'2025-09-16 09:30:00'),
(19,'创建菜单','menu:create','menu','create','创建新菜单',1,'2025-09-16 09:30:00'),
(20,'修改菜单','menu:edit','menu','update','修改菜单信息',1,'2025-09-16 09:30:00'),
(21,'删除菜单','menu:delete','menu','delete','删除菜单',1,'2025-09-16 09:30:00');

/*Table structure for table `role_menus` */

DROP TABLE IF EXISTS `role_menus`;

CREATE TABLE `role_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`),
  CONSTRAINT `fk_role_menus_menu` FOREIGN KEY (`menu_id`) REFERENCES `menus` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_menus_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

/*Data for the table `role_menus` */

insert  into `role_menus`(`id`,`role_id`,`menu_id`,`create_time`) values 
(1,1,1,'2025-09-16 08:50:34'),
(3,1,3,'2025-09-16 08:50:34'),
(4,1,4,'2025-09-16 08:50:34'),
(5,1,5,'2025-09-16 08:50:34'),
(6,1,6,'2025-09-16 08:50:34'),
(7,1,7,'2025-09-16 08:50:34'),
(10,1,10,'2025-09-16 08:50:34'),
(11,1,11,'2025-09-16 08:50:34'),
(12,1,12,'2025-09-16 08:50:34'),
(13,1,13,'2025-09-16 08:50:34'),
(14,1,14,'2025-09-16 08:50:34'),
(15,1,15,'2025-09-16 08:50:34'),
(16,1,16,'2025-09-16 08:50:34'),
(17,1,17,'2025-09-16 08:50:34'),
(18,1,18,'2025-09-16 08:50:34'),
(19,1,19,'2025-09-16 08:50:34'),
(34,6,10,'2025-09-16 08:50:34');

/*Table structure for table `role_permissions` */

DROP TABLE IF EXISTS `role_permissions`;

CREATE TABLE `role_permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`,`permission_id`),
  KEY `idx_role_permissions_role_id` (`role_id`),
  KEY `idx_role_permissions_permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=189 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

/*Data for the table `role_permissions` */

insert  into `role_permissions`(`id`,`role_id`,`permission_id`,`create_time`) values 
(147,1,2,'2025-09-15 09:15:53'),
(148,1,3,'2025-09-15 09:15:53'),
(149,1,8,'2025-09-15 09:15:53'),
(150,1,10,'2025-09-15 09:15:53'),
(151,1,13,'2025-09-15 09:15:53'),
(152,1,7,'2025-09-15 09:15:53'),
(153,1,6,'2025-09-15 09:15:53'),
(154,1,11,'2025-09-15 09:15:53'),
(155,1,4,'2025-09-15 09:15:53'),
(156,1,17,'2025-09-15 09:15:53'),
(157,1,9,'2025-09-15 09:15:53'),
(158,1,15,'2025-09-15 09:15:53'),
(159,1,12,'2025-09-15 09:15:53'),
(160,1,14,'2025-09-15 09:15:53'),
(161,1,16,'2025-09-15 09:15:53'),
(162,1,5,'2025-09-15 09:15:53'),
(163,1,1,'2025-09-15 09:15:53'),
(164,1,18,'2025-09-16 09:30:00'),
(165,1,19,'2025-09-16 09:30:00'),
(166,1,20,'2025-09-16 09:30:00'),
(167,1,21,'2025-09-16 09:30:00'),
(177,6,6,'2025-09-28 20:32:01'),
(178,6,17,'2025-09-28 20:32:01'),
(179,6,9,'2025-09-28 20:32:01'),
(180,6,15,'2025-09-28 20:32:01'),
(181,6,16,'2025-09-28 20:32:01'),
(182,6,5,'2025-09-28 20:32:01'),
(183,7,4,'2025-09-30 09:22:05'),
(184,7,17,'2025-09-30 09:22:05'),
(185,7,9,'2025-09-30 09:22:05'),
(186,7,15,'2025-09-30 09:22:05'),
(187,7,16,'2025-09-30 09:22:05'),
(188,7,5,'2025-09-30 09:22:05');

/*Table structure for table `roles` */

DROP TABLE IF EXISTS `roles`;

CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色代码',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '角色描述',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`),
  KEY `idx_roles_role_code` (`role_code`),
  KEY `idx_roles_is_active` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

/*Data for the table `roles` */

insert  into `roles`(`id`,`role_name`,`role_code`,`description`,`is_active`,`create_time`,`update_time`) values 
(1,'超级管理员','SUPER_ADMIN','拥有系统所有权限的超级管理员',1,'2025-09-04 08:46:45','2025-09-15 09:15:53'),
(6,'普通用户','USER','普通用户',1,'2025-09-08 08:42:18','2025-09-28 20:32:02'),
(7,'教师','TEACHER','老师，可以管理普通用户',1,'2025-09-30 09:22:06','2025-09-30 09:22:06');

/*Table structure for table `sms_codes` */

DROP TABLE IF EXISTS `sms_codes`;

CREATE TABLE `sms_codes` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '验证码类型：register-注册, login-登录',
  `is_used` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已使用',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `ip_address` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  PRIMARY KEY (`id`),
  KEY `idx_phone_type` (`phone`,`type`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信验证码表';

/*Data for the table `sms_codes` */

insert  into `sms_codes`(`id`,`phone`,`code`,`type`,`is_used`,`expire_time`,`create_time`,`used_time`,`ip_address`,`user_agent`) values 
(1,'17531307055','7983','register',0,'2025-09-08 10:48:08','2025-09-08 10:43:08',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(2,'17531307055','4913','register',0,'2025-09-08 10:49:25','2025-09-08 10:44:25',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(3,'17531307055','1710','register',0,'2025-09-08 10:50:26','2025-09-08 10:45:26',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(4,'17531307055','8907','login',0,'2025-09-08 10:51:27','2025-09-08 10:46:27',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(5,'17531307055','1912','login',0,'2025-09-08 10:52:28','2025-09-08 10:47:28',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(6,'17531307055','6346','login',0,'2025-09-08 10:55:19','2025-09-08 10:50:19',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(7,'17531307055','9074','login',0,'2025-09-08 10:58:29','2025-09-08 10:53:29',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(8,'17531307055','8607','register',0,'2025-09-08 11:01:57','2025-09-08 10:56:57',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(9,'17531307055','1078','login',0,'2025-09-08 11:02:57','2025-09-08 10:57:57',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(10,'17531307055','9542','register',0,'2025-09-28 20:39:58','2025-09-28 20:34:58',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0'),
(11,'17531307055','8303','register',0,'2025-09-30 08:46:27','2025-09-30 08:41:27',NULL,'127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0');

/*Table structure for table `user_roles` */

DROP TABLE IF EXISTS `user_roles`;

CREATE TABLE `user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_roles_user_id` (`user_id`),
  KEY `idx_user_roles_role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

/*Data for the table `user_roles` */

insert  into `user_roles`(`id`,`user_id`,`role_id`,`create_time`) values 
(1,1,1,'2025-09-04 08:46:45'),
(6,2,6,'2025-09-08 08:42:33'),
(8,6,6,'2025-09-08 11:00:40'),
(10,8,6,'2025-09-30 08:41:39');

/*Table structure for table `user_sessions` */

DROP TABLE IF EXISTS `user_sessions`;

CREATE TABLE `user_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话令牌',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '刷新令牌',
  `ip_address` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  `expires_at` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_session_token` (`session_token`(255)),
  KEY `idx_refresh_token` (`refresh_token`(255)),
  KEY `idx_expires_at` (`expires_at`),
  KEY `idx_is_active` (`is_active`),
  CONSTRAINT `user_sessions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=92 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户会话表';

/*Data for the table `user_sessions` */

insert  into `user_sessions`(`id`,`user_id`,`session_token`,`refresh_token`,`ip_address`,`user_agent`,`is_active`,`expires_at`,`create_time`) values 
(1,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTQ3MTMyLCJleHAiOjE3NTcwMzM1MzJ9.wopD_Lfeo55J3DbYc4PXFDweIyifbMu574LkxioVSiINawwJc6pBtw4JNXP4yW1q','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTQ3MTMyLCJleHAiOjE3NTk1MzkxMzJ9.wUSMvh-ImoZMnXiT4ZtfUXd3MCCQaw55qg2v6flEKMuBnyWHXI33P01D0klppSAA','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 08:52:13','2025-09-04 08:52:13'),
(2,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTUyNDIxLCJleHAiOjE3NTcwMzg4MjF9.g7MbPM1ugAPRL9KdkhCpkBi80JmEGdBQqhl7ps2ON_GT7SPWyNXuOEASZeQu6ylp','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTUyNDIxLCJleHAiOjE3NTk1NDQ0MjF9.kbgAAjIUI9vhxfUUBv5ELa3TcPflsZXQNftMrC2iUoTnai-xhlMfJcBmsGO5ZoqJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 10:20:22','2025-09-04 10:20:22'),
(3,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0MjQwLCJleHAiOjE3NTcwNDA2NDB9.VWce0Cfmwnc4mf_7wQk9QH2_Qfhb4wCdxXhRJOUspVNwh0EW2Hoa0yb4PDzTwqZt','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0MjQwLCJleHAiOjE3NTk1NDYyNDB9.zG-CF5PDmAYdBPeN743o0Bd1uozF5TNiv1JgzheDn_V_DJe_m8Xne3XN0e7silEU','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 10:50:41','2025-09-04 10:50:41'),
(5,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0OTgxLCJleHAiOjE3NTcwNDEzODF9.IASMeRBALJTyDZPEBsvxgScDKceXZy8Er0sYJgEKFCxDE7SxUSt2LABH0ivriG1Y','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU0OTgxLCJleHAiOjE3NTk1NDY5ODF9.uHWP4xIUeHLAO5tVEoEQYl1xki-FFM7DG8aBY1vWW9XLz_u4a1ZRttl_exx-cR8c','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 11:03:02','2025-09-04 11:03:02'),
(7,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU1MDU2LCJleHAiOjE3NTcwNDE0NTZ9.7_JubNVcr9rCwp8TfsLCoVZsp2MJiSZWC3Uzpx1Gzgr8OzSghZgr4NuZ4tCdlYIE','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU2OTU1MDU2LCJleHAiOjE3NTk1NDcwNTZ9.iyTtsg-6Wxz7l-kqJmk3aUsP7vHZW6KIoEZhH0oU7zPdNRIjxXfhiL14mmxBQzvu','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-04 11:04:16','2025-09-04 11:04:16'),
(8,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MDM2ODI3LCJleHAiOjE3NTcxMjMyMjd9.7SZ9NRbigvX5KuhjOGP7poM7w2DccYW_5b9lj2O2znNh9ineoTqbDvdmwwYtJeA8','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MDM2ODI3LCJleHAiOjE3NTk2Mjg4Mjd9.cAvX5Kszc_lEdplzz7gS7yxXhqKv_ptTWs__h_klrEgO-KNrSz_GZuPZYdGQJk__','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36 Edg/139.0.0.0',0,'2025-10-05 09:47:07','2025-09-05 09:47:07'),
(9,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxNzc4LCJleHAiOjE3NTczNzgxNzh9.KKhKcaHNp1zUu5W846_pVxfUFwiIRuwau5d_u_JqpIeBBwByBGjb0UM9LRg1VzVy','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxNzc4LCJleHAiOjE3NTk4ODM3Nzh9.fBvYpFnFEKximiQfS1KaBGj222SCYeiBjP9bUQHHarpMutOuh_tkvsRGEMpPACsb','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:36:18','2025-09-08 08:36:18'),
(11,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxOTYzLCJleHAiOjE3NTczNzgzNjN9.EpnknYj0hf6QOukzLJFbl0xvUtJ5U4DzGJA3QgwiwYMKjL8FCuHz5E4cmXfQqXMs','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkxOTYzLCJleHAiOjE3NTk4ODM5NjN9.kiy8bG15_19t7WKHGxaxP7iIGIOkzKQSoG_jF5-O7Nz6fQKEoIVtfz1OwFVtwqvJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:39:23','2025-09-08 08:39:23'),
(12,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNjA5LCJleHAiOjE3NTczNzkwMDl9.yrrgNLPBvrp2jbSkWVvmzlHl0vU03AMMvlmZgrvyCa1CWINSldNkfugVQ5Zqv-UA','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNjA5LCJleHAiOjE3NTk4ODQ2MDl9.vrKZ18F5iuzhrmICwva8W4HXVkDfRf4lMAMlAxvUix3axWHHjA3G4TS2-6v1yqD9','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:50:09','2025-09-08 08:50:09'),
(13,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNzUxLCJleHAiOjE3NTczNzkxNTF9.xwlDvsOmFyDMqNFfAFVO12JzPOpOkXNYIS1gQUvF_i8WRlVnqnngrphkot2Qk-J8','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkyNzUxLCJleHAiOjE3NTk4ODQ3NTF9.UFYtsw_sOh1NAdNS6QL1lP3FY6Doac_DB-IEGf9dqst99kPWvA04mhvfK_ijEnKO','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 08:52:32','2025-09-08 08:52:32'),
(14,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkzMzg2LCJleHAiOjE3NTczNzk3ODZ9.4K9yhnboor3kNOV4C1eek5jRiyT5QPjbCwrwl3gw4Wg3ZdjKYyHmfG1hFEwUeDi7','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MjkzMzg2LCJleHAiOjE3NTk4ODUzODZ9.syPipG95bymMk8_erEPP_3LdTooJ3tYMzTPi_oPpnswE_g8k3_beI1wfwGpgWHjt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:03:07','2025-09-08 09:03:07'),
(15,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTM0MDYsImV4cCI6MTc1NzM3OTgwNn0.sV9nFV9WKoYhKRMfMPH_Knd8222NZuYLPd3XMi75OQODtBiKO8qdIVw16UhF32m_','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTM0MDYsImV4cCI6MTc1OTg4NTQwNn0.RS8HDAHyfNzQOI8_E-hwhRGA3Q2WQlkJWUfwCtjsNQasuAqTrr1qr-DUFJYo-vgK','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:03:27','2025-09-08 09:03:27'),
(16,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQwMzksImV4cCI6MTc1NzM4MDQzOX0.NAzhgW0IVibSOCjwun_jnPBGHIxO6Z8AgHUGF-bHwfgcoyDUFoVJ6AeDMEDLQBIK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQwMzksImV4cCI6MTc1OTg4NjAzOX0.P1Uxb6GHroBptC3SZC19ZkD407UgGEpqA9736Jl00SCXS0k4HsXIijD6qIVsAN_l','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:13:59','2025-09-08 09:13:59'),
(17,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQxMTksImV4cCI6MTc1NzM4MDUxOX0.TfD7pfTObWc4fURYdBPixrYUJXDCven5unxCSi4CiFyHrG8z-o15cpyz8ALnzGSN','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQxMTksImV4cCI6MTc1OTg4NjExOX0.8GYcY6q8Fla_owEiS6YBl4xtlGLtt7rbdbGJ2tCNOI2ZKmWHdK3aoU1LzISYq39L','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:15:20','2025-09-08 09:15:20'),
(18,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQ1MzYsImV4cCI6MTc1NzM4MDkzNn0.aJQlJ58PhFD0MdNPnu9kkMuJ7MfyM0bAtLWaCz_mGMvy8W9owzk7AQTjPPi7Ngi6','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTQ1MzYsImV4cCI6MTc1OTg4NjUzNn0.2aKF3FJXOAZYMnMhbo5B-ZB6ay3vowT0eLi1hYpTdaR4B2puYp2XhGsrhoMcofAv','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:22:16','2025-09-08 09:22:16'),
(19,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MDYxLCJleHAiOjE3NTczODE0NjF9.MjfKw0KVcIeq0Py9PT7OLweOSx6fvO0mz3pzYE-echL1PMr3YYHWIp7N2RSU9AuA','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MDYxLCJleHAiOjE3NTk4ODcwNjF9.8a8eKlwzdgcjNUYPgCyHq8ib90OharjPM-5URT9XJTlgSO7J7BF8eG-BGWh5-yCC','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 09:31:01','2025-09-08 09:31:01'),
(20,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUxOTcsImV4cCI6MTc1NzM4MTU5N30.M30QypZ2o6jhzHUspzDA2olvoECFjCf9-a5YwiOBbpculGU-NiBo6IoPlkgMCsiN','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUxOTcsImV4cCI6MTc1OTg4NzE5N30.UGlJb6p9lZ9IgRLf5WmSIF2NAdY0qbEtVC78SHSAiTfBTfZQCsA8yE2YEndkfy-Z','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:33:18','2025-09-08 09:33:18'),
(21,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MzU4LCJleHAiOjE3NTczODE3NTh9.g3g8f70oaP5n8bwtb9qHz6Le8WVK4KAJj0Ip6VJ8bA4nGIbTQKb32K8Iof1-M9FL','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1MzU4LCJleHAiOjE3NTk4ODczNTh9.9_AW1fzH3tBDQALoxtZhbc22Fohjrv5MMjEoagqke1IGun95Oqis7yKYp01CL9Ra','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:35:59','2025-09-08 09:35:59'),
(22,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUzNzIsImV4cCI6MTc1NzM4MTc3Mn0.nAigvq9mzWyNHREzha5wnXABRHu8z2KG5qoxNNargie2cR2PqUXOzyQcflvJ4vMn','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTUzNzIsImV4cCI6MTc1OTg4NzM3Mn0.0PsdceN5sMFh42Vr21mu9zJ0ToUKJLjPDlzGvvjfRyDd6XYAFLkPQaJGmFglSwIL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:36:13','2025-09-08 09:36:13'),
(23,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1NTE4LCJleHAiOjE3NTczODE5MTh9.rDrY6Fr1afLAxlaQg16xr_Rw2P0WD4p8_ec0HnNfENOW2Tyjh-9hjItnyUFB9936','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mjk1NTE4LCJleHAiOjE3NTk4ODc1MTh9.wkm_lQI5RGR-z5_3RkYTTCbzVkrvAjSJnB_zwuV4QsnqBvx_O96QNQKLunSubYds','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 09:38:38','2025-09-08 09:38:38'),
(24,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTU1MzksImV4cCI6MTc1NzM4MTkzOX0.S-ULS6cCdl8DOyj0e8D4oYH9lLHzg-awnOk6FeZ-rXvDPBcCSA01CiDicFRmPRmW','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTcyOTU1MzksImV4cCI6MTc1OTg4NzUzOX0.QT31znIFWfMgfumNzdN2XG50DS6Stp3BeZ6Uc1IQqocC5CWCpR295Gr5N2IEJghL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 09:39:00','2025-09-08 09:39:00'),
(28,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMTgyLCJleHAiOjE3NTczODY1ODJ9.bJ8VOPeECaYceutdDb5WpVhJni8d2B_lRP3y-u8DxdMYpIHBnNo9LXpYYRAWm35C','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMTgyLCJleHAiOjE3NTk4OTIxODJ9.kchZqhN9P9htABNtXLbiQwU57m1RzJIHPEywnE84UcbwfTaZPdSWTIe9ToXKZMiB','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 10:56:23','2025-09-08 10:56:23'),
(29,6,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1NzMwMDI4NSwiZXhwIjoxNzU3Mzg2Njg1fQ.2i7g4S4ZIC3d41ibd8ecnctInJPxPgriGwPMZhS7Z6l3q2fdS7QguBnT50iaxbp6','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjYsInN1YiI6ImFkbWluMSIsImlhdCI6MTc1NzMwMDI4NSwiZXhwIjoxNzU5ODkyMjg1fQ.rQTTeC8HXRtYXmZFInPVgKgTy936gx0gTFLRoGtG_L0QdIQnDJtS8wjuuGjcyuPR','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 10:58:06','2025-09-08 10:58:06'),
(30,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMjk3LCJleHAiOjE3NTczODY2OTd9.7heR9gAbYeCmgrjAqYo-NrCJgTFrDzPj3hocQAdJX9Ym84wkHZhl6hEVfBxOnsHi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMjk3LCJleHAiOjE3NTk4OTIyOTd9.6mTKpoM2Jg3tDWVEskyHk8QOQjtnpp29ivAP7aSLoanpW9XUv5AaXt3mOWrAB_7x','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-08 10:58:18','2025-09-08 10:58:18'),
(31,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczMDAzNzksImV4cCI6MTc1NzM4Njc3OX0.DIXFb1cZaMBt1WWCEaAy4QXG9cytSUroiMo-qlCDZuSd869Hn8zRPuwbPkY-B-nT','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczMDAzNzksImV4cCI6MTc1OTg5MjM3OX0.Tf8PHkLFJKKgXhcFKtgjIfb8bHBLmt33CuaC64e_0qyc38kjdqG84cBPwcilKUac','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 10:59:39','2025-09-08 10:59:39'),
(32,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMzk5LCJleHAiOjE3NTczODY3OTl9.7dyjxQh-0cABwJQBC227e8TDcx6HRu660rgMXPvTd06WDn08bJZg8dXGE9lIotrJ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzAwMzk5LCJleHAiOjE3NTk4OTIzOTl9.Kllb4waIb6TGRp-XThhKl82Hxa2Z9GDUCUKPcL0an9oe49gigX7bhY6jfplwhLGS','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-09 11:00:00','2025-09-08 11:00:00'),
(33,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzg5MjcsImV4cCI6MTc1NzQ2NTMyN30.iIh1DvCgRJLCygi7Eul_PwgtXVYAvEiq1iEXyCVObXuEvspS7wgv1vcCB-q4vYhv','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzg5MjcsImV4cCI6MTc1OTk3MDkyN30.Ys8eoi7l48BU71vN6pkaev3xkn7dgqJwl2MSI53p3O8gB2KCE7KMlBNv0ZS7ky_M','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:48:47','2025-09-09 08:48:47'),
(34,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc4OTY3LCJleHAiOjE3NTc0NjUzNjd9.Gx-NUOodQuHVIn_vjwEWElVbDX7N4T3UfgJtPTpxi0HDyx5pkuhuc0tsPeFGIZdR','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc4OTY3LCJleHAiOjE3NTk5NzA5Njd9.R294nyIjitTjScjzgDyONAfA25M8ugsT77Ogq57u78f1Yfzv21UAgBASS3rIcZsJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:49:28','2025-09-09 08:49:28'),
(35,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzkzNTMsImV4cCI6MTc1NzQ2NTc1M30.IDztpyxP6rIZJAo1kMGghGee-1zqNi2x94_8DkPq3jUyLuA11yeNUFU0YrW1SEEd','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzkzNTMsImV4cCI6MTc1OTk3MTM1M30.Tmhh_PGspJD9-y0vjDW7J9HSqWYaklgFH3WblaxyNGj6ugdKuwvjJPkNa_b2Rk_6','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:55:54','2025-09-09 08:55:54'),
(36,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc5Mzc2LCJleHAiOjE3NTc0NjU3NzZ9.9ffqUeXqcbF4BaXlB9FknlV4ohJM9ttpyapkkXyJEuYN_Sn_uAYsvNJTD03fAH3T','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3Mzc5Mzc2LCJleHAiOjE3NTk5NzEzNzZ9.du_9TWE4lZk_KvB0sgT_VE7M6v6Fhtch5eEn4cKjYB3r2HvwJcEyxMHh_eB86tJJ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:56:16','2025-09-09 08:56:16'),
(37,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzk1ODEsImV4cCI6MTc1NzQ2NTk4MX0.e-YW8KLRoN3AMGchG-pdZnMbLZrRmARcQuGRZUcHceEIlnPhkPTRNWwF7H49i_7Z','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczNzk1ODEsImV4cCI6MTc1OTk3MTU4MX0.gCYu-40BSRjRZduAebiYw2IcFBJrx_HUeRzo4kUAhC-g5M4S9uX13enE6TzRpTOY','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 08:59:42','2025-09-09 08:59:42'),
(38,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODA4ODcsImV4cCI6MTc1NzQ2NzI4N30.iDEzZYeBxE1CHf79kxtu1B4IoUHmuhck_lVeT-1ykraf53MXBVRPAMbBzmSz01aM','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODA4ODcsImV4cCI6MTc1OTk3Mjg4N30.M75UMqA2ftjfIDN2DGxKokOKB653PS5L24EncgK9fDPhWP5ybH0w6pT3ehghWYzt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:21:27','2025-09-09 09:21:27'),
(39,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzgxMTE1LCJleHAiOjE3NTc0Njc1MTV9.C2RI7Jcw1NuvOywyx_bGqdg0Po-ZIbU6RvKvBf8-iF0tQgDsssNnZyB2PgPAyZpC','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3MzgxMTE1LCJleHAiOjE3NTk5NzMxMTV9.BDPHtBU0Uy1WR__ze2MhjYw0dbE77cstlcjJFE0xqtFPMBEcaWhxV6nAXMzEfU_H','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:25:15','2025-09-09 09:25:15'),
(40,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODExMzgsImV4cCI6MTc1NzQ2NzUzOH0.rgIh0N6E0JnZmmAySBU0jgEmIalyccclo-CITf21h_jANcM1zYCZi8RFBgLTCpA9','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTczODExMzgsImV4cCI6MTc1OTk3MzEzOH0.H2rFVFK38J_GYOpu4apO-IWLLE-mRUxCuV-sQ_vot0vjMwJQEcnmnyEO06FVGR-j','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-10 09:25:39','2025-09-09 09:25:39'),
(41,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY1MzA0LCJleHAiOjE3NTc1NTE3MDR9.aOowgbfy8vCjR8OSuimUqgLNDbD3dxSHtxhro08X_aNLwJZ3YC-hagWIQI7yWjXK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY1MzA0LCJleHAiOjE3NjAwNTczMDR9.K9Ze-bBbEyPZ-v9jhrRqqaM2TN63MK8ZBWw_Kc3-YTWtWJB7mxieUW7_D_O7-tOl','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 08:48:24','2025-09-10 08:48:24'),
(42,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NjUzMjgsImV4cCI6MTc1NzU1MTcyOH0.viwTZrt6epn-Cp0tiR8doLjX_FNQJKh-oVeBGa3k07h-b9r1iirvfP5ZJBOw8G52','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NjUzMjgsImV4cCI6MTc2MDA1NzMyOH0.45_QYJcyOSAZlZjTSf5xtqLHzRVLrstH3uXZJ4-YWq9DlJIklRQDb1HEgXK0U3_C','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 08:48:49','2025-09-10 08:48:49'),
(43,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY4NDYxLCJleHAiOjE3NTc1NTQ4NjF9.C10Z8_7S-7Y7plJtXOMqJx7QuWLZtYCDFvnPEhzr4Dg1aXLJdDPB-LaBhXwJriBZ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDY4NDYxLCJleHAiOjE3NjAwNjA0NjF9.GK1CpqeC4b1qkEguzagjkaYuNxT3e3fbtTp1SBnPuG2CW53r84mKDq5GotOm2E6f','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 09:41:02','2025-09-10 09:41:02'),
(44,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0Njg0NzYsImV4cCI6MTc1NzU1NDg3Nn0.tebDV_CX-8ETqepGckTStk45FN8Uk7e_OAzc3qTHSAQ81BrudrWECYbPdz5YVwcf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0Njg0NzYsImV4cCI6MTc2MDA2MDQ3Nn0.XKERgZVcq4IVuygzWIisXwghRb43Pwc8PiISvoprr0pfXD2pO4hYH1ZkHVJ4jX-G','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 09:41:17','2025-09-10 09:41:17'),
(45,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcwNTgyLCJleHAiOjE3NTc1NTY5ODJ9.vNso6atgwY7hiHhOA6izQ29P4wuBt4mRVPbouE7GWg4nik_tyLPJCdSwFeqzdq8P','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcwNTgyLCJleHAiOjE3NjAwNjI1ODJ9.xbyxQaD5h-uzinlp5YLviz8aQyQJQuRRxJxboquymLMO52TSb6KKemhs6BF0tcQ4','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:16:23','2025-09-10 10:16:23'),
(46,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzA2MDQsImV4cCI6MTc1NzU1NzAwNH0.gGNal-fH5DlP_Gw_C02sWy_UuovssltnOhydm5HmU8wSCo74zsFb4qbo91hCbPXQ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzA2MDQsImV4cCI6MTc2MDA2MjYwNH0.rDa5bs8XmWgTn1EHPXbRYF7HrWACYn-u9pl9EaIQlgwQ3l4bW-4KXEtkcmwu4UFH','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:16:44','2025-09-10 10:16:44'),
(47,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMDg1LCJleHAiOjE3NTc1NTc0ODV9.PBo_mkTkGnsDuy2vhZZGqh9X-0SzwqKvHFH_HXaelencmX6NQXRnJGHwTQBn7XFf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMDg1LCJleHAiOjE3NjAwNjMwODV9.UfvO9PddSTgVnSFIZSnmGngE2PMYwAd01YH4vNyElh2tD0bbD5cWjOTIOEXH8_DQ','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:24:46','2025-09-10 10:24:46'),
(48,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMjg1LCJleHAiOjE3NTc1NTc2ODV9.cYN1AXqZSvoXK1WRispiMo8yoxRLqPwlsxAoIQYWg-PH4FlYneDzl8CDGrk5rSQZ','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxMjg1LCJleHAiOjE3NjAwNjMyODV9.8Kzq_7TZCQ_Nda11kHi2_e36_-INYRUoKSDm1rrZvjDTSu-ItNIiFVEPFtqzCTo-','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:28:05','2025-09-10 10:28:06'),
(49,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxNzkxLCJleHAiOjE3NTc1NTgxOTF9.2V6-UgB75Zveu70nvDxvdv59kjk3WEpIk_DlBMh_DdytQTJzdOaKqyn0OZye9QND','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcxNzkxLCJleHAiOjE3NjAwNjM3OTF9.lc4c4lDXbsBhfl8DY56rM2GozWp2T1rJMDYRJu7iomhU4u7cAUA4WFhhng2NBDZf','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:36:32','2025-09-10 10:36:32'),
(50,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcyMjEzLCJleHAiOjE3NTc1NTg2MTN9.o5Gc2z60ERbNtVABaaebWVW7cUnYGsFIX9ebXWX2V-zUWq1baLQkc41G29gyWmTn','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NDcyMjEzLCJleHAiOjE3NjAwNjQyMTN9.JutKTHwzSEYOAj5UZTFogr2n1U3Rr4wtJyUW3IsUQPXKesp3Zx7A4Fvam5b--rtP','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:43:33','2025-09-10 10:43:33'),
(51,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzIyMjUsImV4cCI6MTc1NzU1ODYyNX0.2cTAig0WeA3KR8IxohzytHlHCI1OhrnWHUwdT3w61adK94oroKfNYjH7rn5C5LUf','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc0NzIyMjUsImV4cCI6MTc2MDA2NDIyNX0.MktTHnx_HSOu5-fcxVjwsZWMoIxwsuHON-FTFvgVTN9T-enHc-OzjNNfXWm9VXby','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-11 10:43:45','2025-09-10 10:43:45'),
(52,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMDE0LCJleHAiOjE3NTc2Mzg0MTR9.5m6ryz-Y4niH3h6YEkq2hmklkAaP8KHg5zLajHWzlStgI0rjUOU-MM3iwPCcpFDp','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMDE0LCJleHAiOjE3NjAxNDQwMTR9.0E4BdrHo_do75Ch9rqwjyz7pCYQCWE5Xw5_tnneiD9ROM7j9m56iwP_2jT5vxPn_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:53:34','2025-09-11 08:53:34'),
(53,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMzM0LCJleHAiOjE3NTc2Mzg3MzR9.xfQvk_zdX3cxFGzpRZzdT4Iq5TzWO2M_0bQAvFKri182aJYMA8hcMSrPjHlo3VaY','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTUyMzM0LCJleHAiOjE3NjAxNDQzMzR9.VYpbkCDEb_UIBwSLfDOLy-wcbDxMZbHAxy-G7qLJWqlzSazZ9uCjCIrC05u_3rB5','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:58:55','2025-09-11 08:58:55'),
(54,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc1NTIzNTAsImV4cCI6MTc1NzYzODc1MH0.ZI6KD5KahiHFqKBGWvWepMWGxegsgbHlUydtpSh993962uw80x0A4yIv_M2Tds3A','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc1NTIzNTAsImV4cCI6MTc2MDE0NDM1MH0.1G_Gbb7ch0V3uU1NcQrcF-RGoURJ5goADKEU0A4yyhgHDNFFC7u4IpWCmnD9JsWT','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 08:59:11','2025-09-11 08:59:11'),
(55,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTU0MTU4LCJleHAiOjE3NTc2NDA1NTh9.jjeDTPVcaZCfB0TSBE25BfhuM5PLCsriF4dqb2t0iWu10mya30LZslnJUlrB8lGc','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3NTU0MTU4LCJleHAiOjE3NjAxNDYxNTh9.LEzAZEixqugRqjKgHTZSj1y-d5laPXF7hWb0b3tY_Map7YsJOdLyFoQd2YlUqqsq','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-12 09:29:18','2025-09-11 09:29:18'),
(56,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzc3NDAsImV4cCI6MTc1NzcyNDE0MH0.8Zx83vfjp9gc4WIEEX5J5uEb0wB_iXkRYT3IL_xzKcyf7N-7tjJDjKBf3VjcDJIc','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzc3NDAsImV4cCI6MTc2MDIyOTc0MH0.viGg0XmEOcNzHjLMX2LCvlVzk656ygeKArqDt1wyyPsoRMPS4jqFSnpqG1xda6Dd','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-13 08:42:21','2025-09-12 08:42:21'),
(57,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzk1NDAsImV4cCI6MTc1NzcyNTk0MH0.Gf_5g_z-AlYO_af6kbInDa4348lHeu4rnM1IAOc0iwMnp2Z4w4wReqvkbSc5IcC4','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc2Mzk1NDAsImV4cCI6MTc2MDIzMTU0MH0.ArfO-x9QTzq-Y55lTQNtXSw2rMYh4XLA-hAVerMY4hgFHk5p2kjmdS6OY_A5oGxb','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-13 09:12:20','2025-09-12 09:12:20'),
(58,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg0NDcsImV4cCI6MTc1Nzk4NDg0N30.ukXU81b7Yaz8c6zaKVXTBbLzf01KdGxH3gAmXzEW0k9UKKPCXn0YK55rvlMw8wAF','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg0NDcsImV4cCI6MTc2MDQ5MDQ0N30.-hKWKb_tuuGp4JdJ5Ipcl2T10TRRXw19PLfeTb6mPDIxPmQ8DU-pY40_8LAw60Yt','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:07:28','2025-09-15 09:07:28'),
(59,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg4MjMsImV4cCI6MTc1Nzk4NTIyM30.HQIA3c8_8ArXCqmJljfctyc3Shf5dcaYyppBKaKq_V7VMaTod7eiRc-Xx2438x1J','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc4OTg4MjMsImV4cCI6MTc2MDQ5MDgyM30.mLiVAmu5igMacorVe7AHArP7-VBCZ0hczl-3z27nQNx07wfoidFydSqg3xP9B3HP','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:13:44','2025-09-15 09:13:44'),
(60,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3ODk4OTIzLCJleHAiOjE3NTc5ODUzMjN9.RnbHaA8W2lsYvLhzUqqh6TV34cTVsXbRQ7q2VS5XkP30APSIZt7WOw9sAoo5qqsF','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3ODk4OTIzLCJleHAiOjE3NjA0OTA5MjN9.bcf4WJAQzHnqmLvW_SX1jxv_U0pduHYqy6UjNazEpC0r5P4Vx0jZBVE8xwibF64T','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 09:15:23','2025-09-15 09:15:23'),
(61,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTA2Njg4LCJleHAiOjE3NTc5OTMwODh9.6HUuOU4e3ubZ6BeVcOzU94uq4eR8AMgcqo86nB7LO5VyVnJ_Coutof1vBDYeoFck','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTA2Njg4LCJleHAiOjE3NjA0OTg2ODh9.3CKpouJdXhnOPxHVddMvAVBtDpMKCVZipOQEEfGgD7jaDTOeUUqAkOnpolZS1ICf','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 11:24:49','2025-09-15 11:24:49'),
(62,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5MDY3MTEsImV4cCI6MTc1Nzk5MzExMX0.5cFxRFO_u6oOoGQmX6zVh4AishcaFKSa9vhg11gzER_i-nYBbM2N8srMAWah-dEC','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5MDY3MTEsImV4cCI6MTc2MDQ5ODcxMX0.U3oHETl7veEG86hbF0r8zv7zssKOiXNWshHO5GuT1hy_2Cpa4dRFeFxModDnjoo_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-16 11:25:12','2025-09-15 11:25:12'),
(63,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTgzMDY2LCJleHAiOjE3NTgwNjk0NjZ9.6WKhPbHCIoisTnIrDQsjg8z2BJFjmBT_7zSTYnz0V0DBLNDzSVvFO-szLKHAIJDo','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTgzMDY2LCJleHAiOjE3NjA1NzUwNjZ9._YB7vdpFJfH3SM89GGTskHkkP5qqkS_iyZxESQCHpic6rnuJkSk6Nt9bKnYOqEO0','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 08:37:47','2025-09-16 08:37:47'),
(65,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg1MDIzLCJleHAiOjE3NTgwNzE0MjN9.Fph26TcEGQCvFPOPtBz_Py2_qfHu3pSMX6H-T1QLoG0n1y7M4gEuvQ5fzUjXoQiK','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg1MDIzLCJleHAiOjE3NjA1NzcwMjN9.MexhY9j16PQ6B6aX029-yfpoGYXqsqPTg6D8OoyPWQiQEiE2lYMUH0_b32DvInpS','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:10:24','2025-09-16 09:10:24'),
(66,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MDUxLCJleHAiOjE3NTgwNzI0NTF9.9YyfpztXbsjicx0wz6iYy_-hkGMbceG8Kscytq5hmPK5ApXMNRMTviOYI22KECIt','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MDUxLCJleHAiOjE3NjA1NzgwNTF9.0zK6T-a5N2kG49aoWIP0bVSHQnVBMuZjYiDFPweR0hk3GILTwGtMaz2u5FBQC9Ky','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:27:32','2025-09-16 09:27:32'),
(67,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5ODYwNjMsImV4cCI6MTc1ODA3MjQ2M30.3MKSXSdLECP_Vu5mVLeYXObdjP-fSjnIFCZvtabs3b_xW8xnr6bTsDMN_lHtSqL4','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTc5ODYwNjMsImV4cCI6MTc2MDU3ODA2M30.vEPYpW7phZKLQFbpIpWhAjZZumsix6fHXYVBI1JluYxcu4-1spPNycdRrxfQP49D','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:27:44','2025-09-16 09:27:44'),
(68,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MjQ1LCJleHAiOjE3NTgwNzI2NDV9.Uq0776kkEOMIzWY3GApE7GjU1FB1hKk5RNbqP94T3fDyb0tPEHI3YW__pgIWZrIl','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2MjQ1LCJleHAiOjE3NjA1NzgyNDV9.G2iw6S-paGSrg6V7CZrwq1r2hzK9rYZ3TFsSgiu8UxOI2EfThssvXSsJNVOU-0hU','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:30:46','2025-09-16 09:30:46'),
(69,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2NDUzLCJleHAiOjE3NTgwNzI4NTN9.pVvv1v0vusjgW51XULS17Ww2eLqyPFEn846o2C4Pj3r9bmWnlYQ2LNGAl9l7pbMi','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg2NDUzLCJleHAiOjE3NjA1Nzg0NTN9.qSLPIX2pleMMtiE5svS6p5MPOkKmC-dqHrUTD9GqZhTX14hKpfBKS31sI7EZyGHH','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 09:34:13','2025-09-16 09:34:13'),
(70,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4NTA3LCJleHAiOjE3NTgwNzQ5MDd9.rHBLxf2hEe4T5CbFdVlvWEAFWbwJ9QFSZqcC30fcqR8kCwHfXnkUaDNU7SUoSgvY','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4NTA3LCJleHAiOjE3NjA1ODA1MDd9.VMIpjYmdo7SqsS09C_PteZEUV9w1c7Z6CsfBaNBIWwjOBIG6DtOXMFheHzH4CQjD','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:08:28','2025-09-16 10:08:28'),
(71,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODcwLCJleHAiOjE3NTgwNzUyNzB9.yYw12Jr9tNLTqtm5S9Upomk4XiXTQDHSGXfj3NggYiW1ye3XTb8NgSjhMRRiW-8f','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODcwLCJleHAiOjE3NjA1ODA4NzB9.RAT6g6cvy1fQcZ9glSNPwe-xl4M5qt13EY5Xxu7bCZQqKKE6oOlq8NbXOStXSn1Z','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:14:30','2025-09-16 10:14:30'),
(72,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODk2LCJleHAiOjE3NTgwNzUyOTZ9.jrOeMh-hmy0ILWvnkvg5UDaH1ur4NPVWIIheyLhtiEkfsv0uk-rDIa3l8jJXiXzU','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU3OTg4ODk2LCJleHAiOjE3NjA1ODA4OTZ9.k-H7Kwy3_gv81KWWJrcZQI1I8vOJagBAzlEnLFsHWnGChQQC36VKXJ_6HTLy5mJv','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-17 10:14:56','2025-09-16 10:14:56'),
(73,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyMzc3LCJleHAiOjE3NTgxNTg3Nzd9.G5PwjjHLGYKElQL6xwpenaOjIErxTLO_83Ol0f4xv6U8vCMcGnIOdcPdecUyqQqD','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyMzc3LCJleHAiOjE3NjA2NjQzNzd9.9FWIVfIXtriiEhCGsRZWDkBZcLzyFwFqfdoKa8hD7tKGUOpWtJXX6oijKqprvkux','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-18 09:26:17','2025-09-17 09:26:17'),
(74,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyNDgyLCJleHAiOjE3NTgxNTg4ODJ9.W_aCyI_F8vLzkTsFm39Lig84wOZH-V4xh2OsbZkf9Zfg_hW3fwkwTIfcJVOg2H01','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4MDcyNDgyLCJleHAiOjE3NjA2NjQ0ODJ9.DK6IF4vAX-LHO0B36Y0CWXa5S9F-BXtm5rMgQywk3EeM-B-DCdQvFVnqtrE0tWuX','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-18 09:28:03','2025-09-17 09:28:03'),
(75,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTAxODQzLCJleHAiOjE3NTg1ODgyNDN9.VQVtOXcE10Qs0a8l9ghs7dnA8iaXLYvz-vza5i8IcapaA69-FjWe__SPK1DH8L-o','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTAxODQzLCJleHAiOjE3NjEwOTM4NDN9.5IInyyrLN-WaNlHKwrdjUB7Pw3dR1F7oAoOqU9i-ZWsIpb8D3B3HEqKQGt70DSqL','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-22 08:44:04','2025-09-22 08:44:04'),
(76,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTg4OTAyLCJleHAiOjE3NTg2NzUzMDJ9.x9a-VNuyYXVz4A65mbVn7CwGNmVcu8alTRtRm26EW_LqehnuMM3oWUBelbTzIV61','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU4NTg4OTAyLCJleHAiOjE3NjExODA5MDJ9.St_BhB1o_zODZ-kkxXlbGGc8SgpqVTygZy-tQ4r5wcySjBMj5BR51S6eGXAh9q4o','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-24 08:55:02','2025-09-23 08:55:02'),
(77,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyMzg1LCJleHAiOjE3NTkxNDg3ODV9.Xtht8hDdmgWHXy5DRJp7qc3ub9c8ZZ4KR4bKQ1hOmMMMbym4qdIHwIjNGWL8yXuM','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyMzg1LCJleHAiOjE3NjE2NTQzODV9.qtJ-qEYWr902RWbhcCg4YoUIIIlRgyI6yyjg7TtZ87nPfF8zPSZBoYesu51naMwn','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:26:26','2025-09-28 20:26:26'),
(78,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkwNjI1MDYsImV4cCI6MTc1OTE0ODkwNn0.zwcZtIH874Bhw7HDYMX2myTidQmeN4M4HCfX0-eGylXFC2ZzrLilRhE2wrd0RXrG','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkwNjI1MDYsImV4cCI6MTc2MTY1NDUwNn0.vVSTBv_IrHR0yLW7bAlUiK4uNnos-6tElDWWsaDWYFBKLAzqN4-RfDHeHTb2yWw8','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:28:27','2025-09-28 20:28:27'),
(79,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyNTkxLCJleHAiOjE3NTkxNDg5OTF9.WjJbQeXW1UyvyqQKsB2Avn2XcG68YuGYe-e0LdPyxd1NDw-n9YxZJ3zYOry8NmDW','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYyNTkxLCJleHAiOjE3NjE2NTQ1OTF9.Eo0Iubx5PyyYL9NIQp7df8etzBya6yFgln43d00hG2ugnkLPT67BaGzvlCZSkGBG','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:29:52','2025-09-28 20:29:52'),
(81,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYzMDIyLCJleHAiOjE3NTkxNDk0MjJ9.9RArdGZFS6Oq2iCiPcF_bP36DnQyhV6GhkmpaJD7NknChqzn-nTJzpj72bX7hOt7','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MDYzMDIyLCJleHAiOjE3NjE2NTUwMjJ9.05_vUCQT3lqLnu5ItgYVtWE7iG3HipQ0DhEGMpLuJp7ZxxCMbQX9yHNTdK3iwpUA','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-09-29 20:37:02','2025-09-28 20:37:02'),
(82,8,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NTkxOTI5MTEsImV4cCI6MTc1OTI3OTMxMX0.9nzMXQ4ggjQXIoSWWk_V1YjTolN70m9VZLyEoVoLAJ38vc09mogteutiOlyDyjcy','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjgsInN1YiI6InVzZXIxMjMiLCJpYXQiOjE3NTkxOTI5MTEsImV4cCI6MTc2MTc4NDkxMX0.9IQAvmxtNTa1fN7bWg95OjMlRDp7_MhXUOd-_zIQCwEpcKugJa9folBq4o0eRVkW','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:41:51','2025-09-30 08:41:51'),
(83,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTkzMzE1LCJleHAiOjE3NTkyNzk3MTV9.v-8lNMFR3SnM2Vb0JQfHkMq8Nuo03xKa_0DuR7o4kj2jyytyJKBOPE09qQL96uiG','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTkzMzE1LCJleHAiOjE3NjE3ODUzMTV9.itNSsyeaDfUYWQ6Ku7UKtXroWdhxzx4GIULF66pw1FaviAMEh3QbxDpCzScZffe_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:48:36','2025-09-30 08:48:36'),
(84,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM0ODIsImV4cCI6MTc1OTI3OTg4Mn0.0-uIwSBMbzKjr6J9JChqPOooGWLB10q5Qlly0S0VR1Tf3rePntYLDUgje6kK69gq','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM0ODIsImV4cCI6MTc2MTc4NTQ4Mn0.1Mw6ZztS6t-mRBS8p1JTdKPQ_LHmS5BajnHlMKwaUuzT7uoxvtOHUNZzlh0auRJd','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:51:23','2025-09-30 08:51:23'),
(85,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM2MjYsImV4cCI6MTc1OTI4MDAyNn0.uqnWe5wndCRSYqeQ-yI36nz-jgf2_QSJfMNI1meWVm_l_U9owtmaGCJQXqfr1uAm','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM2MjYsImV4cCI6MTc2MTc4NTYyNn0.LdNtgLXAHbU_sQJuGIlbNxZkZakknf0rQTyupuud7y4CTitCgh3Z9mkHb3SCCEx0','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:53:47','2025-09-30 08:53:47'),
(86,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM3OTQsImV4cCI6MTc1OTI4MDE5NH0.hTBrC9OUWslZ_4KdZevqUJu2E7v20TQZO24NfApGABbMDdShm2u4_Zir7ly4sjKe','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTM3OTQsImV4cCI6MTc2MTc4NTc5NH0.escAkxV5Auwydjb1ob7OvspGhgZiBRec-7_phkitTIfJY1g4PYifZtl9v2JQyxsu','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 08:56:35','2025-09-30 08:56:35'),
(87,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0MTIxLCJleHAiOjE3NTkyODA1MjF9.ivnWnptVqe5EHuYS8_8_WwRDm1-2-W64HLb3PB_tKzmhFxGgfJ76GRNpQKqzip_G','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0MTIxLCJleHAiOjE3NjE3ODYxMjF9.nh7iA7lce2O20DEPHxeojtKfZjt0RWm6vOLbUyIEUQn8P5tOWFSo6jOxYFEisFjY','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:02:02','2025-09-30 09:02:02'),
(88,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQxOTAsImV4cCI6MTc1OTI4MDU5MH0.5x4IuNibLM0bbudBIQumdb0T1vYzZiFNqjkdDM3pR_5pOxXz0uu9WB9eDvdkFNF3','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQxOTAsImV4cCI6MTc2MTc4NjE5MH0.4Ffbv3BxjvnhUPDY3RTuP4CwY0No-CP9vpiLXiLp636dMNsRYyEbzaenk8hN2SOe','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:03:10','2025-09-30 09:03:10'),
(89,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0NTMzLCJleHAiOjE3NTkyODA5MzN9.whqVKlx_b4CCpGCn8UrNF0oNq44l6246GjSjIG5WsxV18okRtNxYkNfQ_XT__wRX','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0NTMzLCJleHAiOjE3NjE3ODY1MzN9.q2B17V4JxvZHZ5RWCjZXPY4C2sfVmuZrnP62lCFzfo6KZVtCVdEDqJQqgSkhCpEa','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:08:54','2025-09-30 09:08:54'),
(90,2,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQ2NjYsImV4cCI6MTc1OTI4MTA2Nn0.XFbe2pbFQErd5wom2vCQGep4j24XBqCsb0HDFJh2Gio4_Fc0ObzzJphB7CpZmh-l','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjIsInN1YiI6InVzZXIiLCJpYXQiOjE3NTkxOTQ2NjYsImV4cCI6MTc2MTc4NjY2Nn0.6721G2ZHZ1hzSP7DZoqMCPTcLQr7i4CI-m-2pQjriaV7wqBY3yXAuD-YZHonvEV_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',0,'2025-10-01 09:11:06','2025-09-30 09:11:06'),
(91,1,'eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0ODIzLCJleHAiOjE3NTkyODEyMjN9.hu2Z5n2QdbVSg2ugRynBII8WKhRFT4WjYNOpj3to8d3dwNNL6qEnStiFo_p5GgQo','eyJhbGciOiJIUzM4NCJ9.eyJ1c2VySWQiOjEsInN1YiI6ImFkbWluIiwiaWF0IjoxNzU5MTk0ODIzLCJleHAiOjE3NjE3ODY4MjN9.HfK-wfaRiEv6B-lfalZzhoOivKl9jPwOfoztoDQ8uSh_2uxioYeV4jAXvZiWZ6F_','127.0.0.1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36 Edg/140.0.0.0',1,'2025-10-01 09:13:44','2025-09-30 09:13:44');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '邮箱',
  `salt` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码盐值',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否激活',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

/*Data for the table `users` */

insert  into `users`(`id`,`username`,`password`,`email`,`salt`,`is_active`,`last_login_time`,`create_time`,`update_time`,`phone`) values 
(1,'admin','Wib17jaGy+f+dqRRLlU31LtkyMXXhRH5P7mBqVJSWZA=','3310530272@qq.com','VENfs5/WIp94Y3epBDQPz3lIHiL+ldNUQq4hn1TXgAk=',1,'2025-09-30 09:13:44','2025-09-04 08:51:54','2025-09-30 09:13:44',NULL),
(2,'user','kOVDpxTPMCxh154PMx2L/llhfHP2GouehIqgfxRUNLE=','3310530279@qq.com','CAyH7i3GuDBGp5dTu/6pa8x2XQG7oNx7u0Hj2SxlNeU=',1,'2025-09-30 09:11:06','2025-09-04 10:50:56','2025-09-30 09:11:06','17531307050'),
(6,'admin1','Ww7ctdhCJwViZLID1HFCuN5dQ7/qr3j8WEphSRgZHwo=','3310530270@qq.com','REjynbaukziVnmQCnocPNgCBRt5cUC9TTvAYs6a52vw=',1,'2025-09-08 10:58:06','2025-09-08 10:57:08','2025-09-28 20:34:48','17531307057'),
(8,'user123','VEcZdfPU8OkbKyUYHXZI+7wGVwiiq5zL1H8tFtu9zzE=','3310530275@qq.com','lM5NWTBXSBnqD85Z132j82IKiH9y1dhTCCjzaGZMqtc=',1,'2025-09-30 08:41:51','2025-09-30 08:41:40','2025-09-30 08:41:51','17531307055');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
