/*
 Navicat Premium Dump SQL

 Source Server         : 宝塔数据库
 Source Server Type    : MySQL
 Source Server Version : 50744 (5.7.44-log)
 Source Host           : 47.104.250.164:3306
 Source Schema         : gra

 Target Server Type    : MySQL
 Target Server Version : 50744 (5.7.44-log)
 File Encoding         : 65001

 Date: 15/05/2026 15:07:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lab_equipment
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment`;
CREATE TABLE `lab_equipment`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（设备唯一标识）',
  `equipment_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备编号（唯一，如“EQ2024001”）',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备型号',
  `lab_id` bigint(20) NOT NULL COMMENT '所属实验室ID',
  `lab_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属实验室名称（冗余字段，用于前端展示）',
  `count` int(10) NOT NULL COMMENT '数量',
  `purchase_date` date NOT NULL COMMENT '采购日期',
  `specification` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备规格参数',
  `manual_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '说明书上传路径（PDF）',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '设备状态（0-在库，1-领用中，2-故障待修，3-已报废）',
  `status_text` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备状态文本（0-在库，1-领用中，2-故障待修，3-已报废）',
  `residual_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '残值（报废时填写）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_equipment_code`(`equipment_code`) USING BTREE COMMENT '设备编号唯一约束',
  INDEX `idx_lab_id`(`lab_id`) USING BTREE COMMENT '实验室ID索引（按实验室查询设备）',
  INDEX `idx_equipment_status`(`status`) USING BTREE COMMENT '设备状态索引（筛选设备用）',
  INDEX `idx_equipment_name`(`equipment_name`) USING BTREE COMMENT '设备名称索引（模糊搜索用）',
  CONSTRAINT `fk_equipment_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_laboratory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备信息表（核心业务表）：实验室删除受限（需先转移/删除设备）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_equipment
-- ----------------------------
INSERT INTO `lab_equipment` VALUES (1, 'EQ-20260311', '笔记本电脑', '天选3', 3, '计算机实验室303', 1, '2026-03-11', '12th Gen Intel(R) Core(TM) i7-12700H   2.30 GHz', '', 0, '在库', NULL, '2026-03-11 17:58:26', '2026-04-25 15:00:27');
INSERT INTO `lab_equipment` VALUES (2, 'EQ-20230327', '电子分析天平', 'FA2004', 1, '物理实验室101', 8, '2026-03-27', '量程:0~200g', NULL, 0, '在库', NULL, '2026-03-27 08:30:57', '2026-04-23 17:51:51');
INSERT INTO `lab_equipment` VALUES (3, 'EQ-2026001', '超声波清洗机', 'PS-40A', 2, '化学实验室202', 10, '2026-03-27', '功率180W', NULL, 0, '在库', NULL, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment` VALUES (4, 'EQ-2026002', '台式高速离心机', 'TG16-WS', 2, '化学实验室202', 15, '2026-03-27', '最高转速16000r/min', NULL, 0, '在库', NULL, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment` VALUES (5, 'EQ-2026003', '恒温水浴锅', 'HH-S4', 2, '化学实验室202', 20, '2026-03-27', '控温范围:0°C~100°C', NULL, 0, '在库', NULL, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment` VALUES (6, 'EQ-2026004', '气垫导轨', 'L-QGD-1', 1, '物理实验室101', 15, '2026-03-27', '1.2m，配光电门', NULL, 0, '在库', NULL, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment` VALUES (7, 'EQ-2026005', '单摆仪', 'J-LD-1', 1, '物理实验室101', 15, '2026-03-27', '摆长 0.5-1m，周期 0.1s', NULL, 0, '在库', NULL, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment` VALUES (8, 'EQ-2026006', '嵌入式开发板', 'STM32F103', 3, '计算机实验室303', 20, '2026-03-27', 'Cortex-M3，72MHz', NULL, 0, '在库', NULL, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment` VALUES (9, 'EQ-2026007', '路由器', 'Cisco ISR 4321', 3, '计算机实验室303', 10, '2026-03-27', '2×GE，支持 VPN', NULL, 0, '在库', NULL, '2026-03-27 11:23:27', '2026-03-27 11:23:27');

-- ----------------------------
-- Table structure for lab_equipment_apply
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_apply`;
CREATE TABLE `lab_equipment_apply`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（申请记录唯一标识）',
  `equipment_id` bigint(20) NOT NULL COMMENT '关联设备ID',
  `asset_id` bigint(20) NULL DEFAULT NULL COMMENT '关联设备实例ID（精确到哪一台）',
  `reserve_id` bigint(20) NULL DEFAULT NULL COMMENT '关联的预约记录ID（无预约则为NULL）',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称（冗余字段，用于前端展示）',
  `user_id` bigint(20) NOT NULL COMMENT '申请人ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请人姓名（冗余字段，用于前端展示）',
  `apply_type` tinyint(4) NOT NULL COMMENT '申请类型（0-领用，1-归还）',
  `apply_quantity` int(10) NOT NULL DEFAULT 1 COMMENT '申请数量（领用/归还的设备台数）',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请提交时间',
  `use_time` datetime NOT NULL COMMENT '领用开始时间（领用申请必填）',
  `return_time` datetime NULL DEFAULT NULL COMMENT '实际归还时间（归还后填写）',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '领用用途',
  `equipment_status` tinyint(4) NULL DEFAULT NULL COMMENT '归还时设备状态（0-正常，1-故障）',
  `approval_user_id` bigint(20) NULL DEFAULT NULL COMMENT '审批人ID（管理员）',
  `approval_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人姓名（冗余字段，用于前端展示）',
  `approval_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '审批状态（0-待审批，1-通过，2-驳回）',
  `status_text` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批状态文本（0-待审批，1-通过，2-驳回）',
  `approval_time` datetime NULL DEFAULT NULL COMMENT '审批时间',
  `remarks` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注（如故障描述）',
  `return_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '归还状态（0-未申请归还 1-待归还审批 2-已归还）',
  `return_apply_time` datetime NULL DEFAULT NULL COMMENT '学生申请归还时间',
  `status` tinyint(4) NULL DEFAULT NULL COMMENT '记录状态（4-软删除/已隐藏）',
  `original_status` tinyint(4) NULL DEFAULT NULL COMMENT '学生点击删除前记录的状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id`) USING BTREE COMMENT '设备ID索引（查询设备领用记录）',
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '申请人ID索引（查询用户领用记录）',
  INDEX `idx_approval_status`(`approval_status`) USING BTREE COMMENT '审批状态索引（筛选待审批记录）',
  INDEX `idx_apply_time`(`apply_time`) USING BTREE COMMENT '申请时间索引（按时间统计）',
  INDEX `fk_apply_approval_user`(`approval_user_id`) USING BTREE,
  INDEX `fk_apply_reserve`(`reserve_id`) USING BTREE,
  INDEX `idx_apply_asset_id`(`asset_id`) USING BTREE,
  INDEX `idx_equipment_quantity`(`equipment_id`, `apply_quantity`, `approval_status`) USING BTREE,
  CONSTRAINT `fk_apply_approval_user` FOREIGN KEY (`approval_user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_apply_asset` FOREIGN KEY (`asset_id`) REFERENCES `lab_equipment_asset` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_apply_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `lab_equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_apply_reserve` FOREIGN KEY (`reserve_id`) REFERENCES `lab_equipment_reserve` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_apply_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备领用归还表：设备删除受限（需先完结领用记录）；用户删除时，关联记录同步删除；审批人删除时，审批人ID设为NULL' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_equipment_apply
-- ----------------------------
INSERT INTO `lab_equipment_apply` VALUES (16, 1, 19, 2, '电子天平', 3, 'student01', 0, 1, '2026-03-09 02:10:10', '2026-03-09 02:10:10', '2026-03-09 09:31:52', '', 1, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-09 09:31:36', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (18, 8, 37, 3, '测试', 3, 'student01', 0, 1, '2026-03-09 02:22:24', '2026-03-09 02:22:24', '2026-03-09 09:31:49', '', 0, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-09 09:31:40', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (20, 6, 6, 4, '工业平板电脑', 3, 'student01', 0, 1, '2026-03-09 09:37:57', '2026-03-09 09:37:57', '2026-03-09 09:38:36', '测试预约使用', 1, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-09 09:38:11', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (21, 9, 42, 5, '测试设备', 3, 'student01', 0, 1, '2026-03-09 11:16:09', '2026-03-09 11:16:09', '2026-03-09 17:17:12', '测试用途', 1, NULL, '张老师', 1, NULL, NULL, '测试驳回归还', 2, '2026-03-09 17:17:01', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (28, 6, NULL, 10, '工业平板电脑', 3, 'student01', 0, 4, '2026-03-11 09:22:53', '2026-03-11 09:22:53', '2026-03-11 09:23:13', '测试4台电脑', 0, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-11 09:23:05', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (29, 1, NULL, 9, '电子天平', 3, 'student01', 0, 2, '2026-03-11 14:33:01', '2026-03-11 14:33:01', '2026-03-11 16:40:29', '测试电子天平2/5', 1, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-11 16:15:40', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (30, 8, NULL, 15, '测试', 3, 'student01', 0, 4, '2026-03-11 17:19:42', '2026-03-11 17:19:42', '2026-03-11 17:22:47', '测试报废显示555', 1, NULL, '系统管理员', 1, NULL, NULL, '', 2, '2026-03-11 17:22:29', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (31, 6, 12, 16, '工业平板电脑', 3, 'student01', 0, 1, '2026-03-11 17:53:56', '2026-03-11 17:53:56', '2026-03-11 17:54:26', '测试zdzdzd', 0, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-11 17:54:14', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (32, 1, 24, 17, '测试设备1', 3, 'student01', 0, 1, '2026-03-12 15:38:34', '2026-03-12 15:38:34', '2026-03-27 10:38:39', '测试设备的预约用途', 0, NULL, '系统管理员', 1, NULL, NULL, NULL, 2, '2026-03-27 10:37:00', 4, 2);
INSERT INTO `lab_equipment_apply` VALUES (33, 1, 24, 21, '计算机', 3, 'student01', 0, 1, '2026-03-27 15:22:03', '2026-03-27 15:22:03', '2026-03-27 16:01:10', '上课使用', 1, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-03-27 15:24:19', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (34, 1, 47, 22, '笔记本电脑', 3, 'student01', 0, 1, '2026-03-27 18:04:42', '2026-03-27 18:04:42', '2026-03-29 17:45:29', '上课使用', 0, NULL, '系统管理员', 1, NULL, NULL, NULL, 2, '2026-03-29 17:45:08', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (35, 1, 47, 24, '笔记本电脑', 3, 'student01', 0, 1, '2026-03-29 17:42:36', '2026-03-29 17:42:36', '2026-03-29 17:45:30', '课程使用', 0, NULL, '系统管理员', 1, NULL, NULL, NULL, 2, '2026-03-29 17:45:10', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (36, 2, 57, 23, '电子分析天平', 3, 'student01', 0, 1, '2026-03-29 17:45:01', '2026-03-29 17:45:01', '2026-03-29 17:45:32', '测量重量', 0, NULL, '系统管理员', 1, NULL, NULL, NULL, 2, '2026-03-29 17:45:11', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (37, 1, 48, 25, '笔记本电脑', 3, 'student01', 0, 1, '2026-03-29 17:50:24', '2026-03-29 17:50:24', NULL, '上课学习', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, '2026-04-14 18:52:59', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (38, 1, 49, 26, '笔记本电脑', 3, 'student01', 0, 1, '2026-04-13 19:01:35', '2026-04-13 19:01:35', NULL, '上课使用', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, '2026-04-23 15:49:13', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (39, 1, 49, 27, '笔记本电脑', 3, 'student01', 0, 1, '2026-04-23 16:53:52', '2026-04-23 16:53:52', NULL, '上课使用', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, '2026-04-23 17:20:25', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (40, 2, 57, 29, '电子分析天平', 3, 'student01', 0, 1, '2026-04-23 17:51:56', '2026-04-23 17:51:56', NULL, '', NULL, NULL, NULL, 1, NULL, NULL, NULL, 1, '2026-04-23 17:51:58', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (41, 1, NULL, 30, '笔记本电脑', 3, 'student01', 0, 2, '2026-04-25 14:59:15', '2026-04-25 14:59:15', '2026-04-25 14:59:57', '上课使用', 0, NULL, '张老师', 1, NULL, NULL, NULL, 2, '2026-04-25 14:59:30', NULL, NULL);
INSERT INTO `lab_equipment_apply` VALUES (42, 1, 50, 31, '笔记本电脑', 3, 'student01', 0, 1, '2026-04-25 15:00:31', '2026-04-25 15:00:31', NULL, '', NULL, NULL, NULL, 1, NULL, NULL, NULL, 0, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for lab_equipment_asset
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_asset`;
CREATE TABLE `lab_equipment_asset`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（设备实例唯一标识）',
  `equipment_id` bigint(20) NOT NULL COMMENT '所属设备类型ID（lab_equipment.id）',
  `asset_code` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实例编号（如 EQ-P2024001-01），唯一',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '实例状态（0-在库，1-领用中，2-维修中，3-已报废）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_asset_code`(`asset_code`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id`) USING BTREE,
  INDEX `idx_asset_status`(`status`) USING BTREE,
  CONSTRAINT `fk_asset_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `lab_equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 172 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备实例表：每台实物一条' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lab_equipment_asset
-- ----------------------------
INSERT INTO `lab_equipment_asset` VALUES (6, 6, 'EQ-PC2024002-01', 2, '2026-03-08 18:13:24', '2026-03-09 09:38:37');
INSERT INTO `lab_equipment_asset` VALUES (7, 1, 'EQ-P2024001-02', 1, '2026-03-08 18:13:24', '2026-03-09 02:04:13');
INSERT INTO `lab_equipment_asset` VALUES (12, 6, 'EQ-PC2024002-02', 0, '2026-03-08 18:13:24', '2026-03-11 17:54:24');
INSERT INTO `lab_equipment_asset` VALUES (13, 1, 'EQ-P2024001-03', 1, '2026-03-08 18:13:24', '2026-03-09 02:04:49');
INSERT INTO `lab_equipment_asset` VALUES (18, 6, 'EQ-PC2024002-03', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (19, 1, 'EQ-P2024001-04', 2, '2026-03-08 18:13:24', '2026-03-09 09:31:53');
INSERT INTO `lab_equipment_asset` VALUES (23, 6, 'EQ-PC2024002-04', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (24, 1, 'EQ-P2024001-05', 2, '2026-03-08 18:13:24', '2026-03-27 16:01:10');
INSERT INTO `lab_equipment_asset` VALUES (26, 6, 'EQ-PC2024002-05', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (28, 6, 'EQ-PC2024002-06', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (29, 6, 'EQ-PC2024002-07', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (30, 6, 'EQ-PC2024002-08', 0, '2026-03-08 18:13:24', '2026-03-08 18:13:24');
INSERT INTO `lab_equipment_asset` VALUES (37, 8, 'EQ-2026-03-01', 0, '2026-03-09 02:15:57', '2026-03-11 00:37:08');
INSERT INTO `lab_equipment_asset` VALUES (38, 8, 'EQ-2026-03-02', 0, '2026-03-09 02:15:57', '2026-03-09 02:15:57');
INSERT INTO `lab_equipment_asset` VALUES (39, 8, 'EQ-2026-03-03', 0, '2026-03-09 02:15:57', '2026-03-09 02:15:57');
INSERT INTO `lab_equipment_asset` VALUES (40, 8, 'EQ-2026-03-04', 0, '2026-03-09 02:15:57', '2026-03-09 02:15:57');
INSERT INTO `lab_equipment_asset` VALUES (41, 8, 'EQ-2026-03-05', 0, '2026-03-09 02:15:57', '2026-03-09 02:15:57');
INSERT INTO `lab_equipment_asset` VALUES (42, 9, 'EQ-20260304-01', 2, '2026-03-09 11:03:39', '2026-03-09 17:17:13');
INSERT INTO `lab_equipment_asset` VALUES (43, 9, 'EQ-20260304-02', 2, '2026-03-09 11:03:39', '2026-03-11 17:15:18');
INSERT INTO `lab_equipment_asset` VALUES (44, 9, 'EQ-20260304-03', 0, '2026-03-09 11:03:39', '2026-03-09 11:03:39');
INSERT INTO `lab_equipment_asset` VALUES (45, 9, 'EQ-20260304-04', 0, '2026-03-09 11:03:39', '2026-03-09 11:03:39');
INSERT INTO `lab_equipment_asset` VALUES (46, 9, 'EQ-20260304-05', 0, '2026-03-09 11:03:39', '2026-03-09 11:03:39');
INSERT INTO `lab_equipment_asset` VALUES (47, 1, 'EQ-20260311-01', 3, '2026-03-11 17:58:26', '2026-03-29 17:46:58');
INSERT INTO `lab_equipment_asset` VALUES (48, 1, 'EQ-20260311-02', 2, '2026-03-11 17:58:26', '2026-03-29 17:50:31');
INSERT INTO `lab_equipment_asset` VALUES (49, 1, 'EQ-20260311-03', 2, '2026-03-11 17:58:26', '2026-04-23 16:54:00');
INSERT INTO `lab_equipment_asset` VALUES (50, 1, 'EQ-20260311-04', 2, '2026-03-11 17:58:26', '2026-04-25 15:00:41');
INSERT INTO `lab_equipment_asset` VALUES (51, 1, 'EQ-20260311-05', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (52, 1, 'EQ-20260311-06', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (53, 1, 'EQ-20260311-07', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (54, 1, 'EQ-20260311-08', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (55, 1, 'EQ-20260311-09', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (56, 1, 'EQ-20260311-10', 0, '2026-03-11 17:58:26', '2026-03-11 17:58:26');
INSERT INTO `lab_equipment_asset` VALUES (57, 2, 'EQ-20230327-01', 1, '2026-03-27 08:30:57', '2026-04-23 17:51:56');
INSERT INTO `lab_equipment_asset` VALUES (58, 2, 'EQ-20230327-02', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (59, 2, 'EQ-20230327-03', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (60, 2, 'EQ-20230327-04', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (61, 2, 'EQ-20230327-05', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (62, 2, 'EQ-20230327-06', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (63, 2, 'EQ-20230327-07', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (64, 2, 'EQ-20230327-08', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (65, 2, 'EQ-20230327-09', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (66, 2, 'EQ-20230327-10', 0, '2026-03-27 08:30:57', '2026-03-27 08:30:57');
INSERT INTO `lab_equipment_asset` VALUES (67, 3, 'EQ-2026001-01', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (68, 3, 'EQ-2026001-02', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (69, 3, 'EQ-2026001-03', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (70, 3, 'EQ-2026001-04', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (71, 3, 'EQ-2026001-05', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (72, 3, 'EQ-2026001-06', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (73, 3, 'EQ-2026001-07', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (74, 3, 'EQ-2026001-08', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (75, 3, 'EQ-2026001-09', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (76, 3, 'EQ-2026001-10', 0, '2026-03-27 11:08:31', '2026-03-27 11:08:31');
INSERT INTO `lab_equipment_asset` VALUES (77, 4, 'EQ-2026002-01', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (78, 4, 'EQ-2026002-02', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (79, 4, 'EQ-2026002-03', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (80, 4, 'EQ-2026002-04', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (81, 4, 'EQ-2026002-05', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (82, 4, 'EQ-2026002-06', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (83, 4, 'EQ-2026002-07', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (84, 4, 'EQ-2026002-08', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (85, 4, 'EQ-2026002-09', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (86, 4, 'EQ-2026002-10', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (87, 4, 'EQ-2026002-11', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (88, 4, 'EQ-2026002-12', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (89, 4, 'EQ-2026002-13', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (90, 4, 'EQ-2026002-14', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (91, 4, 'EQ-2026002-15', 0, '2026-03-27 11:10:14', '2026-03-27 11:10:14');
INSERT INTO `lab_equipment_asset` VALUES (92, 5, 'EQ-2026003-01', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (93, 5, 'EQ-2026003-02', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (94, 5, 'EQ-2026003-03', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (95, 5, 'EQ-2026003-04', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (96, 5, 'EQ-2026003-05', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (97, 5, 'EQ-2026003-06', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (98, 5, 'EQ-2026003-07', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (99, 5, 'EQ-2026003-08', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (100, 5, 'EQ-2026003-09', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (101, 5, 'EQ-2026003-10', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (102, 5, 'EQ-2026003-11', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (103, 5, 'EQ-2026003-12', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (104, 5, 'EQ-2026003-13', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (105, 5, 'EQ-2026003-14', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (106, 5, 'EQ-2026003-15', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (107, 5, 'EQ-2026003-16', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (108, 5, 'EQ-2026003-17', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (109, 5, 'EQ-2026003-18', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (110, 5, 'EQ-2026003-19', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (111, 5, 'EQ-2026003-20', 0, '2026-03-27 11:13:00', '2026-03-27 11:13:00');
INSERT INTO `lab_equipment_asset` VALUES (112, 6, 'EQ-2026004-01', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (113, 6, 'EQ-2026004-02', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (114, 6, 'EQ-2026004-03', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (115, 6, 'EQ-2026004-04', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (116, 6, 'EQ-2026004-05', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (117, 6, 'EQ-2026004-06', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (118, 6, 'EQ-2026004-07', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (119, 6, 'EQ-2026004-08', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (120, 6, 'EQ-2026004-09', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (121, 6, 'EQ-2026004-10', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (122, 6, 'EQ-2026004-11', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (123, 6, 'EQ-2026004-12', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (124, 6, 'EQ-2026004-13', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (125, 6, 'EQ-2026004-14', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (126, 6, 'EQ-2026004-15', 0, '2026-03-27 11:20:32', '2026-03-27 11:20:32');
INSERT INTO `lab_equipment_asset` VALUES (127, 7, 'EQ-2026005-01', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (128, 7, 'EQ-2026005-02', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (129, 7, 'EQ-2026005-03', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (130, 7, 'EQ-2026005-04', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (131, 7, 'EQ-2026005-05', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (132, 7, 'EQ-2026005-06', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (133, 7, 'EQ-2026005-07', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (134, 7, 'EQ-2026005-08', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (135, 7, 'EQ-2026005-09', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (136, 7, 'EQ-2026005-10', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (137, 7, 'EQ-2026005-11', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (138, 7, 'EQ-2026005-12', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (139, 7, 'EQ-2026005-13', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (140, 7, 'EQ-2026005-14', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (141, 7, 'EQ-2026005-15', 0, '2026-03-27 11:21:27', '2026-03-27 11:21:27');
INSERT INTO `lab_equipment_asset` VALUES (142, 8, 'EQ-2026006-01', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (143, 8, 'EQ-2026006-02', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (144, 8, 'EQ-2026006-03', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (145, 8, 'EQ-2026006-04', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (146, 8, 'EQ-2026006-05', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (147, 8, 'EQ-2026006-06', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (148, 8, 'EQ-2026006-07', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (149, 8, 'EQ-2026006-08', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (150, 8, 'EQ-2026006-09', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (151, 8, 'EQ-2026006-10', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (152, 8, 'EQ-2026006-11', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (153, 8, 'EQ-2026006-12', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (154, 8, 'EQ-2026006-13', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (155, 8, 'EQ-2026006-14', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (156, 8, 'EQ-2026006-15', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (157, 8, 'EQ-2026006-16', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (158, 8, 'EQ-2026006-17', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (159, 8, 'EQ-2026006-18', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (160, 8, 'EQ-2026006-19', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (161, 8, 'EQ-2026006-20', 0, '2026-03-27 11:22:40', '2026-03-27 11:22:40');
INSERT INTO `lab_equipment_asset` VALUES (162, 9, 'EQ-2026007-01', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (163, 9, 'EQ-2026007-02', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (164, 9, 'EQ-2026007-03', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (165, 9, 'EQ-2026007-04', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (166, 9, 'EQ-2026007-05', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (167, 9, 'EQ-2026007-06', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (168, 9, 'EQ-2026007-07', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (169, 9, 'EQ-2026007-08', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (170, 9, 'EQ-2026007-09', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');
INSERT INTO `lab_equipment_asset` VALUES (171, 9, 'EQ-2026007-10', 0, '2026-03-27 11:23:27', '2026-03-27 11:23:27');

-- ----------------------------
-- Table structure for lab_equipment_group
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_group`;
CREATE TABLE `lab_equipment_group`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '设备组ID（如“电子天平FA2004”）',
  `group_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备组编号（如EQ-P2024001）',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备名称',
  `model` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备型号',
  `lab_id` bigint(20) NOT NULL COMMENT '所属实验室ID',
  `lab_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属实验室名称（冗余）',
  `specification` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '规格参数',
  `manual_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '说明书路径',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_group_code`(`group_code`) USING BTREE,
  INDEX `idx_lab_id`(`lab_id`) USING BTREE,
  CONSTRAINT `fk_group_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_laboratory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '设备组表（同型号设备的共性信息）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lab_equipment_group
-- ----------------------------

-- ----------------------------
-- Table structure for lab_equipment_maintain
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_maintain`;
CREATE TABLE `lab_equipment_maintain`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（维护记录唯一标识）',
  `equipment_id` bigint(20) NOT NULL COMMENT '关联设备ID',
  `asset_id` bigint(20) NULL DEFAULT NULL COMMENT '关联设备实例ID（精确到哪一台）',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称（冗余字段，用于前端展示）',
  `maintain_type` tinyint(4) NOT NULL COMMENT '维护类型（0-日常维护，1-维修）',
  `apply_user_id` bigint(20) NOT NULL COMMENT '申请人ID（学生/教师）',
  `apply_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请人姓名（冗余字段，用于前端展示）',
  `assign_user_id` bigint(20) NULL DEFAULT NULL COMMENT '指派维修人员ID（管理员）',
  `assign_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '指派维修人员姓名（冗余字段，用于前端展示）',
  `maintain_content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '维护/维修内容',
  `maintain_time` datetime NULL DEFAULT NULL COMMENT '维护/维修执行时间',
  `cost` decimal(10, 2) NULL DEFAULT NULL COMMENT '维修成本（维修类型必填）',
  `progress_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '进度状态（0-待处理，1-进行中，2-已完成）',
  `remind_cycle` int(11) NULL DEFAULT NULL COMMENT '维护周期（天，日常维护必填）',
  `next_remind_time` datetime NULL DEFAULT NULL COMMENT '下次提醒时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `original_status` tinyint(4) NULL DEFAULT NULL COMMENT '维修员点击删除前记录的状态',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id`) USING BTREE COMMENT '设备ID索引（查询设备维护记录）',
  INDEX `idx_apply_user_id`(`apply_user_id`) USING BTREE COMMENT '申请人ID索引（查询用户提交的维护记录）',
  INDEX `idx_progress_status`(`progress_status`) USING BTREE COMMENT '进度状态索引（筛选待处理记录）',
  INDEX `idx_next_remind_time`(`next_remind_time`) USING BTREE COMMENT '下次提醒时间索引（定时任务查询）',
  INDEX `fk_maintain_assign_user`(`assign_user_id`) USING BTREE,
  INDEX `idx_maintain_asset_id`(`asset_id`) USING BTREE,
  CONSTRAINT `fk_maintain_apply_user` FOREIGN KEY (`apply_user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_maintain_asset` FOREIGN KEY (`asset_id`) REFERENCES `lab_equipment_asset` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_maintain_assign_user` FOREIGN KEY (`assign_user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_maintain_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `lab_equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备维护记录表：设备删除受限（需先完结维护记录）；申请人删除时，关联记录同步删除；维修人员删除时，指派人员ID设为NULL' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_equipment_maintain
-- ----------------------------
INSERT INTO `lab_equipment_maintain` VALUES (8, 1, 24, '计算机', 1, 3, 'student01', 4, 'repairer01', '维修成本大于实际价值', '2026-03-27 18:02:02', 0.00, 2, NULL, NULL, '2026-03-27 16:01:12', '2026-03-27 18:02:01', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (9, 1, 47, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '损坏严重,无法修复', '2026-03-27 18:05:15', 0.00, 2, NULL, NULL, '2026-03-27 18:04:49', '2026-03-27 18:05:15', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (10, 1, 47, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '无法修复', '2026-03-28 19:20:47', 0.00, 2, NULL, NULL, '2026-03-28 19:20:24', '2026-03-28 19:20:46', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (11, 1, 47, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '修复完成', '2026-03-29 17:20:10', 0.00, 2, NULL, NULL, '2026-03-28 19:21:51', '2026-03-29 17:20:09', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (12, 1, 47, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '修复完成', '2026-03-29 17:20:07', 0.00, 2, NULL, NULL, '2026-03-29 15:03:59', '2026-03-29 17:20:07', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (13, 1, 47, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '修复完成', '2026-03-29 17:44:01', 0.00, 2, NULL, NULL, '2026-03-29 17:42:45', '2026-03-29 17:44:00', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (14, 1, 48, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '修复成本大于实际价值', '2026-03-29 17:50:52', 10.00, 2, NULL, NULL, '2026-03-29 17:50:31', '2026-03-29 17:50:51', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (15, 1, 49, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '修复完成', '2026-04-23 16:34:59', 50.00, 2, NULL, NULL, '2026-04-13 19:02:03', '2026-04-23 16:34:59', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (16, 1, 49, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '坏了', NULL, NULL, 1, NULL, NULL, '2026-04-23 16:53:59', '2026-04-23 16:54:08', NULL);
INSERT INTO `lab_equipment_maintain` VALUES (17, 1, 50, '笔记本电脑', 1, 3, '李四', 4, 'repairer01', '无法修复', '2026-04-25 15:01:29', 50.00, 2, NULL, NULL, '2026-04-25 15:00:41', '2026-04-25 15:01:30', NULL);

-- ----------------------------
-- Table structure for lab_equipment_reserve
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_reserve`;
CREATE TABLE `lab_equipment_reserve`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（预约记录唯一标识）',
  `equipment_id` bigint(20) NOT NULL COMMENT '关联设备ID',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称（冗余字段，用于前端展示）',
  `user_id` bigint(20) NOT NULL COMMENT '预约人ID（学生）',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约人姓名（冗余字段，用于前端展示）',
  `reserve_quantity` int(10) NOT NULL DEFAULT 1 COMMENT '预约设备数量',
  `reserve_date` date NOT NULL COMMENT '预约日期',
  `start_time` time NULL DEFAULT NULL COMMENT '预约开始时段（如“09:00”）',
  `end_time` time NOT NULL COMMENT '预约结束时段（如“11:00”）',
  `purpose` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '领用用途',
  `status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '预约状态（0-待确认，1-已确认，2-已取消，3-已完成, 4-软删除, 5-删除）',
  `status_text` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '预约状态文本（0-待确认，1-已确认，2-已取消，3-已完成）',
  `is_used` tinyint(1) NULL DEFAULT 0 COMMENT '是否已领用（0-未领用，1-已领用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `original_status` tinyint(4) NULL DEFAULT NULL COMMENT '学生教师点击删除前记录的状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_reserve_conflict`(`equipment_id`, `reserve_date`, `start_time`, `end_time`, `reserve_quantity`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id`) USING BTREE COMMENT '设备ID索引（查询设备预约记录）',
  INDEX `idx_user_id`(`user_id`) USING BTREE COMMENT '预约人ID索引（查询用户预约记录）',
  INDEX `idx_reserve_date`(`reserve_date`) USING BTREE COMMENT '预约日期索引（按日期筛选预约）',
  INDEX `idx_status`(`status`) USING BTREE COMMENT '预约状态索引（筛选待确认预约）',
  CONSTRAINT `fk_reserve_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `lab_equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_reserve_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备预约表：设备删除受限（需先取消预约）；预约人删除时，关联记录同步删除' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_equipment_reserve
-- ----------------------------
INSERT INTO `lab_equipment_reserve` VALUES (2, 1, '电子天平', 3, '李四', 1, '2026-03-02', '01:24:57', '03:24:57', NULL, 4, NULL, 1, '2026-03-09 01:25:09', '2026-03-11 14:34:06', 2);
INSERT INTO `lab_equipment_reserve` VALUES (3, 8, '测试', 3, '李四', 1, '2026-03-09', '01:21:22', '03:21:22', NULL, 4, NULL, 1, '2026-03-09 02:21:36', '2026-03-11 14:36:15', 1);
INSERT INTO `lab_equipment_reserve` VALUES (4, 6, '工业平板电脑', 3, '李四', 1, '2026-03-02', '09:37:00', '10:37:00', '测试预约使用', 4, NULL, 1, '2026-03-09 09:37:15', '2026-03-11 14:41:38', 1);
INSERT INTO `lab_equipment_reserve` VALUES (5, 9, '测试设备', 3, '李四', 1, '2026-03-02', '10:03:49', '11:03:49', '测试用途', 1, NULL, 1, '2026-03-09 11:11:03', '2026-03-11 15:11:57', 1);
INSERT INTO `lab_equipment_reserve` VALUES (9, 1, '电子天平', 3, '李四', 2, '2026-03-10', '13:58:01', '16:58:01', '测试电子天平2/5', 1, NULL, 1, '2026-03-10 14:58:20', '2026-03-11 14:33:12', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (10, 6, '工业平板电脑', 3, '李四', 4, '2026-02-23', '14:21:22', '16:21:22', '测试4台电脑', 1, NULL, 1, '2026-03-10 15:21:58', '2026-03-11 09:22:58', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (15, 8, '测试', 3, '李四', 4, '2026-03-10', '16:18:48', '19:18:48', '测试报废显示555', 1, NULL, 1, '2026-03-11 17:19:12', '2026-03-11 17:19:40', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (16, 6, '工业平板电脑', 3, '李四', 1, '2026-03-02', '19:24:30', '22:24:30', '测试zdzdzd', 1, NULL, 1, '2026-03-11 17:24:45', '2026-03-11 17:54:03', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (17, 1, '测试设备1', 3, '李四', 1, '2026-03-12', '15:35:35', '19:35:35', '测试设备的预约用途', 1, NULL, 1, '2026-03-12 15:37:52', '2026-03-12 16:49:29', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (18, 1, '测试设备1', 3, '李四', 1, '2026-03-12', '15:39:49', '16:39:49', '测试2', 4, NULL, 0, '2026-03-12 15:40:02', '2026-03-27 10:35:12', 2);
INSERT INTO `lab_equipment_reserve` VALUES (19, 1, '测试设备1', 3, '李四', 1, '2026-03-11', '15:08:39', '15:59:41', '测试', 4, NULL, 0, '2026-03-12 16:08:50', '2026-03-27 10:35:14', 2);
INSERT INTO `lab_equipment_reserve` VALUES (20, 1, '测试设备1', 3, '李四', 1, '2026-03-11', '15:09:38', '16:09:38', NULL, 4, NULL, 0, '2026-03-12 16:09:46', '2026-03-27 10:35:16', 2);
INSERT INTO `lab_equipment_reserve` VALUES (21, 1, '计算机', 3, '李四', 1, '2026-03-27', '10:36:48', '11:31:48', '上课使用', 1, NULL, 1, '2026-03-27 10:35:46', '2026-03-27 15:22:03', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (22, 1, '笔记本电脑', 3, '李四', 1, '2026-03-27', '15:15:27', '16:15:27', '上课使用', 1, NULL, 1, '2026-03-27 15:21:34', '2026-03-27 18:04:41', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (23, 2, '电子分析天平', 3, '李四', 1, '2026-03-27', '15:23:46', '17:23:46', '测量重量', 1, NULL, 1, '2026-03-27 15:24:11', '2026-03-29 17:45:00', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (24, 1, '笔记本电脑', 3, '李四', 1, '2026-03-29', '17:42:01', '18:42:01', '课程使用', 1, NULL, 1, '2026-03-29 17:42:17', '2026-03-29 17:42:36', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (25, 1, '笔记本电脑', 3, '李四', 1, '2026-03-29', '17:49:59', '18:49:59', '上课学习', 1, NULL, 1, '2026-03-29 17:50:11', '2026-03-29 17:50:23', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (26, 1, '笔记本电脑', 3, '李四', 1, '2026-03-29', '18:00:44', '20:00:44', '上课使用', 1, NULL, 1, '2026-03-29 18:00:54', '2026-04-13 19:01:34', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (27, 1, '笔记本电脑', 3, '李四', 1, '2026-04-13', '19:00:59', '20:00:59', '上课使用', 1, NULL, 1, '2026-04-13 19:01:11', '2026-04-23 16:53:52', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (28, 1, '笔记本电脑', 3, '李四', 1, '2026-04-23', '11:22:01', '13:22:01', '上课使用', 2, NULL, 0, '2026-04-23 11:22:19', '2026-04-23 16:03:31', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (29, 2, '电子分析天平', 3, '李四', 1, '2026-04-23', '17:52:36', '18:51:36', NULL, 1, NULL, 1, '2026-04-23 17:51:47', '2026-04-23 17:51:56', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (30, 1, '笔记本电脑', 3, '李四', 2, '2026-04-25', '14:58:18', '16:58:18', '上课使用', 1, NULL, 1, '2026-04-25 14:58:45', '2026-04-25 14:59:24', NULL);
INSERT INTO `lab_equipment_reserve` VALUES (31, 1, '笔记本电脑', 3, '李四', 1, '2026-04-25', '15:00:12', '17:00:12', NULL, 1, NULL, 1, '2026-04-25 15:00:20', '2026-04-25 15:00:31', NULL);

-- ----------------------------
-- Table structure for lab_equipment_scrap
-- ----------------------------
DROP TABLE IF EXISTS `lab_equipment_scrap`;
CREATE TABLE `lab_equipment_scrap`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（报废记录唯一标识）',
  `equipment_id` bigint(20) NOT NULL COMMENT '关联设备类型ID（lab_equipment.id，同一类型可有多条报废）',
  `asset_id` bigint(20) NULL DEFAULT NULL COMMENT '关联设备实例ID（lab_equipment_asset.id）',
  `equipment_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称（冗余字段，用于前端展示）',
  `scrap_reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报废原因（如“超年限、高维修成本”）',
  `residual_value` decimal(10, 2) NOT NULL COMMENT '残值',
  `apply_user_id` bigint(20) NOT NULL COMMENT '申请人ID（管理员）',
  `apply_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '申请人姓名（冗余字段，用于前端展示）',
  `approval_user_id` bigint(20) NULL DEFAULT NULL COMMENT '审批人ID（上级管理员，可选）',
  `approval_user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审批人姓名（冗余字段，用于前端展示）',
  `approval_status` tinyint(4) NOT NULL DEFAULT 0 COMMENT '审批状态（0-待审批，1-通过，2-驳回）',
  `disposal_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处置方式（回收/销毁）',
  `disposal_time` datetime NULL DEFAULT NULL COMMENT '处置时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_asset_id`(`asset_id`) USING BTREE COMMENT '实例仅能有一条报废申请',
  INDEX `idx_apply_user_id`(`apply_user_id`) USING BTREE COMMENT '申请人ID索引（查询管理员提交的报废记录）',
  INDEX `idx_approval_status`(`approval_status`) USING BTREE COMMENT '审批状态索引（筛选待审批记录）',
  INDEX `fk_scrap_approval_user`(`approval_user_id`) USING BTREE,
  INDEX `idx_equipment_id`(`equipment_id`) USING BTREE COMMENT '设备类型ID索引',
  CONSTRAINT `fk_scrap_apply_user` FOREIGN KEY (`apply_user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_scrap_approval_user` FOREIGN KEY (`approval_user_id`) REFERENCES `sys_user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_scrap_asset` FOREIGN KEY (`asset_id`) REFERENCES `lab_equipment_asset` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_scrap_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `lab_equipment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '设备报废表：设备删除受限（需先驳回报废申请）；申请人删除时，关联记录同步删除；审批人删除时，审批人ID设为NULL' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_equipment_scrap
-- ----------------------------
INSERT INTO `lab_equipment_scrap` VALUES (1, 6, NULL, '工业平板电脑', '使用年限超8年，硬件老化严重，维修成本高于残值', 500.00, 1, '系统管理员', NULL, NULL, 1, '回收处理', '2025-11-28 16:53:59', '2025-11-13 16:53:59', '2026-03-07 17:47:10');
INSERT INTO `lab_equipment_scrap` VALUES (2, 3, NULL, NULL, '测试', 0.00, 4, NULL, 1, NULL, 1, '销毁', '2026-03-09 09:36:37', '2026-03-09 09:36:13', '2026-03-09 09:36:37');
INSERT INTO `lab_equipment_scrap` VALUES (3, 8, NULL, NULL, '测试500', 0.00, 4, NULL, 2, NULL, 1, '回收', '2026-03-09 11:00:51', '2026-03-09 11:00:34', '2026-03-09 11:00:52');
INSERT INTO `lab_equipment_scrap` VALUES (4, 1, NULL, NULL, '测试使用年限已到', 0.00, 4, NULL, 1, NULL, 1, '测试回收处理', '2026-03-11 17:14:42', '2026-03-11 16:41:42', '2026-03-11 17:14:39');
INSERT INTO `lab_equipment_scrap` VALUES (5, 9, NULL, NULL, '测试报废字段显示', 0.00, 4, NULL, 1, NULL, 1, '测试666', '2026-03-11 17:15:56', '2026-03-11 17:15:39', '2026-03-11 17:15:54');
INSERT INTO `lab_equipment_scrap` VALUES (10, 1, 47, '笔记本电脑', '无法修复', 0.00, 4, 'repairer01', 2, NULL, 1, '销毁', '2026-03-29 14:23:22', '2026-03-28 19:20:46', '2026-03-29 14:23:21');
INSERT INTO `lab_equipment_scrap` VALUES (15, 1, 48, '笔记本电脑', '修复成本大于实际价值', 500.00, 4, 'repairer01', NULL, NULL, 0, NULL, NULL, '2026-03-29 17:50:51', '2026-03-29 17:50:51');
INSERT INTO `lab_equipment_scrap` VALUES (16, 1, 50, '笔记本电脑', '无法修复', 500.00, 4, 'repairer01', NULL, NULL, 0, NULL, NULL, '2026-04-25 15:01:30', '2026-04-25 15:01:30');

-- ----------------------------
-- Table structure for lab_laboratory
-- ----------------------------
DROP TABLE IF EXISTS `lab_laboratory`;
CREATE TABLE `lab_laboratory`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（实验室唯一标识）',
  `lab_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实验室名称（如“物理实验室101”）',
  `lab_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '实验室位置（如“实验楼3层”）',
  `lab_manager` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实验室管理员姓名',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '实验室状态（0-停用，1-正常）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_lab_name`(`lab_name`) USING BTREE COMMENT '实验室名称唯一约束',
  INDEX `idx_lab_status`(`status`) USING BTREE COMMENT '实验室状态索引（筛选可用实验室）'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of lab_laboratory
-- ----------------------------
INSERT INTO `lab_laboratory` VALUES (1, '物理实验室101', '实验楼3层301室', '王老师', 1, '2025-12-28 16:53:59', '2025-12-28 16:53:59');
INSERT INTO `lab_laboratory` VALUES (2, '化学实验室202', '实验楼4层402室', '李老师', 1, '2025-12-28 16:53:59', '2026-04-23 21:32:38');
INSERT INTO `lab_laboratory` VALUES (3, '计算机实验室303', '实验楼5层503室', '赵老师', 1, '2025-12-28 16:53:59', '2025-12-28 16:53:59');
INSERT INTO `lab_laboratory` VALUES (4, '测试添加', '测试添加', '李老师', 1, '2026-03-03 22:43:47', '2026-03-03 22:43:47');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（权限唯一标识）',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称（如“设备信息录入”）',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码（如“equipment:add”）',
  `module_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属模块（如“设备信息管理”）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '权限创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_permission_code`(`permission_code`) USING BTREE COMMENT '权限编码唯一约束',
  INDEX `idx_module_name`(`module_name`) USING BTREE COMMENT '模块名称索引（权限筛选用）'
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '设备录入', 'equipment:add', '设备信息管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (2, '设备查询', 'equipment:query', '设备信息管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (3, '设备编辑', 'equipment:edit', '设备信息管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (4, '设备删除', 'equipment:delete', '设备信息管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (5, '说明书上传', 'equipment:uploadManual', '设备信息管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (6, '领用申请提交', 'apply:addBorrow', '领用与归还管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (7, '归还申请提交', 'apply:addReturn', '领用与归还管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (8, '申请审批', 'apply:approve', '领用与归还管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (9, '申请查询', 'apply:query', '领用与归还管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (10, '维护记录录入', 'maintain:addRecord', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (11, '维护周期设置', 'maintain:setCycle', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (12, '维修申请提交', 'maintain:addRepair', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (13, '维修进度更新', 'maintain:updateProgress', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (14, '维修成本记录', 'maintain:recordCost', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (15, '维护记录查询', 'maintain:query', '维护与维修管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (16, '报废申请发起', 'scrap:add', '报废管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (17, '报废审批', 'scrap:approve', '报废管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (18, '报废记录查询', 'scrap:query', '报废管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (19, '设备预约提交', 'reserve:add', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (20, '预约查询', 'reserve:query', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (21, '预约确认/取消', 'reserve:operate', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (22, '领用频次统计', 'stat:borrowCount', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (23, '设备使用率计算', 'stat:usageRate', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (24, '统计数据导出', 'stat:export', '预约与使用统计', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (25, '用户账号创建', 'user:add', '权限管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (26, '用户角色分配', 'user:assignRole', '权限管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (27, '角色权限配置', 'role:assignPerm', '权限管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (28, '密码批量重置', 'user:batchResetPwd', '权限管理', '2025-12-28 16:53:59');
INSERT INTO `sys_permission` VALUES (29, '用户查询', 'user:query', '权限管理', '2025-12-28 16:53:59');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（角色唯一标识）',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称（管理员/教师/学生）',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码（ADMIN/TEACHER/STUDENT）',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '角色创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code`) USING BTREE COMMENT '角色编码唯一约束'
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '系统管理员', 'ADMIN', '拥有系统所有操作权限，负责设备管理、审批、用户配置等', '2025-12-28 16:53:59');
INSERT INTO `sys_role` VALUES (2, '教师', 'TEACHER', '拥有设备查询、领用申请、维修申请、查看统计等权限', '2025-12-28 16:53:59');
INSERT INTO `sys_role` VALUES (3, '学生', 'STUDENT', '拥有设备查询、领用申请、公用设备预约等权限', '2025-12-28 16:53:59');
INSERT INTO `sys_role` VALUES (4, '维修师傅', 'REPAIR', '待维修接单、维修记录、发起报废申请等', '2026-03-06 15:40:33');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `role_id` bigint(20) NOT NULL COMMENT '关联角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称（冗余字段，用于前端展示）',
  `permission_id` bigint(20) NOT NULL COMMENT '关联权限ID',
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限名称（冗余字段，用于前端展示）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_permission`(`role_id`, `permission_id`) USING BTREE COMMENT '角色-权限组合唯一（避免重复关联）',
  INDEX `fk_role_perm_perm`(`permission_id`) USING BTREE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `sys_permission` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表（多对多）：角色删除时，关联记录同步删除；权限删除时，关联记录同步删除' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, '系统管理员', 16, '报废申请发起');
INSERT INTO `sys_role_permission` VALUES (2, 1, '系统管理员', 17, '报废审批');
INSERT INTO `sys_role_permission` VALUES (3, 1, '系统管理员', 18, '报废记录查询');
INSERT INTO `sys_role_permission` VALUES (4, 1, '系统管理员', 25, '用户账号创建');
INSERT INTO `sys_role_permission` VALUES (5, 1, '系统管理员', 26, '用户角色分配');
INSERT INTO `sys_role_permission` VALUES (6, 1, '系统管理员', 27, '角色权限配置');
INSERT INTO `sys_role_permission` VALUES (7, 1, '系统管理员', 28, '密码批量重置');
INSERT INTO `sys_role_permission` VALUES (8, 1, '系统管理员', 29, '用户查询');
INSERT INTO `sys_role_permission` VALUES (9, 1, '系统管理员', 10, '维护记录录入');
INSERT INTO `sys_role_permission` VALUES (10, 1, '系统管理员', 11, '维护周期设置');
INSERT INTO `sys_role_permission` VALUES (11, 1, '系统管理员', 12, '维修申请提交');
INSERT INTO `sys_role_permission` VALUES (12, 1, '系统管理员', 13, '维修进度更新');
INSERT INTO `sys_role_permission` VALUES (13, 1, '系统管理员', 14, '维修成本记录');
INSERT INTO `sys_role_permission` VALUES (14, 1, '系统管理员', 15, '维护记录查询');
INSERT INTO `sys_role_permission` VALUES (15, 1, '系统管理员', 1, '设备录入');
INSERT INTO `sys_role_permission` VALUES (16, 1, '系统管理员', 2, '设备查询');
INSERT INTO `sys_role_permission` VALUES (17, 1, '系统管理员', 3, '设备编辑');
INSERT INTO `sys_role_permission` VALUES (18, 1, '系统管理员', 4, '设备删除');
INSERT INTO `sys_role_permission` VALUES (19, 1, '系统管理员', 5, '说明书上传');
INSERT INTO `sys_role_permission` VALUES (20, 1, '系统管理员', 19, '设备预约提交');
INSERT INTO `sys_role_permission` VALUES (21, 1, '系统管理员', 20, '预约查询');
INSERT INTO `sys_role_permission` VALUES (22, 1, '系统管理员', 21, '预约确认/取消');
INSERT INTO `sys_role_permission` VALUES (23, 1, '系统管理员', 22, '领用频次统计');
INSERT INTO `sys_role_permission` VALUES (24, 1, '系统管理员', 23, '设备使用率计算');
INSERT INTO `sys_role_permission` VALUES (25, 1, '系统管理员', 24, '统计数据导出');
INSERT INTO `sys_role_permission` VALUES (26, 1, '系统管理员', 6, '领用申请提交');
INSERT INTO `sys_role_permission` VALUES (27, 1, '系统管理员', 7, '归还申请提交');
INSERT INTO `sys_role_permission` VALUES (28, 1, '系统管理员', 8, '申请审批');
INSERT INTO `sys_role_permission` VALUES (29, 1, '系统管理员', 9, '申请查询');
INSERT INTO `sys_role_permission` VALUES (32, 2, '教师', 2, '设备查询');
INSERT INTO `sys_role_permission` VALUES (33, 2, '教师', 5, '说明书上传');
INSERT INTO `sys_role_permission` VALUES (34, 2, '教师', 6, '领用申请提交');
INSERT INTO `sys_role_permission` VALUES (35, 2, '教师', 7, '归还申请提交');
INSERT INTO `sys_role_permission` VALUES (36, 2, '教师', 8, '申请审批');
INSERT INTO `sys_role_permission` VALUES (37, 2, '教师', 11, '维护周期设置');
INSERT INTO `sys_role_permission` VALUES (38, 2, '教师', 12, '维修申请提交');
INSERT INTO `sys_role_permission` VALUES (39, 2, '教师', 15, '维护记录查询');
INSERT INTO `sys_role_permission` VALUES (40, 2, '教师', 18, '报废记录查询');
INSERT INTO `sys_role_permission` VALUES (41, 2, '教师', 19, '设备预约提交');
INSERT INTO `sys_role_permission` VALUES (42, 2, '教师', 20, '预约查询');
INSERT INTO `sys_role_permission` VALUES (43, 2, '教师', 21, '预约确认/取消');
INSERT INTO `sys_role_permission` VALUES (44, 2, '教师', 22, '领用频次统计');
INSERT INTO `sys_role_permission` VALUES (45, 3, '学生', 2, '设备查询');
INSERT INTO `sys_role_permission` VALUES (46, 3, '学生', 6, '领用申请提交');
INSERT INTO `sys_role_permission` VALUES (47, 3, '学生', 7, '归还申请提交');
INSERT INTO `sys_role_permission` VALUES (48, 3, '学生', 8, '申请审批');
INSERT INTO `sys_role_permission` VALUES (49, 3, '学生', 12, '维修申请提交');
INSERT INTO `sys_role_permission` VALUES (50, 3, '学生', 15, '维护记录查询');
INSERT INTO `sys_role_permission` VALUES (51, 3, '学生', 18, '报废记录查询');
INSERT INTO `sys_role_permission` VALUES (52, 3, '学生', 19, '设备预约提交');
INSERT INTO `sys_role_permission` VALUES (53, 3, '学生', 20, '预约查询');
INSERT INTO `sys_role_permission` VALUES (54, 4, '维修师傅', 10, '维护记录录入');
INSERT INTO `sys_role_permission` VALUES (55, 4, '维修师傅', 11, '维护周期设置');
INSERT INTO `sys_role_permission` VALUES (56, 4, '维修师傅', 12, '维修申请提交');
INSERT INTO `sys_role_permission` VALUES (57, 4, '维修师傅', 13, '维修进度更新');
INSERT INTO `sys_role_permission` VALUES (58, 4, '维修师傅', 14, '维修成本记录');
INSERT INTO `sys_role_permission` VALUES (59, 4, '维修师傅', 15, '维护记录查询');
INSERT INTO `sys_role_permission` VALUES (60, 4, '维修师傅', 16, '报废申请发起');
INSERT INTO `sys_role_permission` VALUES (61, 4, '维修师傅', 18, '报废记录查询');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID（用户唯一标识）',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名（学号/工号）',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密后的密码（BCrypt算法）',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名（学生/教师姓名）',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号（用于密码重置验证）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱（可选）',
  `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '账号状态（0-禁用，1-正常）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE COMMENT '用户名唯一约束',
  INDEX `idx_phone`(`phone`) USING BTREE COMMENT '手机号索引（密码重置用）'
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '123456', '系统管理员', '13800138000', 'admin@lab.com', 1, '2025-12-28 16:53:59', '2026-03-04 23:54:22');
INSERT INTO `sys_user` VALUES (2, 'teacher01', '123456', '张老师', '13900139001', 'teacher01@lab.com', 1, '2025-12-28 16:53:59', '2026-03-02 23:44:42');
INSERT INTO `sys_user` VALUES (3, 'student01', '123456', '李四', '13730017002', '3796796581@qq.com', 1, '2025-12-28 16:53:59', '2026-03-27 17:31:09');
INSERT INTO `sys_user` VALUES (4, 'repairer01', '123456', '维修员', '19711085124', 'repairer@lab.com', 1, '2026-03-02 02:17:40', '2026-03-06 15:41:50');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户姓名（冗余字段，用于前端展示）',
  `role_id` bigint(20) NOT NULL COMMENT '关联角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色名称（冗余字段，用于前端展示）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_role`(`user_id`, `role_id`) USING BTREE COMMENT '用户-角色组合唯一（避免重复关联）',
  INDEX `fk_user_role_role`(`role_id`) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表（多对多）：用户删除时，关联记录同步删除；角色删除时，关联记录同步删除' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, '系统管理员', 1, '系统管理员');
INSERT INTO `sys_user_role` VALUES (2, 2, '张老师', 2, '教师');
INSERT INTO `sys_user_role` VALUES (5, 4, '维修员', 4, '维修师傅');
INSERT INTO `sys_user_role` VALUES (10, 3, '李四', 3, '学生');

SET FOREIGN_KEY_CHECKS = 1;
